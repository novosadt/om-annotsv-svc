/*
 * Copyright (C) 2021  Tomas Novosad
 * VSB-TUO, Faculty of Electrical Engineering and Computer Science
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package cz.vsb.genetics.svc.annotsv;

import cz.vsb.genetics.svc.common.Chromosome;
import cz.vsb.genetics.svc.common.StructuralVariant;
import cz.vsb.genetics.svc.common.SvType;
import cz.vsb.genetics.svc.parser.SvResultParserBase;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotSvTsvParser extends SvResultParserBase {
    private final Pattern chromLocPatternWithChr = Pattern.compile("(chr\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");
    private final Pattern chromLocPatternWithoutChr = Pattern.compile("(\\d+|MT|M|T|mt|m|t|X|Y|x|y):(\\d+)");

    @Override
    public void parseResultFile(String file, String delim) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        //skip header line
        reader.readLine();

        String line;
        while((line = reader.readLine()) != null) {
            if (StringUtils.isBlank(line))
                continue;

            parseLine(line, delim);
        }

        reader.close();
    }

    @Override
    public void printStructuralVariantStats() {
        printStructuralVariantStats("AnnotSV");
    }

    private void parseLine(String line, String delim) throws Exception {
        String[] values = line.split(delim);

        addStructuralVariant(values);
    }

    private void addStructuralVariant(String[] values) throws Exception {
        String srcChromId = "ch" + values[1];
        String dstChromId = srcChromId;
        Long srcLoc = new Long(values[2]);
        Long dstLoc = new Long(values[3]);
        Long svLength = StringUtils.isBlank(values[4]) ? 0 : Math.abs(new Long(values[4]));
        String svType = values[5].toLowerCase();
        Double svFreq = getSvFreq(values[14]);
        String annotSvType = StringUtils.isBlank(values[15]) ? "" : values[15].toLowerCase();
        String gene = values[16];


        if (!annotSvType.equals("full"))
            return;

        if (svType.equals("bnd")) {
            String chromLoc = values[9];
            Matcher m = chromLocPatternWithChr.matcher(chromLoc);
            if (!m.find()) {
                m = chromLocPatternWithoutChr.matcher(chromLoc);
                if (!m.find())
                    throw new Exception("Unsupported chromosome:location format: " + chromLoc);
            }

            dstChromId = m.group(1);
            dstLoc = new Long(m.group(2));
        }

        if (svType.equals("ins"))
            dstLoc = srcLoc + svLength;

        Chromosome srcChrom = Chromosome.getChromosome(srcChromId);
        Chromosome dstChrom = Chromosome.getChromosome(dstChromId);

        StructuralVariant sv = new StructuralVariant(srcChrom, srcLoc, dstChrom, dstLoc, svLength, gene, svFreq, null);

        switch (svType) {
            case "bnd" : addStructuralVariant(sv, translocations, SvType.BND); break;
            case "cnv" : addStructuralVariant(sv, copyNumberVariations, SvType.CNV); break;
            case "del" : addStructuralVariant(sv, deletions, SvType.DEL); break;
            case "ins" : addStructuralVariant(sv, insertions, SvType.INS); break;
            case "dup" : addStructuralVariant(sv, duplications, SvType.DUP); break;
            case "inv" : addStructuralVariant(sv, inversions, SvType.INV); break;
            default: addStructuralVariant(sv, unknown, SvType.UNK);
        }
    }

    private Double getSvFreq(String frequencies) {
        String[] values = frequencies.split(":");

        if (values.length < 2)
            return null;

        String[] referenceAlternate = values[1].split(",");

        try {
            double reference = new Double(referenceAlternate[0]);
            double alternate = new Double(referenceAlternate[1]);

            return alternate / (reference + alternate) * 100.0;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}

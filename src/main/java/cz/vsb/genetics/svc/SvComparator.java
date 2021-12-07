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

package cz.vsb.genetics.svc;

import cz.vsb.genetics.sv.StructuralVariant;
import cz.vsb.genetics.sv.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParser;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.util.*;

public class SvComparator {
    private FileWriter fileWriter;
    private boolean reportOnlyCommonGenes = false;
    private Long variantDistance = null;
    private Set<StructuralVariantType> svTypes;
    private String svLabel1;
    private String svLabel2;

    public void compareStructuralVariants(SvResultParser svParser1, String svLabel1, SvResultParser svParser2, String svLabel2,
                                          String outputFile, boolean reportOnlyCommonGeneVariants, Long variantDistance, Set<StructuralVariantType> svTypes) throws Exception {
        fileWriter = new FileWriter(outputFile);
        this.reportOnlyCommonGenes = reportOnlyCommonGeneVariants;
        this.variantDistance = variantDistance;
        this.svTypes = svTypes;
        this.svLabel1 = svLabel1;
        this.svLabel2 = svLabel2;

        printHeader(svLabel1, svLabel2);

        processStructuralVariants(svParser1.getTranslocations(), svParser2.getTranslocations(), StructuralVariantType.BND);
        processStructuralVariants(svParser1.getInversions(), svParser2.getInversions(),  StructuralVariantType.INV);
        processStructuralVariants(svParser1.getDuplications(), svParser2.getDuplications(),  StructuralVariantType.DUP);
        processStructuralVariants(svParser1.getDeletions(), svParser2.getDeletions(),  StructuralVariantType.DEL);
        processStructuralVariants(svParser1.getInsertions(), svParser2.getInsertions(),  StructuralVariantType.INS);
        processStructuralVariants(svParser1.getUnknown(), svParser2.getUnknown(),  StructuralVariantType.UNK);

        fileWriter.close();
    }

    private void processStructuralVariants(List<StructuralVariant> structuralVariants1,
            List<StructuralVariant> structuralVariants2, StructuralVariantType svType) throws Exception{
        Set<StructuralVariant> processedVariants = new HashSet<>();

        if (svTypes != null && !svTypes.contains(svType))
            return;

        int similarVariantsCount = 0;

        for (StructuralVariant structuralVariant : structuralVariants1) {
            if (!processedVariants.contains(structuralVariant))
                processedVariants.add(structuralVariant);
            else
                continue;

            List<StructuralVariant> similarVariants = findNearestStructuralVariants(structuralVariant,  structuralVariants2);

            if (similarVariants.size() == 0)
                continue;

            printSimilarTranslocations(structuralVariant, similarVariants, svType);
            similarVariantsCount++;
        }

        double percentage = structuralVariants1.size() == 0 ? 0.0 :
                (double)similarVariantsCount / (double)structuralVariants1.size() * 100.0;

        System.out.printf("Common SV (%s with %s / %s) - %s:\t%d/%d (%.02f%%)%n", svLabel1, svLabel2, svLabel1, svType.toString(),
                similarVariantsCount, structuralVariants1.size(), percentage);
    }

    private List<StructuralVariant> findNearestStructuralVariants(StructuralVariant structuralVariant,
            List<StructuralVariant> structuralVariants) {
        Map<Long, StructuralVariant> similarStructuralVariants = new TreeMap<>();

        for (StructuralVariant otherVariant : structuralVariants) {
            if (!(structuralVariant.getSrcChromosome() == otherVariant.getSrcChromosome() &&
                    structuralVariant.getDstChromosome() == otherVariant.getDstChromosome()))
                continue;

            Long srcDist = Math.abs(structuralVariant.getSrcLoc() - otherVariant.getSrcLoc());
            Long dstDist = Math.abs(structuralVariant.getDstLoc() - otherVariant.getDstLoc());
            Long distSum = srcDist + dstDist;

            if (reportOnlyCommonGenes) {
                List<String> commonGenes = getCommonGenes(structuralVariant, otherVariant);
                if (commonGenes.size() < 1)
                    continue;
            }

            if (variantDistance != null && distSum > variantDistance)
                continue;

            similarStructuralVariants.put(distSum, otherVariant);
        }

        return new ArrayList<>(similarStructuralVariants.values());
    }

    private void printHeader(String svLabel1, String svLabel2) throws Exception {
        fileWriter.write("sv_type\t" +
                "src_chr\t" +
                "dst_chr\t" +
                svLabel1 + "_src_pos\t" +
                svLabel1 + "_dst_pos\t" +
                svLabel2 + "_src_pos\t" +
                svLabel2 + "_dst_pos\t" +
                "src_pos_dist\t" +
                "dst_pos_dist\t" +
                "dist_var\t" +
                svLabel1 + "_sv_size\t" +
                svLabel2 + "_sv_size\t" +
                svLabel1 + "_gene\t" +
                svLabel2 + "_gene\t" +
                "common_genes\n"
        );
    }

    private void printSimilarTranslocations(StructuralVariant structuralVariant,
                                            List<StructuralVariant> structuralVariants, StructuralVariantType svType) throws Exception {
        StructuralVariant similarStructuralVariant = structuralVariants.get(0);

        Long srcDist = Math.abs(structuralVariant.getSrcLoc() - similarStructuralVariant.getSrcLoc());
        Long dstDist = Math.abs(structuralVariant.getDstLoc() - similarStructuralVariant.getDstLoc());

        String commonGenes = StringUtils.join(getCommonGenes(structuralVariant, similarStructuralVariant), ",");

        String line = String.format("%s\t%s\t%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\t%s\t%s\n",
                svType.toString(),
                structuralVariant.getSrcChromosome().toString(),
                structuralVariant.getDstChromosome().toString(),
                structuralVariant.getSrcLoc(),
                structuralVariant.getDstLoc(),
                similarStructuralVariant.getSrcLoc(),
                similarStructuralVariant.getDstLoc(),
                srcDist,
                dstDist,
                srcDist + dstDist,
                structuralVariant.getSize(),
                similarStructuralVariant.getSize(),
                structuralVariant.getGene(),
                similarStructuralVariant.getGene(),
                commonGenes);

        fileWriter.write(line);
    }

    private List<String> getCommonGenes(StructuralVariant sv1, StructuralVariant sv2) {
        if (StringUtils.isBlank(sv1.getGene()) || StringUtils.isBlank(sv2.getGene()))
            return Collections.emptyList();

        String[] sv1Genes = sv1.getGene().toUpperCase().split("[/;]");
        List<String> sv2Genes = Arrays.asList(sv2.getGene().toUpperCase().split("[/;]"));

        List<String> commonGenes = new ArrayList<>();

        for (String gene : sv1Genes) {
            if (sv2Genes.contains(gene))
                commonGenes.add(gene);
        }

        return commonGenes;
    }
}

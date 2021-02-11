/*
 * om-annotsv-svc: Optical Mapping and AnnotSV Structural Variant Comparator
 *
 * Application for comparison of structural variants found by optical mapping technology (Bionano Genomics)
 * with AnnotSV analysis of 3rd generation sequencing technologies 10xGenomics, Oxford Nanopore Technologies and Pacbio.
 *
 *
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


package cz.vsb.genetics.svc.main;

import cz.vsb.genetics.svc.SvComparator;
import cz.vsb.genetics.svc.common.SvType;
import cz.vsb.genetics.svc.annotsv.AnnotSvTsvParser;
import cz.vsb.genetics.svc.bionano.BionanoPipelineResultParser;
import cz.vsb.genetics.svc.parser.SvResultParser;
import jdk.nashorn.internal.runtime.ECMAException;
import org.apache.commons.cli.*;

import java.util.Properties;
import java.util.Set;

public class BionanoAnnotSvComparator {
    private static final String VERSION = "1.1.2";

    private static final String ARG_BIONANO_INPUT = "bionano_input";
    private static final String ARG_ANNOTSV_INPUT = "annotsv_input";
    private static final String ARG_GENE_INTERSECTION = "gene_intersection";
    private static final String ARG_VARIANT_DISTANCE = "variant_distance";
    private static final String ARG_VARIANT_TYPE = "variant_type";
    private static final String ARG_OUTPUT = "output";

    public static void main(String[] args) {
        CommandLine cmd = getCommandLine(args);

        try {
            boolean onlyCommonGeneVariants = cmd.hasOption(ARG_GENE_INTERSECTION);
            Long variantDistance = cmd.hasOption(ARG_VARIANT_DISTANCE) ? new Long(cmd.getOptionValue(ARG_VARIANT_DISTANCE)) : null;
            Set<SvType> variantType = cmd.hasOption(ARG_VARIANT_TYPE) ? SvType.getSvTypes(cmd.getOptionValue(ARG_VARIANT_TYPE)) : null;

            SvResultParser bionanoParser = new BionanoPipelineResultParser();
            bionanoParser.setRemoveDuplicateVariants(true);
            bionanoParser.parseResultFile(cmd.getOptionValue(ARG_BIONANO_INPUT), "[,\t]");

            SvResultParser annotsvParser = new AnnotSvTsvParser();
            annotsvParser.setRemoveDuplicateVariants(true);
            annotsvParser.parseResultFile(cmd.getOptionValue(ARG_ANNOTSV_INPUT), "\t");

            SvComparator svComparator = new SvComparator();
            svComparator.compareStructuralVariants(bionanoParser, "bionano", annotsvParser,
                "annotsv", cmd.getOptionValue(ARG_OUTPUT), onlyCommonGeneVariants, variantDistance, variantType);

            bionanoParser.printStructuralVariantStats();
            annotsvParser.printStructuralVariantStats();
        }
        catch (Exception e) {
            System.out.println("Error occurred:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static CommandLine getCommandLine(String[] args) {
        Options options = new Options();

        Option bionanoInput = new Option("b", ARG_BIONANO_INPUT, true, "bionano pipeline result file path (smap)");
        bionanoInput.setRequired(true);
        bionanoInput.setType(String.class);
        options.addOption(bionanoInput);

        Option annotsvInput = new Option("a", ARG_ANNOTSV_INPUT, true, "annotsv tsv file path");
        annotsvInput.setRequired(true);
        annotsvInput.setType(String.class);
        options.addOption(annotsvInput);

        Option geneIntersection = new Option("g", ARG_GENE_INTERSECTION, false, "select only variants with common genes (default false)");
        geneIntersection.setRequired(false);
        options.addOption(geneIntersection);

        Option variantDistance = new Option("d", ARG_VARIANT_DISTANCE, true, "variants distance sum (start distance + end distance) filter");
        variantDistance.setType(Long.class);
        variantDistance.setRequired(false);
        options.addOption(variantDistance);

        Option variantType = new Option("t", ARG_VARIANT_TYPE, true, "variant type filter, any combination of [BND,CNV,DEL,INS,DUP,INV,UNK]");
        variantType.setType(String.class);
        variantType.setRequired(false);
        options.addOption(variantType);

        Option output = new Option("o", ARG_OUTPUT, true, "output result file");
        output.setRequired(true);
        output.setType(String.class);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("\nSVC - Bionano Genomics (OM) and AnnotSV (ONT, 10x, PacBio) Structural Variant Comparator, v" + BionanoAnnotSvComparator.version() + "\n");
            System.out.println(e.getMessage());
            System.out.println();
            formatter.printHelp(
                    300,
                    "\njava -jar bionano-annotsv-svc.jar " +
                            "-b <bionano pipeline result file> " +
                            "-a <annotsv tsv file> " +
                            "-o <result.csv> " +
                            "[-g] " +
                            "[-d <Integer>] " +
                            "[-t <comma separated list>]",
                    "\noptions:"                    ,
                    options,
                    "\nTomas Novosad, VSB-TU Ostrava, 2021" +
                          "\nFEI, Department of Computer Science" +
                          "\nVersion: " + BionanoAnnotSvComparator.version() +
                          "\nLicense: GPL-3.0-only ");

            System.exit(1);
        }

        return cmd;
    }

    public static String version() {
        final Properties properties = new Properties();

        try {
            properties.load(BionanoAnnotSvComparator.class.getClassLoader().getResourceAsStream("project.properties"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return properties.getProperty("version");
    }

}

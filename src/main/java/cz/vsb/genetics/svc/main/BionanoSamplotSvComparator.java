package cz.vsb.genetics.svc.main;

import cz.vsb.genetics.ngs.sv.SamplotBionanoVariantGenerator;
import cz.vsb.genetics.om.sv.BionanoPipelineResultParser;
import cz.vsb.genetics.sv.StructuralVariantType;
import cz.vsb.genetics.sv.SvResultParser;
import org.apache.commons.cli.*;

import java.util.Properties;
import java.util.Set;

public class BionanoSamplotSvComparator {
    private static final String ARG_BIONANO_INPUT = "bionano_input";
    private static final String ARG_SAMPLOT_GENERATE_VARIANTS = "samplot_generate_variants";
    private static final String ARG_SAMPLOT_VARIANTS = "samplot_variants";
    private static final String ARG_SAMPLOT_CMD_BASE = "samplot_command_base";
    private static final String ARG_SAMPLOT_WORKDIR = "samplot_workdir";
    private static final String ARG_VARIANT_TYPE = "variant_type";
    private static final String ARG_VARIANT_DISTANCE = "variant_distance";
    private static final String ARG_FORKS = "forks";
    private static final String ARG_OUTPUT = "output";

    public static void main(String[] args) {
        CommandLine cmd = getCommandLine(args);

        try {
            boolean generateSamplotVariants = cmd.hasOption(ARG_SAMPLOT_GENERATE_VARIANTS);
            Long variantDistance = cmd.hasOption(ARG_VARIANT_DISTANCE) ? new Long(cmd.getOptionValue(ARG_VARIANT_DISTANCE)) : null;
            Set<StructuralVariantType> variantType = cmd.hasOption(ARG_VARIANT_TYPE) ? StructuralVariantType.getSvTypes(cmd.getOptionValue(ARG_VARIANT_TYPE)) : null;

            SvResultParser bionanoParser = new BionanoPipelineResultParser();
            bionanoParser.setRemoveDuplicateVariants(true);
            bionanoParser.parseResultFile(cmd.getOptionValue(ARG_BIONANO_INPUT), "[,\t]");

            if (generateSamplotVariants) {
                int forks = cmd.hasOption(ARG_FORKS) ? new Integer(cmd.getOptionValue(ARG_FORKS)) : 1;

                String workdir = cmd.getOptionValue(ARG_SAMPLOT_WORKDIR);
                workdir = workdir.replaceAll("/$", "");
                workdir = workdir.replaceAll("\\$", "");

                SamplotBionanoVariantGenerator variantGenerator = new SamplotBionanoVariantGenerator();
                variantGenerator.generate(
                        bionanoParser,
                        cmd.getOptionValue(ARG_SAMPLOT_VARIANTS),
                        cmd.getOptionValue(ARG_SAMPLOT_CMD_BASE),
                        workdir,
                        forks);
            }

        }
        catch (Exception e) {
            System.out.println("Error occurred:");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static CommandLine getCommandLine(String[] args) {
        Options options = new Options();

        Option bionanoInput = new Option("b", ARG_BIONANO_INPUT, true, "bionano pipeline result file path (smap)");
        bionanoInput.setRequired(true);
        bionanoInput.setArgName("smap file");
        bionanoInput.setType(String.class);
        options.addOption(bionanoInput);

        Option samplotVariants = new Option("s", ARG_SAMPLOT_VARIANTS, true, "samplot csv variants file path");
        samplotVariants.setRequired(true);
        samplotVariants.setArgName("csv file");
        samplotVariants.setType(String.class);
        options.addOption(samplotVariants);

        Option samplotCmdBase = new Option("c", ARG_SAMPLOT_CMD_BASE, true, "samplot base command (e.g. python3 /home/samplot/samplot.py plot -b /home/samplot/bam/sample.bam");
        samplotCmdBase.setRequired(true);
        samplotCmdBase.setArgName("csv file");
        samplotCmdBase.setType(String.class);
        options.addOption(samplotCmdBase);

        Option samplotWorkdir = new Option("w", ARG_SAMPLOT_WORKDIR, true, "samplot work directory");
        samplotWorkdir.setRequired(true);
        samplotWorkdir.setArgName("directory");
        samplotWorkdir.setType(String.class);
        options.addOption(samplotWorkdir);

        Option samplotGenerateVariants = new Option("gv", ARG_SAMPLOT_GENERATE_VARIANTS, false, "generate samplot variants from Bionano smap file");
        samplotGenerateVariants.setRequired(false);
        options.addOption(samplotGenerateVariants);

        Option variantType = new Option("t", ARG_VARIANT_TYPE, true, "variant type filter, any combination of [BND,CNV,DEL,INS,DUP,INV,UNK], comma separated");
        variantType.setType(String.class);
        variantType.setArgName("sv types");
        variantType.setRequired(false);
        options.addOption(variantType);

        Option variantDistance = new Option("d", ARG_VARIANT_DISTANCE, true, "distance variance filter - number of bases difference between variant from NGS and OM");
        variantDistance.setType(Long.class);
        variantDistance.setArgName("number");
        variantDistance.setRequired(false);
        options.addOption(variantDistance);

        Option forks = new Option("f", ARG_FORKS, true, "number of samplot process forks");
        forks.setType(Integer.class);
        forks.setArgName("number");
        forks.setRequired(false);
        options.addOption(forks);

        Option output = new Option("o", ARG_OUTPUT, true, "output result file");
        output.setRequired(true);
        output.setArgName("csv file");
        output.setType(String.class);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("\nSVC - Bionano Genomics (OM) and SAMPlot Structural Variant Comparator, v" + BionanoSamplotSvComparator.version() + "\n");
            System.out.println(e.getMessage());
            System.out.println();
            formatter.printHelp(
                    300,
                    "\njava -jar om-samplot-svc.jar ",
                    "\noptions:",
                    options,
                    "\nTomas Novosad, VSB-TU Ostrava, 2022" +
                            "\nFEI, Department of Computer Science" +
                            "\nVersion: " + version() +
                            "\nLicense: GPL-3.0-only ");

            System.exit(1);
        }

        return cmd;
    }

    private static String version() {
        final Properties properties = new Properties();

        try {
            properties.load(BionanoSamplotSvComparator.class.getClassLoader().getResourceAsStream("project.properties"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return properties.getProperty("version");
    }
}

package info.lynxnet.crossword;

import org.apache.commons.cli.*;

import java.util.Arrays;

public class Settings {
    private int maxPermSetSize = Constants.MAX_PERM_SET_SIZE;
    private boolean rankByScores = true;
    private Class<? extends PlacementSequenceGenerator> placementSequenceGeneratorClass = Constants.PLACEMENT_GENERATOR_CLASS;
    private String fileName = Constants.DEFAULT_FILE_NAME;
    private int boardSize = Constants.DEFAULT_BOARD_SIZE;
    private String builderName = Constants.DEFAULT_BUILDER_TYPE.name();
    private int[] weights = Constants.DEFAULT_WEIGHTS;

    private static Settings instance = new Settings();

    public static Settings getInstance() {
        return instance;
    }

    private static CommandLineParser parserInstance = new DefaultParser();

    private static CommandLineParser getParser() {
        return parserInstance;
    }

    private static Options options = new Options();

    static {
        options.addOption(Option.builder("f")
                .hasArg(true)
                .desc("File with words to be used in the crossword puzzle")
                .longOpt("file")
                .type(String.class)
                .build());
        options.addOption(Option.builder("s")
                .hasArg(true)
                .longOpt("boardSize")
                .type(Integer.TYPE)
                .desc("Size of the crossword puzzle board")
                .build());
        options.addOption(Option.builder("b")
                .hasArg(true)
                .longOpt("builder")
                .type(String.class)
                .desc("The builder implementation [DEFAULT|PARALLEL|TREESOME]")
                .build());
        options.addOption(Option.builder("w")
                .hasArg(true)
                .numberOfArgs(4)
                .valueSeparator(',')
                .longOpt("weights")
                .type(Integer.class)
                .desc("Weights A,B,C,D for the scoring function:" +
                        " board filling, rows/columns filling, symmetry and crossings scores")
                .build());
        options.addOption(Option.builder("r")
                .hasArg(true)
                .longOpt("rankByScores")
                .desc("Switch on / off ordering of lists of row permutations for each step by puzzle scores")
                .type(Boolean.class)
                .build());
        options.addOption(Option.builder("m")
                .hasArg(true)
                .longOpt("maxPermSetSize")
                .type(Integer.TYPE)
                .desc("Maximum size of row permitation list for each step")
                .build());
    }

    public static Settings configure(String[] args, boolean printUsage) {
        CommandLineParser parser = getParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("s")) {
                instance.boardSize = Integer.parseInt(line.getOptionValue("s"));
            }
            if (line.hasOption("b")) {
                instance.builderName = line.getOptionValue("b");
            }
            if (line.hasOption("f")) {
                instance.fileName = line.getOptionValue("f");
            }
            if (line.hasOption("w")) {
                String[] strWeights = line.getOptionValues("w");
                String weithsErrorMessage = "The value of the weights parameter should be a comma-delimited list of four integer numbers";
                if (strWeights.length != 4) {
                    throw new ParseException(weithsErrorMessage);
                }
                int[] newWeights = new int[4];
                for (int i = 0; i < strWeights.length; i++) {
                    try {
                        newWeights[i] = Integer.parseInt(strWeights[i]);
                    } catch (NumberFormatException e) {
                        throw new ParseException(weithsErrorMessage);
                    }
                }
                instance.weights = newWeights;
            }
            if (line.hasOption("r")) {
                instance.rankByScores = Boolean.parseBoolean(line.getOptionValue("r"));
            }
            if (line.hasOption("m")) {
                instance.maxPermSetSize = Integer.parseInt(line.getOptionValue("m"));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            if (!printUsage) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Usage", options);
            }
        }

        if (printUsage) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Usage", options);
        }
        return instance;
    }

    private Settings() {
    }

    public int getMaxPermSetSize() {
        return maxPermSetSize;
    }

    public boolean isRankByScores() {
        return rankByScores;
    }

    public Class<? extends PlacementSequenceGenerator> getPlacementSequenceGeneratorClass() {
        return placementSequenceGeneratorClass;
    }

    public String getFileName() {
        return fileName;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public String getBuilderName() {
        return builderName;
    }

    public int[] getWeights() {
        return weights;
    }

    @Override
    public String toString() {
        return "maxPermSetSize=" + maxPermSetSize +
                "\nrankByScores=" + rankByScores +
                "\nplacementSequenceGeneratorClass=" + placementSequenceGeneratorClass +
                "\nfileName='" + fileName + '\'' +
                "\nboardSize=" + boardSize +
                "\nbuilderName='" + builderName + '\'' +
                "\nweights=" + Arrays.toString(weights);
    }
}

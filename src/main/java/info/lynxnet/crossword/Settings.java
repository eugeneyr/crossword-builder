package info.lynxnet.crossword;

public class Settings {
    private int maxPermSetSize = Constants.MAX_PERM_SET_SIZE;
    private boolean rankByScores = true;
    private Class<? extends PlacementSequenceGenerator> placementSequenceGeneratorClass = Constants.PLACEMENT_GENERATOR_CLASS;
    private String fileName = Constants.DEFAULT_FILE_NAME;
    private int boardSize = Constants.DEFAULT_BOARD_SIZE;
    private String builderName = Constants.DEFAULT_BUILDER_NAME;
    private int[] weights = Constants.DEFAULT_WEIGHTS;

    private static Settings ourInstance = new Settings();

    public static Settings getInstance() {
        return ourInstance;
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

    public static void readSettingsFromCommandLine(String[] args) {

    }
}

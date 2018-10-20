package info.lynxnet.crossword;

import info.lynxnet.crossword.krisskross.BeautifulKrissKrossCrossword;

public enum BuilderType {
    DEFAULT(BeautifulCrossword.class),
    PARALLEL(ParallelBeautifulCrossword.class),
    TREESOME(BeautifulKrissKrossCrossword.class);

    BuilderType(Class<? extends BeautifulCrossword> builderClass) {
        this.builderClass = builderClass;
    }

    public BeautifulCrossword createBuilder() {
        try {
            return builderClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<? extends BeautifulCrossword> builderClass;

    public static BeautifulCrossword getBuilder(String name) {
        return BuilderType.valueOf(name).createBuilder();
    }
}

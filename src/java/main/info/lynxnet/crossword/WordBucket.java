package info.lynxnet.crossword;

import java.util.*;

public class WordBucket {
    private Set<String> words = new HashSet<String>();
    private Map<Character, Map<Integer, Set<String>>> charsToPositions =
            new HashMap<Character, Map<Integer, Set<String>>>();

    public WordBucket() {
    }

    public Map<Character, Map<Integer, Set<String>>> getCharsToPositions() {
        return charsToPositions;
    }

    public void setCharsToPositions(TreeMap<Character, Map<Integer, Set<String>>> charsToPositions) {
        this.charsToPositions = charsToPositions;
    }

    public void addWord(String word) {
        if (word == null) {
            throw new IllegalArgumentException("The word is null");
        }
        words.add(word);
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Map<Integer, Set<String>> positionMap = charsToPositions.get(c);
            if (positionMap == null) {
                positionMap = new TreeMap<Integer, Set<String>>();
                charsToPositions.put(c, positionMap);
            }
            Set<String> strings = positionMap.get(i);
            if (strings == null) {
                strings = new TreeSet<String>();
                positionMap.put(i, strings);
            }
            strings.add(word);
        }
    }

    public Set<String> getWords(char c, int i) {
        if (c == Constants.EMPTY_CELL_FILLER) {
            return Collections.unmodifiableSet(words);
        }
        Map<Integer, Set<String>> positionMap = charsToPositions.get(c);
        if (positionMap != null) {
            Set<String> strings = positionMap.get(i);
            if (strings != null) {
                return Collections.unmodifiableSet(strings);
            }
        }
        return Collections.EMPTY_SET;
    }

    public Set<String> getWords(char c) {
        Map<Integer, Set<String>> positionMap = charsToPositions.get(c);
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        if (positionMap != null) {
            for (Map.Entry<Integer, Set<String>> entry : positionMap.entrySet()) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    public Set<String> getWords() {
        return Collections.unmodifiableSet(words);
    }

    public Set<String> getWordsByPattern(String pattern) {
        int length = pattern.length();
        Set<String> result = new HashSet<>(words);
        for (int i = 0; i < length; i++) {
            if (!Character.isAlphabetic(pattern.charAt(i))) {
                continue;
            }
            Set<String> words = getWords(pattern.charAt(i), i);
            result.retainAll(words);
        }
        return result;
    }
}

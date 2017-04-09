package info.lynxnet.crossword;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class WordStore {
    protected Map<Integer, WordBucket> bucketsByWordLength = new HashMap<>();
    protected Set<String> words = new TreeSet<String>();

    public WordBucket getBucket(int wordLength) {
        bucketsByWordLength.putIfAbsent(wordLength, new WordBucket());
        return bucketsByWordLength.get(wordLength);
    }

    public WordStore(String[] dictionary) {
        this.load(dictionary);
    }

    public WordStore(String fileName) {
        this.load(fileName);
    }

    public void load(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String s;
            do {
                s = reader.readLine();
                if (s != null) {
                    s = s.toUpperCase().trim();
                    if (s.length() > 0) {
                        getBucket(s.length()).addWord(s);
                        words.add(s);
                    }
                }
            } while (s != null);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public void load(String[] dictionary) {
        if (dictionary != null) {
            for (String s : dictionary) {
                if (s == null) {
                    continue;
                }
                s = s.toUpperCase().trim();
                if (s.length() > 0) {
                    getBucket(s.length()).addWord(s);
                    words.add(s);
                }
            }
        }
    }

    public Set<String> getWords() {
        return words;
    }

    public Set<String> getWordsByPattern(String pattern) {
        if (pattern == null || !bucketsByWordLength.containsKey(pattern.length())) {
            return Collections.EMPTY_SET;
        }
        return getBucket(pattern.length()).getWordsByPattern(pattern);
    }

    public Set<String> getWordsByPattern(String pattern, Collection<String> blacklist) {
        if (pattern == null || !bucketsByWordLength.containsKey(pattern.length())) {
            return Collections.EMPTY_SET;
        }
        Set<String> result = getBucket(pattern.length()).getWordsByPattern(pattern);
        if (blacklist != null) {
            result.removeAll(blacklist);
        }
        return result;
    }

}

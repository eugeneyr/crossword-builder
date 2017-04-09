package info.lynxnet.crossword.test;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.security.*;
import javax.swing.*;

public class BeautifulCrosswordTester {
    int N; // rows/columns in the field
    String[] words;
    int[] weights;

    // -----------------------------------------
    void generate(long seed) {
        try {
            // generate test case
            SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.setSeed(seed);
            N = rnd.nextInt(81) + 20;
            if (seed == 1)
                N = 11;
            System.out.println("N = " + N);
            // get the list of all dictionary words and choose the ones that
            // will be used
            BufferedReader br = new BufferedReader(new FileReader("data/crossword/words.txt"));
            double prob = rnd.nextDouble() * 0.009 + 0.001; // probability of
            // each word to be
            // used
            if (seed == 1)
                prob = 0.001;
            Vector<String> w = new Vector<String>();
            for (String line; (line = br.readLine()) != null; )
                if (rnd.nextDouble() < prob)
                    w.add(line);
            words = new String[w.size()];
            words = w.toArray(words);
            System.out.println(words.length + " words");
            /*
			 * for (int i=0; i<words.length; i++) System.out.println(words[i]);
			 */
            // get the weights of different scoring parts
            weights = new int[4];
            for (int i = 0; i < 4; i++)
                weights[i] = rnd.nextInt(10) + 1;
            for (int i = 0; i < 4; i++)
                System.out.print(weights[i] + " ");
            System.out.println();
        } catch (Exception e) {
            System.err.println("An exception occurred while generating test case.");
            e.printStackTrace();
        }
    }

    // -----------------------------------------
    public double runTest(String seed) {
        try {
            int i, j;
            generate(Long.parseLong(seed));
            // pass the generated parameters to the solution and get the return.
            // No manual mode
            String[] ret = generateCrossword(N, words, weights);
            if (ret == null || ret.length != N) {
                addFatalError("Your return contained invalid number of elements.");
                return 0;
            }
            char[][] board = new char[N][N];
            for (i = 0; i < N; i++) {
                if (ret[i] == null || ret[i].length() != N) {
                    addFatalError("Element "
                            + i
                            + " of your return contained invalid number of characters.");
                    return 0;
                }
                for (j = 0; j < N; j++) {
                    board[i][j] = ret[i].charAt(j);
                    if ((board[i][j] < 'A' || board[i][j] > 'Z')
                            && board[i][j] != '.') {
                        addFatalError("Character [" + i + "][" + j
                                + "] of your return was invalid.");
                        return 0;
                    }
                }
            }

            // score the return
            // 0. validity with respect to uniqueness of words used and words
            // coverage for all sequences of at least 2 letters in row
            int totalLetters = 0;
            boolean[] usedWord = new boolean[words.length];
            Arrays.fill(usedWord, false);
            StringBuffer sb = new StringBuffer();
            for (i = 0; i < N; i++) {
                sb.append(new String(board[i]));
                sb.append(".");
            }
            for (i = 0; i < N; i++) {
                for (j = 0; j < N; j++)
                    sb.append(board[j][i]);
                sb.append(".");
            }
            String[] met = sb.toString().split("[\\.]+");
            for (i = 0; i < met.length; i++) {
                totalLetters += met[i].length();
                if (met[i].length() >= 2) { // check only words of length 2 and
                    // more
                    j = Arrays.binarySearch(words, met[i]);
                    // check whether this is a word
                    if (j < 0) {
                        addFatalError("Your crossword contains word \""
                                + met[i]
                                + "\" which is not present in dictionary.");
                        return 0;
                    }
                    // check whether this word was already used
                    if (usedWord[j]) {
                        addFatalError("Your crossword contains word \""
                                + met[i] + "\" twice or more.");
                        return 0;
                    }
                    // mark the word as used
                    usedWord[j] = true;
                }
            }
            totalLetters /= 2; // each letter was counted twice

            // 0. validity with respect to "each letter must be part of a word"
            // = each letter cell must have a letter cell neighbor
            for (i = 0; i < N; i++)
                for (j = 0; j < N; j++)
                    if (board[i][j] != '.'
                            && (i == 0 || board[i - 1][j] == '.')
                            && (i == N - 1 || board[i + 1][j] == '.')
                            && (j == 0 || board[i][j - 1] == '.')
                            && (j == N - 1 || board[i][j + 1] == '.')) {
                        // all neighbors are . or outside of the board
                        addFatalError("Your crossword contains a letter which is not part of any word (at "
                                + i + ", " + j + ").");
                        return 0;
                    }

            // 1. board filling score = no of letters / no of cells
            double boardFillingScore = totalLetters * 1.0 / (N * N);
            addFatalError("Board filling score = " + boardFillingScore);

            // 2. rows/cols filling score - no of cols with at least 1 char * no
            // of rows with at least 1 char / N*N
            int filledCols = 0, filledRows = 0;
            boolean emptyCol, emptyRow;
            for (i = 0; i < N; i++) {
                emptyCol = emptyRow = true;
                for (j = 0; j < N; j++) {
                    if (board[i][j] != '.')
                        emptyRow = false;
                    if (board[j][i] != '.')
                        emptyCol = false;
                }
                if (!emptyCol)
                    filledCols++;
                if (!emptyRow)
                    filledRows++;
            }
            double rcFillingScore = filledCols * filledRows * 1.0 / (N * N);
            addFatalError("Rows/columns filling score = " + rcFillingScore);

            // 3. symmetry score
            double symmetryScore = 0.0, nc = 0, cellScore;
            int nEqual;
            for (i = 0; i < (N + 1) / 2; i++)
                for (j = 0; j <= i; j++) {
                    nEqual = (board[i][j] == '.' ? 1 : 0)
                            + (board[i][N - j - 1] == '.' ? 1 : 0)
                            + (board[N - i - 1][j] == '.' ? 1 : 0)
                            + (board[N - i - 1][N - j - 1] == '.' ? 1 : 0)
                            + (board[j][i] == '.' ? 1 : 0)
                            + (board[j][N - i - 1] == '.' ? 1 : 0)
                            + (board[N - j - 1][i] == '.' ? 1 : 0)
                            + (board[N - j - 1][N - i - 1] == '.' ? 1 : 0);
                    nEqual = Math.max(nEqual, 8 - nEqual);
                    cellScore = 0;
                    if (nEqual == 8)
                        cellScore = 1;
                    if (nEqual == 7)
                        cellScore = 0.5;
                    if (nEqual == 6)
                        cellScore = 0.1;
                    symmetryScore += cellScore;
                    nc++;
                    // System.out.println(i+" "+j+" "+nEqual+": "+board[i][j]+board[i][N-j-1]+board[N-i-1][j]+board[N-i-1][N-j-1]+board[j][i]+board[j][N-i-1]+board[N-j-1][i]+board[N-j-1][N-i-1]+" -> "+cellScore);
                }
            symmetryScore /= nc;
            addFatalError("Symmetry score = " + symmetryScore);

            // 4. words crossings score - no of letters which are parts of 2
            // words, divided by no of letters overall
            // for each letter, check whether it's part of a vertical word, and
            // part of horizontal word
            double crossingsScore = 0;
            for (i = 0; i < N; i++)
                for (j = 0; j < N; j++)
                    if (board[i][j] != '.'
                            && (i > 0 && board[i - 1][j] != '.' || i < N - 1
                            && board[i + 1][j] != '.')
                            && (j > 0 && board[i][j - 1] != '.' || j < N - 1
                            && board[i][j + 1] != '.'))
                        crossingsScore++;
            if (totalLetters > 0)
                crossingsScore /= totalLetters;
            addFatalError("Crossings score = " + crossingsScore);

            return (boardFillingScore * weights[0] + rcFillingScore
                    * weights[1] + symmetryScore * weights[2] + crossingsScore
                    * weights[3])
                    / (weights[0] + weights[1] + weights[2] + weights[3]);
        } catch (Exception e) {
            System.err
                    .println("An exception occurred while trying to get your program's results.");
            e.printStackTrace();
            return 0;
        }
    }

    // ------------- server part -------------------
    public String checkData(String test) {
        return "";
    }

    // -----------------------------------------
    public String displayTestCase(String test) {
        StringBuffer sb = new StringBuffer();
        sb.append("seed = " + test + "\n");
        generate(Long.parseLong(test));
        sb.append("N = " + N + "\n");
        sb.append(words.length + "words\n");
        sb.append("Weights are\n");
        //
        return sb.toString();
    }

    // -----------------------------------------
    public double[] score(double[][] sc) {
        double[] res = new double[sc.length];
        // absolute
        for (int i = 0; i < sc.length; i++) {
            res[i] = 0;
            for (int j = 0; j < sc[0].length; j++)
                res[i] += sc[i][j];
        }
        return res;
    }

    // ------------- visualization part ------------
    static String exec;
    static Process proc;
    InputStream is;
    OutputStream os;
    BufferedReader br;

    // -----------------------------------------
    String[] generateCrossword(int N, String[] words, int[] weights)
            throws IOException {
        // pass the parameters
        int i;
        StringBuffer sb = new StringBuffer();
        sb.append(N + "\n" + words.length + "\n");
        for (i = 0; i < words.length; i++)
            sb.append(words[i] + "\n");
        for (i = 0; i < weights.length; i++)
            sb.append(weights[i] + "\n");
        os.write(sb.toString().getBytes());
        os.flush();
        // and get the return
        String[] ret = new String[N];
        for (i = 0; i < N; i++)
            ret[i] = br.readLine();
        return ret;
    }

    // -----------------------------------------
    public BeautifulCrosswordTester(String seed) {
        try {
            // interface for runTest
            try {
                Runtime rt = Runtime.getRuntime();
                proc = rt.exec(exec);
                os = proc.getOutputStream();
                is = proc.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                new ErrorReader(proc.getErrorStream()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Score = " + runTest(seed));
            if (proc != null)
                try {
                    proc.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------
    public static void main(String[] args) {
        String seed = "1";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-seed"))
                seed = args[++i];
            if (args[i].equals("-exec"))
                exec = args[++i];
        }
        if (exec == null) {
            System.out.println("No solution to run.");
            return;
        }
        BeautifulCrosswordTester f = new BeautifulCrosswordTester(seed);
    }

    // -----------------------------------------
    void addFatalError(String message) {
        System.out.println(message);
    }
}

class ErrorReader extends Thread {
    InputStream error;

    public ErrorReader(InputStream is) {
        error = is;
    }

    public void run() {
        try {
            byte[] ch = new byte[50000];
            int read;
            while ((read = error.read(ch)) > 0) {
                String s = new String(ch, 0, read);
                System.out.print(s);
                System.out.flush();
            }
        } catch (Exception e) {
        }
    }
}

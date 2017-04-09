package info.lynxnet.crossword;

import java.util.*;
import java.util.concurrent.*;

public class ParallelBeautifulCrossword extends BeautifulCrossword {
    protected BlockingQueue<Runnable> queue = new LinkedBlockingDeque<Runnable>(100000);
    protected ExecutorService service = new ThreadPoolExecutor(
            2, Runtime.getRuntime().availableProcessors() / 2,
            10, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

    public long getActiveCount() {
        return ((ThreadPoolExecutor) service).getActiveCount();
    }

    public long getQueuedSubmissionCount() {
        return queue.size();
    }

    @Override
    public String getState() {
        return String.format("%s active tasks = %d queued submissions = %d ***",
                super.getState(), getActiveCount(), getQueuedSubmissionCount());
    }

    public ParallelBeautifulCrossword() {
    }

    public void execute(CrosswordBuilder builder) {
        try {
            service.submit(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String[] generateCrossword(int N, int[] weights) {
        n = N;
        this.weights = weights;
        Board board = new Board(n);
        execute(new CrosswordBuilder(this, board, n, 0, Direction.ACROSS));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!queue.isEmpty()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<Board> puzzles = new ArrayList<>(getBestPuzzles());
        return puzzles.size() > 0 ? puzzles.get(puzzles.size() - 1).asStringArray() : null;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        System.out.println(String.format("Current queue length: %d", queue.size()));
        service.shutdown();
    }
}

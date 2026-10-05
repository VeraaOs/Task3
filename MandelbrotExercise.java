import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
ДЗ 3. Множество Мандельброта
Распараллелить вычисление множества Мандельброта.
Вывод картинки опционален (в данном решении картинка не выводится, 
только производится расчёт итераций для каждого пикселя).
*/
public class MandelbrotExercise {

    public static void main(String[] args) throws Exception {
        int width = 1000;
        int height = 1000;
        int maxIterations = 1000;

        double minR = -2.5, maxR = 1.0;
        double minI = -1.25, maxI = 1.25;

        int workers = 8; // Количество потоков

        int[][] iterations = new int[width][height];

        ExecutorService pool = Executors.newFixedThreadPool(workers);
        List<Future<?>> results = new ArrayList<>();

        try {
            for (int worker = 0; worker < workers; worker++) {
                int fromY = worker * height / workers;
                int toY = (worker + 1) * height / workers;

                results.add(pool.submit(() -> {
                    for (int y = fromY; y < toY; y++) {
                        for (int x = 0; x < width; x++) {
                            double cr = minR + (maxR - minR) * x / width;
                            double ci = minI + (maxI - minI) * y / height;

                            double zr = 0;
                            double zi = 0;
                            int iter = 0;

                            while (zr * zr + zi * zi <= 4.0 && iter < maxIterations) {
                                double zrNew = zr * zr - zi * zi + cr;
                                zi = 2 * zr * zi + ci;
                                zr = zrNew;
                                iter++;
                            }

                            iterations[x][y] = iter;
                        }
                    }
                }));
            }

            for (Future<?> result : results) {
                result.get(30, TimeUnit.SECONDS);
            }

        } finally {
            pool.shutdownNow();
        }

        int totalPixels = width * height;
        System.out.println("OK: Mandelbrot set calculated for " + totalPixels + " pixels using " + workers + " threads");
    }
}
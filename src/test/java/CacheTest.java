import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class CacheTest {
    private final Logger log = LoggerFactory.getLogger(CacheTest.class);

    @Test
    public void validWorkTest() throws IOException {
        Path inputfilePath = Path.of("input.txt");
        Files.writeString(inputfilePath,
                "5 15\n" +
                        "3\n" +
                        "1\n" +
                        "4\n" +
                        "1\n" +
                        "5\n" +
                        "9\n" +
                        "2\n" +
                        "6\n" +
                        "5\n" +
                        "3\n" +
                        "5\n" +
                        "8\n" +
                        "7\n" +
                        "9\n" +
                        "3");

        Path outputfilePath = Path.of("output.txt");
        if (Files.exists(outputfilePath)) Files.delete(Path.of("output.txt"));

        long start = System.currentTimeMillis();
        Cache cache = new Cache(inputfilePath, outputfilePath);
        cache.requestCount();
        long elapsedTime = System.currentTimeMillis() - start;

        assertTrue(elapsedTime <= 3000);

        assertTrue(Files.exists(outputfilePath));
        String fileData = Files.readString(outputfilePath);
        assertEquals(9, Integer.parseInt(fileData));
    }

    // Данный тест, конечно, не претендует на "достоверность" его результата
    // (за счёт того, что почти всегда генерируется большое количество уникальных элементов,
    // а задание как бы подразумевает наличие немалого числа дуликатов), но это хотя бы что-то:)
    @Test
    public void averageTimeTest() throws IOException {
        Path inputfilePath = Path.of("input.txt");
        assertTrue(Files.exists(inputfilePath));

        Path outputfilePath = Path.of("output.txt");
        if (Files.exists(outputfilePath)) Files.delete(Path.of("output.txt"));

        Cache cache = new Cache(inputfilePath, outputfilePath);
        SplittableRandom random = new SplittableRandom();

        List<Long> timeList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            int randomN = random.nextInt(99999) + 1;
            int randomM = random.nextInt(99999) + 1;
            Generator.generateTestCase(randomN, randomM, inputfilePath.toString());
            long start = System.currentTimeMillis();
            cache.requestCount();
            long elapsedTime = System.currentTimeMillis() - start;
            timeList.add(elapsedTime);

            log.info("i=" + i
                    + ", cache size=" + randomN
                    + ", request size=" + randomM
                    + ", time=" + elapsedTime + "ms"
                    + ", current avg=" + avgValue(timeList) + "ms");
        }
        long avgTime = avgValue(timeList);
        log.info("Avg time=" + avgTime + "ms");
        assertTrue(avgTime <= 3000);
    }

    private long avgValue(List<Long> lst) {
        long sum = 0;
        for (Long time : lst) {
            sum += time;
        }
        long avg = sum / lst.size();
        return avg;
    }
}
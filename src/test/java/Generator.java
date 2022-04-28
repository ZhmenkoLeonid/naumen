import java.io.FileWriter;
import java.io.IOException;
import java.util.SplittableRandom;

public class Generator {

    public static void generateTestCase(int N, int M, String filePath) throws IOException {
        SplittableRandom random = new SplittableRandom();
        long[] values = new long[M];
        for (int i = 0; i < M - 2; i++) {
            values[i] = random.nextLong(M);
        }
        values[M - 2] = 0;
        values[M - 1] = Long.MAX_VALUE;

        FileWriter writer = new FileWriter(filePath);
        writer.write(N + " " + M + System.lineSeparator());
        for (long value : values) {
            writer.write(value + System.lineSeparator());
        }
        writer.close();
    }
}

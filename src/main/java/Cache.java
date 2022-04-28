import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Cache {
    private final Path inputFilePath;
    private final Path outputFilePath;

    private int cacheCapacity;
    private List<Long> requestNumberList;
    private Map<Long, Integer> frequencyCountMap;
    private int lastFrequencyCalculationStartIndex;

    public Cache(Path inputFilePath, Path outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void requestCount() throws IOException {
        parseFile(inputFilePath);

        List<Long> cache = new ArrayList<>(cacheCapacity);
        frequencyCountMap = null;
        int cachelessRequestCount = 0;

        for (int i = 0; i < requestNumberList.size(); i++) {
            long requestNumber = requestNumberList.get(i);
            if (!cache.contains(requestNumber)) {
                if (cache.size() < cacheCapacity) {
                    cache.add(requestNumber);
                } else {
                    cache.set(minCacheRequestCountIndexFrom(i, cache), requestNumber);
                }
                cachelessRequestCount++;
            }
        }

        Files.writeString(outputFilePath, String.valueOf(cachelessRequestCount));
    }

    private int minCacheRequestCountIndexFrom(int startIndex, List<Long> cache) {
        int[] frequencyCountArray = new int[cache.size()];
        if (frequencyCountMap == null) {
            // init
            frequencyCountMap = new HashMap<>();
            for (int i = 0; i < requestNumberList.size(); i++) {
                long requestNumber = requestNumberList.get(i);
                frequencyCountMap.putIfAbsent(requestNumber, 0);
                frequencyCountMap.put(requestNumber, frequencyCountMap.get(requestNumber) + 1);
            }
        } else {
            // recalculate
            for (int i = lastFrequencyCalculationStartIndex; i < startIndex; i++) {
                Long requestNumber = requestNumberList.get(i);
                frequencyCountMap.replace(requestNumber, frequencyCountMap.get(requestNumber) - 1);
            }
        }


        // получаем количество появлений в будущем для номеров из кэша
        for (int i = 0; i < frequencyCountArray.length; i++) {
            frequencyCountArray[i] = frequencyCountMap.getOrDefault(cache.get(i), 0);
        }
        // запоминаем индекс для пересчёта частот
        lastFrequencyCalculationStartIndex = startIndex;
        // возвращаем индекс наименее часто встречаемого номера из кэша (индекс, который будем заменять)
        return minArrayValue(frequencyCountArray);
    }

    private int minArrayValue(int[] arr) {
        int min = arr[0];
        int minIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private void parseFile(Path filePath) throws IOException {
        Scanner scanner = new Scanner(filePath);
        cacheCapacity = scanner.nextInt();
        int requestCount = scanner.nextInt();

        requestNumberList = new ArrayList<>(requestCount);
        for (int i = 0; i < requestCount; i++) {
            requestNumberList.add(scanner.nextLong());
        }
    }
}
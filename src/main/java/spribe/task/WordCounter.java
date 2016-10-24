package spribe.task;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Taras Zubrei
 */
public class WordCounter {
    private ConcurrentHashMap<String, Long> map;

    public WordCounter() {
        map = new ConcurrentHashMap<>();
    }

    public void add(String... words) {
        Arrays.stream(words).forEach(this::add);
    }

    public void add(String word) {
        map.compute(unify(word), (k, v) -> v == null ? 1L : v + 1L);
    }

    public Long get(String word) {
        return map.getOrDefault(unify(word), 0L);
    }
    private String unify(String word) {
        return word.toLowerCase();
    }
}

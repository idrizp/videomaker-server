package dev.idriz.videomaker.list;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ListUtils {

    /**
     * Returns a random element from the list
     * @param list the list to get the random element from
     * @return a random element from the list
     * @param <T> the type of the list
     */
    public static <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

}

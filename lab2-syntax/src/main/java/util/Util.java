package util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {
    public static <T> Set<T> arrayToSet(T... objects) {
        return Arrays.stream(objects).collect(Collectors.toSet());
    }
}

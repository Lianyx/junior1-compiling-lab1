package util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Util {
    public static <T> List<T> mergeList(List<T> l1, List<T> l2) {
        return Stream.of(l1, l2).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> Set<T> mergeSet(Set<T> s1, Set<T> s2) {
        return Stream.of(s1, s2).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static Set<Integer> intsToSet(int... ints) {
        return IntStream.of(ints).boxed().collect(Collectors.toSet());
    }

    public static List<Integer> intsToList(int... ints) {
        return IntStream.of(ints).boxed().collect(Collectors.toList());
    }
}

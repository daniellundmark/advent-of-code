package year2018.day01;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day01Test {

    @Test
    public void testFrequency() {
        assertEquals(1, Day01.frequency(List.of(0L, 1L)));
        assertEquals(-1, Day01.frequency(List.of(1L, -2L)));
        assertEquals(2, Day01.frequency(List.of(-1L, 3L)));
        assertEquals(3, Day01.frequency(List.of(2L, 1L)));

        assertEquals(3, Day01.frequency(List.of(1L, 1L, 1L)));
        assertEquals(0, Day01.frequency(List.of(1L, 1L, -2L)));
        assertEquals(-6, Day01.frequency(List.of(-1L, -2L, -3L)));
    }

    @Test
    public void testFirstReachedTwice() {

        assertEquals(2, Day01.firstReachedTwice( toLongs(List.of(1, -2, 3, 1))));
        assertEquals(10, Day01.firstReachedTwice( toLongs(List.of(3, 3, 4, -2, -4))));
        assertEquals(5, Day01.firstReachedTwice( toLongs(List.of(-6, 3, 8, 5, -6))));
        assertEquals(14, Day01.firstReachedTwice( toLongs(List.of(7, 7, -2, -7, -4))));
    }

    private List<Long> toLongs(List<Integer> ints) {
        return ints.stream().map(Long::valueOf).toList();
    }
}

package year2019.day01;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day01Test {

    @Test
    public void testDirectFuelRequirement() {

        assertEquals(2, Day01.directFuelRequirement(12));
        assertEquals(2, Day01.directFuelRequirement(14));
        assertEquals(654, Day01.directFuelRequirement(1969));
        assertEquals(33583, Day01.directFuelRequirement(100756));

    }

    @Test
    public void testRecursiveFuelRequirement() {
        assertEquals(2, Day01.recursiveFuelRequirement(14));
        assertEquals(966, Day01.recursiveFuelRequirement(1969));
        assertEquals(50346, Day01.recursiveFuelRequirement(100756));
    }

}

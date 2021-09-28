package year2019.day01;

import util.Utils;

import java.util.List;


public class Day01 {

    static long directFuelRequirement(long mass) {
        return Math.max(mass / 3 - 2, 0);
    }

    public static long part1(List<Long> inputs) {
        var total = inputs
                        .stream().mapToLong(Day01::directFuelRequirement)
                        .sum();

        return total;
    }

    static long recursiveFuelRequirement(long mass) {
        if(mass <= 0) {
            return 0;
        }
        var fuel = directFuelRequirement(mass);
        return fuel + recursiveFuelRequirement(fuel);
    }


    public static long part2(List<Long> inputs) {
        return inputs.stream()
                        .map(Day01::recursiveFuelRequirement).reduce(0L, Long::sum);
    }

    public static void main(String[] args) {
        var inputs =  Utils.readAndTransform("year2019/day01.input", Long::parseLong);

        System.out.println("part1: "+part1(inputs));
        System.out.println("part2: "+part2(inputs));
    }

}

package year2018.day01;

import com.google.common.collect.Sets;
import util.Utils;

import java.util.List;

public class Day01 {

    public static long frequency(List<Long> inputs) {
        return inputs.stream().reduce(0L, Long::sum);
    }


    public static long firstReachedTwice(List<Long> inputs) {
        var found = Sets.<Long>newHashSet();
        var currentFrequency = 0L;
        var index = 0;
        while(true) {
            currentFrequency += inputs.get(index);
            if(found.contains(currentFrequency)) {
                return currentFrequency;
            }
            found.add(currentFrequency);
            index = (index + 1) % inputs.size();
        }
    }

    public static void main(String[] args) {
        var input = Utils.readAndTransform("year2018/day01.input", Long::parseLong);

        System.err.println("Part1: "+frequency(input));

        System.err.println("Part2: "+firstReachedTwice(input));
    }

}

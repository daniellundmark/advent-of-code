package year2018.day02;


import com.google.common.collect.Streams;
import util.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day02 {


    public static CheckSum checkLine(String line) {
        var characterCounts = new CharacterCounts();
        line.chars().mapToObj(c -> (char) c).forEach(characterCounts::increment);

        var twos = characterCounts.counts().values().stream().anyMatch(c -> c == 2);
        var threes = characterCounts.counts().values().stream().anyMatch(c -> c == 3);

        return new CheckSum(twos ? 1 : 0, threes ? 1: 0);
    }

    public static int checkSum(List<String> lines) {
        var total = lines.stream().map(Day02::checkLine).reduce(new CheckSum(0, 0), CheckSum::add);
        return total.multiply();
    }

    public static long difference(String a, String b) {
        assert(a.length() == b.length());

        var differentCharacters = Streams
                .zip(a.chars().boxed(), b.chars().boxed(), Integer::equals)
                .filter(isEqual -> !isEqual)
                .count();

        return differentCharacters;
    }

    public static Pair<String> findBoxes(List<String> boxIds) {
        for(int i = 0; i < boxIds.size() - 1; i++) {
            for(int j = i + 1; j < boxIds.size(); j++) {
                var diff = difference(boxIds.get(i), boxIds.get(j));
                if(diff == 1) {
                    return new Pair(boxIds.get(i), boxIds.get(j));
                }
            }
        }
        throw new RuntimeException("Could not find pair of boxes with difference 1");
    }

    public static String commonLetters(Pair<String> pair) {
        return IntStream.range(0, pair.a().length())
                .filter(i -> pair.a().charAt(i) == pair.b().charAt(i))
                .mapToObj(i -> pair.a().charAt(i))
                .map(c -> c.toString())
                .collect(Collectors.joining());
    }

    public static void main(String[] args) {
        var lines = Utils.readInput("year2018/day02.input");

        System.err.println("Part1: "+checkSum(lines));
        System.err.println("Part2: "+ commonLetters( findBoxes(lines) ));
    }

}


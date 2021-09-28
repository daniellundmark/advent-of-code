package year2018.day01;

import org.junit.jupiter.api.Test;
import year2018.day02.CheckSum;
import year2018.day02.Day02;
import year2018.day02.Pair;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day02Test {

    @Test
    public void testCheckLine() {
        assertEquals(new CheckSum(0, 0), Day02.checkLine("abcdef"));
        assertEquals(new CheckSum(1, 1), Day02.checkLine("bababc"));
        assertEquals(new CheckSum(1, 0), Day02.checkLine("abbcde"));
        assertEquals(new CheckSum(0, 1), Day02.checkLine("abcccd"));
        assertEquals(new CheckSum(1, 0), Day02.checkLine("aabcdd"));
        assertEquals(new CheckSum(1, 0), Day02.checkLine("abcdee"));
        assertEquals(new CheckSum(0, 1), Day02.checkLine("ababab"));
    }

    @Test
    public void testCheckSum() {
        assertEquals(12, Day02.checkSum(List.of(
                "abcdef",
                "bababc",
                "abbcde",
                "abcccd",
                "aabcdd",
                "abcdee",
                "ababab"
        )));
    }

    @Test
    public void testDifference() {
        assertEquals(1, Day02.difference("aaa", "aba"));
        assertEquals(2, Day02.difference("aaa", "abc"));
        assertEquals(2, Day02.difference("abcde", "axcye"));
        assertEquals(1, Day02.difference("fghij", "fguij"));
    }

    @Test
    public void testFindBoxes() {
        assertEquals(new Pair("fghij", "fguij"),
                Day02.findBoxes("""
                        abcde
                        fghij
                        klmno
                        pqrst
                        fguij
                        axcye
                        wvxyz
                        """.lines().toList()
        ));
    }

    @Test
    public void testCommonLetters() {
        assertEquals("fgij", Day02.commonLetters(new Pair<>("fghij", "fguij")));
    }
}

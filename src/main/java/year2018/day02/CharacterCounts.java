package year2018.day02;

import java.util.HashMap;

record CharacterCounts(HashMap<Character, Integer> counts) {
    public CharacterCounts() {
        this(new HashMap<>());
    }

    public void increment(Character c) {
        if (!counts.containsKey(c)) {
            counts.put(c, 1);
        } else {
            counts.put(c, counts.get(c) + 1);
        }
    }
}

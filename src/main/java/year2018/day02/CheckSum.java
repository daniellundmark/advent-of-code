package year2018.day02;

public record CheckSum(int twos, int threes) {
    public CheckSum add(CheckSum other) {
        return new CheckSum(this.twos + other.twos, this.threes + other.threes);
    }

    public int multiply() {
        return twos * threes;
    }
}

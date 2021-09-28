package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Utils {

    public static List<String> readInput(String fileName) {

        try {
            return Files.readAllLines(Paths.get("src/main/resources/"+fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file="+fileName, e);
        }
    }

    public static <T> List<T> readAndTransform(String fileName, Function<String, T> transformer) {
        var lines = readInput(fileName);
        return lines.stream().map(line -> transformer.apply(line)).toList();
    }
}

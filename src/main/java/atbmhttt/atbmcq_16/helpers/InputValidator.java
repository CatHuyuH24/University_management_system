package atbmhttt.atbmcq_16.helpers;

public class InputValidator {
    public static void validateInput(String input) throws IllegalArgumentException {

        // Disallow special characters: ; ' " -- / , . * _ #
        String[] bannedPatterns = { ";", "'", "\"", "--", "/", "\\.", "\\*", "_", "#" };
        for (String pattern : bannedPatterns) {
            if (input.contains(pattern.replace("\\", ""))) {
                throw new IllegalArgumentException(
                        "Input contains illegal character or sequence: " + pattern.replace("\\", ""));
            }
        }
    }
}

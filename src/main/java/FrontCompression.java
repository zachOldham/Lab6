import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Implement front compression.
 * <p>
 * Front compression (also called, strangely, back compression, and, less strangely, front coding)
 * is a compression algorithm used for reducing the size of certain kinds of textual structured
 * data. Instead of storing an entire string value, we use a prefix from the previous value in a
 * list.
 * <p>
 * Front compression is particularly useful when compressing lists of words where each successive
 * element has a great deal of similarity with the previous. One example is a search (or book)
 * index. Another example is a dictionary.
 * <p>
 * This starter code will help walk you through the process of implementing front compression.
 *
 * @see <a href="https://cs125.cs.illinois.edu/lab/6/">Lab 6 Description</a>
 * @see <a href="https://en.wikipedia.org/wiki/Incremental_encoding"> Incremental Encoding on
 *      Wikipedia </a>
 */

public class FrontCompression {

    /**
     * Compress a newline-separated list of words using simple front compression.
     *
     * @param corpus the newline-separated list of words to compress
     * @return the input compressed using front encoding
     */
    public static String compress(final String corpus) {
        System.out.println("Compress Input:");
        System.out.println(corpus);
        /*
         * Defend against bad inputs.
         */
        if (corpus == null) {
            return null;
        } else if (corpus.length() == 0) {
            return "";
        }
        int curPrefixLength = 0;
        String compressedWord = "";
        Scanner wordScanner = new Scanner(corpus);
        String lastWord = wordScanner.nextLine();
        String curWord = "";
        String compressedStr = lastWord;
        while (wordScanner.hasNextLine()) {
            compressedStr += "\n";
            curWord = wordScanner.nextLine();
            curPrefixLength = longestPrefix(curWord, lastWord);
            compressedWord = curPrefixLength + " "
            + curWord.substring(curPrefixLength, curWord.length());
            compressedStr += compressedWord;
            lastWord = curWord;
        }
        wordScanner.close();
        System.out.println("Compress Output:");
        System.out.println(compressedStr);
        return compressedStr;
    }

    /**
     * Decompress a newline-separated list of words using simple front compression.
     *
     * @param corpus the newline-separated list of words to decompress
     * @return the input decompressed using front encoding
     */
    public static String decompress(final String corpus) {
        System.out.println("Decompress Input:");
        System.out.println(corpus);
        /*
         * Defend against bad inputs.
         */
        if (corpus == null) {
            return null;
        } else if (corpus.length() == 0) {
            return "";
        }
        int prefixLength = 0;
        String prefix = "";
        String body = "";
        Scanner wordScanner = new Scanner(corpus);
        String decompressedStr = "";
        String curWord = "";
        String lastWord = wordScanner.nextLine();
        decompressedStr += lastWord;
        String decompressedWord = "";
        while (wordScanner.hasNext()) {
            decompressedStr += "\n";
            curWord = wordScanner.nextLine();
            prefixLength = Integer.parseInt(curWord.substring(0, curWord.indexOf(' ')));
            prefix = lastWord.substring(0, prefixLength);
            body = curWord.substring(curWord.indexOf(' ') + 1, curWord.length());
            decompressedWord = prefix + body;
            decompressedStr += decompressedWord;
            lastWord = decompressedWord;
        }
        wordScanner.close();
        System.out.println("Decompress Output:");
        System.out.println(decompressedStr);
        return decompressedStr;
    }

    /**
     * Compute the length of the common prefix between two strings.
     *
     * @param firstString the first string
     * @param secondString the second string
     * @return the length of the common prefix between the two strings
     */
    private static int longestPrefix(final String firstString, final String secondString) {
        int prefixLength = 0;
        int shortWordLength = 0;
        if (firstString.length() < secondString.length()) {
            shortWordLength = firstString.length();
        } else {
            shortWordLength = secondString.length();
        }
        for (int index = 0; index < shortWordLength; index++) {
            if (firstString.charAt(index) == secondString.charAt(index)) {
                prefixLength++;
            } else {
                break;
            }
        }
        return prefixLength;
    }

    /**
     * Test your compression and decompression algorithm.
     *
     * @param unused unused input arguments
     * @throws URISyntaxException thrown if the file URI is invalid
     * @throws FileNotFoundException thrown if the file cannot be found
     */
    public static void main(final String[] unused)
            throws URISyntaxException, FileNotFoundException {

        /*
         * The magic 6 lines that you need in Java to read stuff from a file.
         */
        String words = null;
        String wordsFilePath = FrontCompression.class.getClassLoader().getResource("words.txt")
                .getFile();
        wordsFilePath = new URI(wordsFilePath).getPath();
        File wordsFile = new File(wordsFilePath);
        Scanner wordsScanner = new Scanner(wordsFile, "UTF-8");
        words = wordsScanner.useDelimiter("\\A").next();
        wordsScanner.close();

        String originalWords = words;
        String compressedWords = compress(words);
        String decompressedWords = decompress(compressedWords);

        if (decompressedWords.equals(originalWords)) {
            System.out.println("Original length: " + originalWords.length());
            System.out.println("Compressed length: " + compressedWords.length());
        } else {
            System.out.println("Your compression or decompression is broken!");
            String[] originalWordsArray = originalWords.split("\\R");
            String[] decompressedWordsArray = decompressedWords.split("\\R");
            boolean foundMismatch = false;
            for (int stringIndex = 0; //
                    stringIndex < Math.min(originalWordsArray.length,
                            decompressedWordsArray.length); //
                    stringIndex++) {
                if (!(originalWordsArray[stringIndex]
                        .equals(decompressedWordsArray[stringIndex]))) {
                    System.out.println("Line " + stringIndex + ": " //
                            + originalWordsArray[stringIndex] //
                            + " != " + decompressedWordsArray[stringIndex]);
                    foundMismatch = true;
                    break;
                }
            }
            if (!foundMismatch) {
                if (originalWordsArray.length != decompressedWordsArray.length) {
                    System.out.println("Original and decompressed files have different lengths");
                } else {
                    System.out.println("Original and decompressed files " //
                            + "have different line endings.");
                }
            }
        }
    }
}

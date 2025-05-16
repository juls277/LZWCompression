import java.util.*;

public class LZWCompression {
    private static final int DICTIONARY_SIZE = 256;
    private static final char[] ALPHABET = {'a', 'b', 'c', 'd'};

    public static void main(String[] args) {
        // Define probability distributions for the two cases
        double[] probsCase1 = {0.5, 0.2, 0.15, 0.15};
        double[] probsCase2 = {0.3, 0.3, 0.25, 0.15};

        // Generate random sequences of length 1000
        String seq1 = generateSequence(ALPHABET, probsCase1, 1000);
        String seq2 = generateSequence(ALPHABET, probsCase2, 1000);

        // Compress and report
        runCase(seq1, "Case 1");
        runCase(seq2, "Case 2");
    }

    private static void runCase(String sequence, String caseName) {
        List<Integer> compressed = compress(sequence);
        int compressedBits = compressed.size() * 8;  // fixed 8-bit codes up to 256 entries
        double compressionRatio = 2000.0 / compressedBits;  // 1000 symbols × 2 bits each = 2000 bits original

        System.out.printf("%s:\n", caseName);
        System.out.printf("  Original bits: %d%n", 2000);
        System.out.printf("  Compressed codes: %d entries%n", compressed.size());
        System.out.printf("  Compressed bits: %d%n", compressedBits);
        System.out.printf("  Compression ratio: %.3f%n\n", compressionRatio);
    }

    private static String generateSequence(char[] alphabet, double[] probs, int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            double r = rand.nextDouble();
            double cum = 0.0;
            for (int j = 0; j < alphabet.length; j++) {
                cum += probs[j];
                if (r < cum) {
                    sb.append(alphabet[j]);
                    break;
                }
            }
        }
        return sb.toString();
    }

    public static List<Integer> compress(String input) {
        int dictSize = ALPHABET.length;
        Map<String, Integer> dictionary = new HashMap<>();
        // Initialize dictionary with single-character strings
        for (int i = 0; i < ALPHABET.length; i++) {
            dictionary.put(String.valueOf(ALPHABET[i]), i);
        }

        String w = "";
        List<Integer> result = new ArrayList<>();

        for (char c : input.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {
                result.add(dictionary.get(w));
                if (dictSize < DICTIONARY_SIZE) {
                    dictionary.put(wc, dictSize++);
                }
                w = String.valueOf(c);
            }
        }

        // Output the code for w
        if (!w.isEmpty()) {
            result.add(dictionary.get(w));
        }
        return result;
    }

    public static String decompress(List<Integer> compressed) {
        int dictSize = ALPHABET.length;
        Map<Integer, String> dictionary = new HashMap<>();
        // Initialize dictionary with single-character strings
        for (int i = 0; i < ALPHABET.length; i++) {
            dictionary.put(i, String.valueOf(ALPHABET[i]));
        }

        String w = dictionary.get(compressed.get(0));
        StringBuilder result = new StringBuilder(w);

        for (int i = 1; i < compressed.size(); i++) {
            int k = compressed.get(i);
            String entry;
            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == dictSize) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed k: " + k);
            }
            result.append(entry);
            // Add w+entry[0] to the dictionary
            if (dictSize < DICTIONARY_SIZE) {
                dictionary.put(dictSize++, w + entry.charAt(0));
            }
            w = entry;
        }
        return result.toString();
    }
}

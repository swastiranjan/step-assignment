import java.util.*;

class PlagiarismDetector {
    private Map<String, Set<String>> nGramIndex = new HashMap<>();
    private int n = 5; // 5-grams

    public void indexDocument(String docId, String text) {
        String[] words = text.split("\\s+");
        for (int i = 0; i <= words.length - n; i++) {
            String nGram = String.join(" ", Arrays.copyOfRange(words, i, i + n));
            nGramIndex.computeIfAbsent(nGram, k -> new HashSet<>()).add(docId);
        }
    }

    public Map<String, Integer> analyzeDocument(String docId, String text) {
        Map<String, Integer> matches = new HashMap<>();
        String[] words = text.split("\\s+");
        for (int i = 0; i <= words.length - n; i++) {
            String nGram = String.join(" ", Arrays.copyOfRange(words, i, i + n));
            if (nGramIndex.containsKey(nGram)) {
                for (String otherDoc : nGramIndex.get(nGram)) {
                    if (!otherDoc.equals(docId)) {
                        matches.put(otherDoc, matches.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }
        return matches;
    }
}

public class plagiarism {
    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        detector.indexDocument("essay_089", "this is a sample essay with some text repeated");
        detector.indexDocument("essay_092", "this is a sample essay with a lot of repeated text and plagiarism suspected");

        Map<String, Integer> result = detector.analyzeDocument("essay_123", "this is a sample essay with some text repeated plagiarism suspected");

        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            System.out.println("Matches with " + entry.getKey() + ": " + entry.getValue() + " n-grams");
        }
    }
}
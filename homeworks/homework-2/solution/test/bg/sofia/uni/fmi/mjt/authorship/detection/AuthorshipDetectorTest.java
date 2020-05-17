package bg.sofia.uni.fmi.mjt.authorship.detection;

import org.junit.Before;
import org.junit.Test;

import static java.lang.Math.pow;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.*;

public class AuthorshipDetectorTest {
    private final double delta = pow(10, -13);

    private AuthorshipDetectorImpl authorshipDetector;

    private String mysteryText = "this is the\n" +
            "first sentence. Isn't\n" +
            "it? Yes ! !! This \n" +
            "\n" +
            "last bit :) is also a sentence, but \n" +
            "without a terminator other than the end of the file";

    private String knownSignatures = "Test Author, 3.5555555555555554, 1.0, 1.0, 4.5, 1.0\n" +
            "Fyodor Dostoevsky, 4.34066732195, 0.0528571428571, 0.0233414043584, 12.8108273249, 2.16705364781";

    private byte[] biteKnownSignatures = knownSignatures.getBytes();
    private final double[] weight = {11, 33, 50, 0.4, 4};

    @Before
    public void before() {
        authorshipDetector = new AuthorshipDetectorImpl(new ByteArrayInputStream(biteKnownSignatures), weight);
    }

    @Test
    public void testTextToListOfWords() {
        List<String> result = null;
        result = authorshipDetector.textToListOfWords(mysteryText);

        String expectedText = "this is the " +
                "first sentence isn't " +
                "it yes this " +
                "last bit is also a sentence but " +
                "without a terminator other than the end of the file";
        String[] expectedArray = expectedText.split(" ");

        List<String> expected = new ArrayList<>(Arrays.asList(expectedArray));

        assertEquals(expected, result);
    }

    @Test
    public void testAverageWordLengthWithInteger() {
        List<String> test = new ArrayList<>();
        test.add("one");
        test.add("one");
        test.add("one");

        double actual = authorshipDetector.averageWordLength(test);
        final double expected = 3;
        assertEquals(expected, actual, delta);
    }

    @Test
    public void testAverageWordLengthWithFloatPoint() {
        List<String> test = new ArrayList<>();
        test.add("one");
        test.add("onee");
        test.add("oneee");

        double actual = authorshipDetector.averageWordLength(test);
        final double expected = (3 + 4 + 5) / 3;
        assertEquals(expected, actual, delta);
    }

    @Test
    public void testAverageWordLengthWithZeroWords() {
        List<String> test = new ArrayList<>();

        double actual = authorshipDetector.averageWordLength(test);
        final double expected = 0;
        assertEquals(expected, actual, delta);
    }

    @Test
    public void testTypeTokenRatioSimple() {
        List<String> test = new ArrayList<>();
        test.add("one");
        test.add("different");
        test.add("wow");

        Map<String, Integer> map = authorshipDetector.wordCountMap(test);

        final double expected = 3 / (double) 3;
        double actual = authorshipDetector.typeTokenRatio(map, test.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testTypeTokenRatioWithMoreWords() {
        List<String> test = new ArrayList<>();
        test.add("one");
        test.add("one");
        test.add("one");
        test.add("different");
        test.add("wow");
        test.add("wow");

        Map<String, Integer> map = authorshipDetector.wordCountMap(test);

        final double expected = 3 / (double) 6;
        double actual = authorshipDetector.typeTokenRatio(map, test.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testTypeTokenRatioWithZeroWords() {
        List<String> test = new ArrayList<>();

        Map<String, Integer> map = authorshipDetector.wordCountMap(test);

        final double expected = 0;
        double actual = authorshipDetector.typeTokenRatio(map, test.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testHapaxLegomenaRatioSimple() {
        List<String> test = new ArrayList<>();
        test.add("one");
        test.add("different");
        test.add("wow");

        Map<String, Integer> map = authorshipDetector.wordCountMap(test);

        final double expected = 3 / (double) 3;
        double actual = authorshipDetector.hapaxLegomenaRatio(map, test.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testHapaxLegomenaRatioWithZeroWords() {
        List<String> test = new ArrayList<>();

        Map<String, Integer> map = authorshipDetector.wordCountMap(test);

        final double expected = 0;
        double actual = authorshipDetector.hapaxLegomenaRatio(map, test.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testHapaxLegomenaRatioWithMoreWords() {
        List<String> test = new ArrayList<>();
        test.add("one");
        test.add("one");
        test.add("one");
        test.add("different");
        test.add("wow");
        test.add("wow");

        Map<String, Integer> map = authorshipDetector.wordCountMap(test);

        final double expected = 1 / (double) test.size();
        double actual = authorshipDetector.hapaxLegomenaRatio(map, test.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testTextToListOfSentences() {
        List<String> actual = authorshipDetector.textToListOfSentences(mysteryText);

        List<String> expected = new ArrayList<>();
        expected.add("this is the first sentence");
        expected.add("Isn't it");
        expected.add("Yes");
        expected.add("This   last bit :) is also a sentence, " +
                "but  without a terminator other than the end of the file");

        assertEquals(expected, actual);
    }

    @Test
    public void testAverageSentenceLengthSimple() {
        String testText = "Hi, how are you today? What is your name?";
        List<String> wordsList = authorshipDetector.textToListOfWords(testText);

        List<String> sentencesList = authorshipDetector.textToListOfSentences(testText);

        final double expected = 9 / (double) 2;
        double actual = authorshipDetector.averageSentenceLength(sentencesList, wordsList.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testAverageSentenceLengthWithMoreWordsAndSentences() {
        List<String> wordsList = authorshipDetector.textToListOfWords(mysteryText);

        List<String> sentencesList = authorshipDetector.textToListOfSentences(mysteryText);

        final double expected = wordsList.size() / (double) 4;
        double actual = authorshipDetector.averageSentenceLength(sentencesList, wordsList.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testAverageSentenceLengthWithZeroWords() {
        String testString = "";
        List<String> wordsList = authorshipDetector.textToListOfWords(testString);

        List<String> sentencesList = authorshipDetector.textToListOfSentences(testString);

        final double expected = 0.0;
        double actual = authorshipDetector.averageSentenceLength(sentencesList, wordsList.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testPhrasesInListOfSentences() {
        List<String> sentencesList = authorshipDetector.textToListOfSentences(mysteryText);

        final int expected = 3;
        int actual = authorshipDetector.phrasesInListOfSentences(sentencesList);

        assertEquals(expected, actual);
    }

    @Test
    public void testPhrasesInListOfSentencesSimple() {
        String testString = "This ; is , Test : sentence: with ;6 phrases";
        List<String> sentenceList = authorshipDetector.textToListOfSentences(testString);

        final int expected = 6;
        int actual = authorshipDetector.phrasesInListOfSentences(sentenceList);

        assertEquals(expected, actual);
    }

    @Test
    public void testAverageSentenceComplexity() {
        String testString = "This ; is , Test : sentence: with ;6 phrases";
        List<String> sentenceList = authorshipDetector.textToListOfSentences(testString);

        final double expected = 6 / (double) 1;
        int phrasesCount = authorshipDetector.phrasesInListOfSentences(sentenceList);
        double actual = authorshipDetector.averageSentenceComplexity(phrasesCount, sentenceList.size());

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testAverageSentenceComplexityWithZeroPhrases() {
        String testString = "Is is test sentence? with 6 phrases";
        List<String> sentenceList = authorshipDetector.textToListOfSentences(testString);

        double expected = 0.0;
        int phrasesCount = authorshipDetector.phrasesInListOfSentences(sentenceList);
        double actual = authorshipDetector.averageSentenceComplexity(phrasesCount, sentenceList.size());

        assertEquals(expected, actual, delta);
    }

    private LinguisticSignature makeLinguisticSignature(double averageWordLength, double typeTokenRatio,
                                                        double hapaxLegomenaRatio, double averageSentenceLength,
                                                        double averageSentenceComplexity) {
        final int featuresArraySize = 5;
        double[] featuresArray = new double[featuresArraySize];
        final int indexOfAverageWordLength = 0;
        final int indexOfTypeTokenRatio = 1;
        final int indexOfHapaxLegomenaRatio = 2;
        final int indexOfAverageSentenceLength = 3;
        final int indexOfAverageSentenceComplexity = 4;

        featuresArray[indexOfAverageWordLength] = averageWordLength;
        featuresArray[indexOfTypeTokenRatio] = typeTokenRatio;
        featuresArray[indexOfHapaxLegomenaRatio] = hapaxLegomenaRatio;
        featuresArray[indexOfAverageSentenceLength] = averageSentenceLength;
        featuresArray[indexOfAverageSentenceComplexity] = averageSentenceComplexity;

        Map<FeatureType, Double> featuresMap = new EnumMap<>(FeatureType.class);
        int i = 0;
        for (FeatureType type : FeatureType.values()) {
            featuresMap.put(type, featuresArray[i++]);
        }

        return new LinguisticSignature(featuresMap);
    }

    @Test
    public void testCalculateSimilarityWithIdenticalSignatures() {
        final double averageWordLength = 3.96868608302;
        final double typeTokenRatio = 0.0529378997714;
        final double hapaxLegomenaRatio = 0.0208217283571;
        final double averageSentenceLength = 22.2267197987;
        final double averageSentenceComplexity = 3.4129614094;

        LinguisticSignature signature1 = makeLinguisticSignature(averageWordLength, typeTokenRatio,
                hapaxLegomenaRatio, averageSentenceLength, averageSentenceComplexity);

        LinguisticSignature signature2 = makeLinguisticSignature(averageWordLength, typeTokenRatio,
                hapaxLegomenaRatio, averageSentenceLength, averageSentenceComplexity);

        final double expected = 0.0;

        double actual = authorshipDetector.calculateSimilarity(signature1, signature2);

        assertEquals(expected, actual, delta);
    }

    @Test
    public void testCalculateSimilarityWithDifferentSignatures() {
        final double averageWordLength1 = 4.4;
        final double typeTokenRatio1 = 0.1;
        final double hapaxLegomenaRatio1 = 0.05;
        final double averageSentenceLength1 = 10;
        final double averageSentenceComplexity1 = 2;

        final double averageWordLength2 = 4.3;
        final double typeTokenRatio2 = 0.1;
        final double hapaxLegomenaRatio2 = 0.04;
        final double averageSentenceLength2 = 16;
        final double averageSentenceComplexity2 = 4;

        LinguisticSignature signature1 = makeLinguisticSignature(averageWordLength1, typeTokenRatio1,
                hapaxLegomenaRatio1, averageSentenceLength1, averageSentenceComplexity1);

        LinguisticSignature signature2 = makeLinguisticSignature(averageWordLength2, typeTokenRatio2,
                hapaxLegomenaRatio2, averageSentenceLength2, averageSentenceComplexity2);

        final double expected = 12;

        double actual = authorshipDetector.calculateSimilarity(signature1, signature2);

        assertEquals(expected, actual, delta);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateSimilarityWithFirstSignatureNull() {
        final double averageWordLength2 = 4.3;
        final double typeTokenRatio2 = 0.1;
        final double hapaxLegomenaRatio2 = 0.04;
        final double averageSentenceLength2 = 16;
        final double averageSentenceComplexity2 = 4;

        final LinguisticSignature signature1 = null;

        LinguisticSignature signature2 = makeLinguisticSignature(averageWordLength2, typeTokenRatio2,
                hapaxLegomenaRatio2, averageSentenceLength2, averageSentenceComplexity2);

        authorshipDetector.calculateSimilarity(signature1, signature2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateSimilarityWithSecondSignatureNull() {
        final double averageWordLength1 = 4.4;
        final double typeTokenRatio1 = 0.1;
        final double hapaxLegomenaRatio1 = 0.05;
        final double averageSentenceLength1 = 10;
        final double averageSentenceComplexity1 = 2;

        LinguisticSignature signature1 = makeLinguisticSignature(averageWordLength1, typeTokenRatio1,
                hapaxLegomenaRatio1, averageSentenceLength1, averageSentenceComplexity1);

        final LinguisticSignature signature2 = null;

        authorshipDetector.calculateSimilarity(signature1, signature2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateSignatureWithNullParameter() {
        authorshipDetector.calculateSignature(null);
    }

    @Test
    public void testCalculateSignature() {
        String testString = "This is the\n" +
                "test string. It has : 2 phrases.";

        LinguisticSignature actual = authorshipDetector.calculateSignature(
                new ByteArrayInputStream(testString.getBytes()));

        List<String> listOfWords = authorshipDetector.textToListOfWords(testString);
        Map<String, Integer> wordsCountMap = authorshipDetector.wordCountMap(listOfWords);
        List<String> listOfSentences = authorshipDetector.textToListOfSentences(testString);
        final int allWordsCount = listOfWords.size();
        final int phrasesCount = authorshipDetector.phrasesInListOfSentences(listOfSentences);
        final int sentencesCount = listOfSentences.size();

        final double expectedAverageWordLength = authorshipDetector.averageWordLength(listOfWords);
        final double expectedTypeTokenRatio = authorshipDetector.typeTokenRatio(wordsCountMap, allWordsCount);
        final double expectedHapaxLegomenaRatio =
                authorshipDetector.hapaxLegomenaRatio(wordsCountMap, allWordsCount);
        final double expectedAverageSentenceLength =
                authorshipDetector.averageSentenceLength(listOfSentences, allWordsCount);
        final double expectedAverageSentenceComplexity =
                authorshipDetector.averageSentenceComplexity(phrasesCount, sentencesCount);

        LinguisticSignature expected = makeLinguisticSignature(expectedAverageWordLength, expectedTypeTokenRatio,
                expectedHapaxLegomenaRatio, expectedAverageSentenceLength, expectedAverageSentenceComplexity);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAuthorWithNullParameter() {
        authorshipDetector.findAuthor(null);
    }

    @Test
    public void testFindAuthor() {
        String testString = "This is the\n" +
                "test string. It has : 2 phrases.";
        final String expected = "Test Author";

        String actual = authorshipDetector.findAuthor(new ByteArrayInputStream(testString.getBytes()));

        assertEquals(expected, actual);
    }
}

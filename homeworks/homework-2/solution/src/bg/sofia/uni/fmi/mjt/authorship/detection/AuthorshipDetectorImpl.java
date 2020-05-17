package bg.sofia.uni.fmi.mjt.authorship.detection;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class AuthorshipDetectorImpl implements AuthorshipDetector {
    private Map<String, LinguisticSignature> authorsSignatures;
    private double[] weights;

    public AuthorshipDetectorImpl(InputStream signaturesDataset, double[] weights) {
        this.authorsSignatures = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(signaturesDataset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addNewAuthorSignature(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem with the input stream", e);
        }

        this.weights = weights;
    }

    private void addNewAuthorSignature(String line) {
        String[] tokens = line.split(",");
        LinguisticSignature signature = LinguisticSignature.createLinguisticSignature(line);
        authorsSignatures.put(tokens[0], signature);
    }

    private String readAllLinesWithStream(BufferedReader reader) {
        return reader.lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String readMysteryTextInString(InputStream mysteryText) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mysteryText))) {
            return readAllLinesWithStream(reader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can't find the file", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem with the input stream", e);
        }
    }

    @Override
    public LinguisticSignature calculateSignature(InputStream mysteryText) {
        if (mysteryText == null) {
            throw new IllegalArgumentException();
        }

        final int featuresArraySize = 5;
        double[] featuresArray = new double[featuresArraySize];
        final int indexOfAverageWordLength = 0;
        final int indexOfTypeTokenRatio = 1;
        final int indexOfHapaxLegomenaRatio = 2;
        final int indexOfAverageSentenceLength = 3;
        final int indexOfAverageSentenceComplexity = 4;

        String mysteryTextString = readMysteryTextInString(mysteryText);

        List<String> listOfWords = textToListOfWords(mysteryTextString);
        Map<String, Integer> wordsCountMap = wordCountMap(listOfWords);
        List<String> listOfSentences = textToListOfSentences(mysteryTextString);

        featuresArray[indexOfAverageWordLength] = averageWordLength(listOfWords);

        int allWordsCount = listOfWords.size();

        featuresArray[indexOfTypeTokenRatio] = typeTokenRatio(wordsCountMap, allWordsCount);
        featuresArray[indexOfHapaxLegomenaRatio] = hapaxLegomenaRatio(wordsCountMap, allWordsCount);
        featuresArray[indexOfAverageSentenceLength] = averageSentenceLength(listOfSentences, allWordsCount);

        int phrasesCount = phrasesInListOfSentences(listOfSentences);
        int sentencesCount = listOfSentences.size();
        featuresArray[indexOfAverageSentenceComplexity] = averageSentenceComplexity(phrasesCount, sentencesCount);

        Map<FeatureType, Double> featuresMap = new EnumMap<>(FeatureType.class);
        int i = 0;
        for (FeatureType type : FeatureType.values()) {
            featuresMap.put(type, featuresArray[i++]);
        }

        return new LinguisticSignature(featuresMap);
    }

    @Override
    public double calculateSimilarity(LinguisticSignature firstSignature, LinguisticSignature secondSignature) {
        if (firstSignature == null || secondSignature == null) {
            throw new IllegalArgumentException("Null parameter");
        }

        double sum = 0;

        int i = 0;
        for (FeatureType type : FeatureType.values()) {
            sum += abs(firstSignature.getFeatures().get(type) -
                    secondSignature.getFeatures().get(type)) * weights[i++];
        }

        final int base = 10;
        final int degree = 13;
        sum = sum * pow(base, degree);
        sum = Math.round(sum);
        sum = sum / pow(base, degree);

        return sum;
    }

    @Override
    public String findAuthor(InputStream mysteryText) {
        if (mysteryText == null) {
            throw new IllegalArgumentException("Null InputStream parameter");
        }

        LinguisticSignature signature = calculateSignature(mysteryText);

        String best = "";
        double bestCoefficient = Double.MAX_VALUE;
        for (Map.Entry<String, LinguisticSignature> entry : authorsSignatures.entrySet()) {
            double similarityCoefficient = calculateSimilarity(signature, entry.getValue());
            if (Double.compare(similarityCoefficient, bestCoefficient) < 0) {
                bestCoefficient = similarityCoefficient;
                best = entry.getKey();
            }
        }

        return best;
    }

    public static String cleanUp(String word) {
        return word.toLowerCase()
                .replaceAll("^[!.,:;\\-?<>#\\*\'\"\\[\\(\\]\\)\\n\\t\\\\]+" +
                        "|[!.,:;\\-?<>#\\*\'\"\\[\\(\\]\\)\\n\\t\\\\]+$", "");
    }

    List<String> textToListOfWords(String mysteryText) {
        List<String> listOfWords = null;
        String[] wordsArray = mysteryText.split("\\s+");
        listOfWords = Arrays.stream(wordsArray).map(AuthorshipDetectorImpl::cleanUp)
                .filter(el -> el.compareTo("") != 0).collect(Collectors.toList());
        return listOfWords;
    }

    double averageWordLength(List<String> listOfWords) {
        int counter = listOfWords.size();
        double sum = listOfWords.stream()
                .map(String::length)
                .reduce(0, Integer::sum);

        return counter == 0 ? 0 : (sum / counter);
    }

    Map<String, Integer> wordCountMap(List<String> listOfWords) {
        Map<String, Integer> map = new HashMap<>();

        for (String i : listOfWords) {
            Integer value = map.get(i);
            if (value == null) {
                map.put(i, 1);
            } else {
                map.put(i, value + 1);
            }
        }

        return map;
    }

    double typeTokenRatio(Map<String, Integer> wordsCountMap, int allWordsCount) {
        return allWordsCount == 0 ? 0 : wordsCountMap.keySet().size() / (double) allWordsCount;
    }

    double hapaxLegomenaRatio(Map<String, Integer> wordsCountMap, int allWordsCount) {
        int uniqueWordsCounter = 0;

        for (Map.Entry<String, Integer> i : wordsCountMap.entrySet()) {
            if (i.getValue() == 1) {
                ++uniqueWordsCounter;
            }
        }

        return allWordsCount == 0 ? 0 : uniqueWordsCounter / (double) allWordsCount;
    }

    List<String> textToListOfSentences(String mysteryText) {
        mysteryText = mysteryText.replace('\n', ' ');
        String[] sentencesArray = mysteryText.split("[!?.]+");

        return Arrays.stream(sentencesArray).map(String::strip)
                .filter(el -> el.compareTo("") != 0).collect(Collectors.toList());
    }

    double averageSentenceLength(List<String> listOfSentences, int allWordsCount) {
        return allWordsCount == 0 ? 0 : allWordsCount / (double) listOfSentences.size();
    }

    int phrasesInListOfSentences(List<String> listOfSentences) {
        return listOfSentences.stream()
                .map(el -> el.split("[,:;]+"))
                .filter(el -> el.length > 1)
                .map(el -> el.length)
                .reduce(0, Integer::sum);
    }

    double averageSentenceComplexity(int phrasesCount, int sentencesCount) {
        return sentencesCount == 0 ? 0 : phrasesCount / (double) sentencesCount;
    }
}

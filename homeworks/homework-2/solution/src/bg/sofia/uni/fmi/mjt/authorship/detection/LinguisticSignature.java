package bg.sofia.uni.fmi.mjt.authorship.detection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LinguisticSignature {
    private Map<FeatureType, Double> features;

    public LinguisticSignature(Map<FeatureType, Double> features) {
        this.features = features;
    }

    public Map<FeatureType, Double> getFeatures() {
        return features;
    }

    public static LinguisticSignature createLinguisticSignature(String line) {
        String[] tokens = line.split(",");
        Map<FeatureType, Double> features = new HashMap<>();
        final int indexOfAverageWordLength = 1;
        final int indexOfTypeTokenRatio = 2;
        final int indexOfHapaxLegomenaRatio = 3;
        final int indexOfAverageSentenceLength = 4;
        final int indexOfAverageSentenceComplexity = 5;
        features.put(FeatureType.AVERAGE_WORD_LENGTH, Double.parseDouble(tokens[indexOfAverageWordLength]));
        features.put(FeatureType.TYPE_TOKEN_RATIO, Double.parseDouble(tokens[indexOfTypeTokenRatio]));
        features.put(FeatureType.HAPAX_LEGOMENA_RATIO, Double.parseDouble(tokens[indexOfHapaxLegomenaRatio]));
        features.put(FeatureType.AVERAGE_SENTENCE_LENGTH, Double.parseDouble(tokens[indexOfAverageSentenceLength]));
        features.put(FeatureType.AVERAGE_SENTENCE_COMPLEXITY,
                Double.parseDouble(tokens[indexOfAverageSentenceComplexity]));
        return new LinguisticSignature(features);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinguisticSignature that = (LinguisticSignature) o;
        return Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(features);
    }
}

package bg.sofia.uni.fmi.mjt.youtube;

import bg.sofia.uni.fmi.mjt.youtube.model.TrendingVideo;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class YoutubeTrendsExplorer {

    private List<TrendingVideo> trendingVideos;

    /**
     * Loads the dataset from the given {@code dataInput} stream.
     */
    public YoutubeTrendsExplorer(InputStream dataInput) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput))) {
            this.trendingVideos = reader.lines().skip(1)
                    .map(TrendingVideo::createTrendingVideo).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Problem with the input stream", e);
        }
    }

    YoutubeTrendsExplorer(List<TrendingVideo> list) {
        this.trendingVideos = list;
    }

    /**
     * Returns all videos loaded from the dataset.
     **/
    public Collection<TrendingVideo> getTrendingVideos() {
        return trendingVideos;
    }

    //Връща ID-то на trending видеото с най-малко харесвания
    public String findIdOfLeastLikedVideo() {
        return trendingVideos.stream().min(Comparator.comparing(TrendingVideo::getLikes)).get().getId();
    }

    //Връща ID-то на най-одобряваното trending видео - като от броя харесвания вадим броя нехаресвания
    public String findIdOfMostLikedLeastDislikedVideo() {
        return trendingVideos.stream()
                .max(Comparator.comparingInt(a -> (int) (a.getLikes() - a.getDislikes())))
                .get().getId();
    }

    //Връща списък от заглавията на трите най-гледани trending видеа, подредени в намаляващ ред на гледанията
    public List<String> findDistinctTitlesOfTop3VideosByViews() {
        final int topNViewed = 3;
        return trendingVideos.stream()
                .sorted(Comparator.comparing(TrendingVideo::getViews).reversed())
                .limit(topNViewed)
                .map(TrendingVideo::getTitle)
                .collect(Collectors.toList());
    }

    //Връща ID-то на видеото с най-много тагове
    public String findIdOfMostTaggedVideo() {
        return trendingVideos.stream()
                .max(Comparator.comparing(a -> a.getTags().size()))
                .get().getId();
    }

    //Връща заглавието на най-рано публикуваното видео, станало trending преди да е събрало 100.000 гледания
    public String findTitleOfFirstVideoTrendingBefore100KViews() {
        final int becomeTrendingBeforeNViews = 100_000;
        return trendingVideos.stream()
                .filter(e -> e.getViews() < becomeTrendingBeforeNViews)
                .min(Comparator.comparing(TrendingVideo::getPublishDate))
                .get().getTitle();
    }

    //Връща ID-то на видеото, което най-често е било trending
    public String findIdOfMostTrendingVideo() {
        Map<String, Long> trendingCounter = trendingVideos.stream()
                .collect(Collectors.groupingBy(TrendingVideo::getId, Collectors.counting()));

        return trendingCounter.entrySet().stream()
                .max(Comparator.comparing(e -> e.getValue()))
                .get().getKey();
    }

}
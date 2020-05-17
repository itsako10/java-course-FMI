package bg.sofia.uni.fmi.mjt.youtube;

import bg.sofia.uni.fmi.mjt.youtube.model.TrendingVideo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import java.io.ByteArrayInputStream;

public class YoutubeTrendsExplorerTest {
    private YoutubeTrendsExplorer youtubeTrendsExplorer;

    private String input =
            "video_id\ttrending_date\ttitle\tpublish_time\ttags\tviews\tlikes\tdislikes\t\t\t\t\t\t\t\t\t\n" +
                    "Least liked video\t17.01.12\t" +
                    "Least liked video title\t" + "2017-11-28T19:27:30.000Z\t\"least|\"\"least liked\"\"\"\t" +
                    "1\t0\t9999999999\t\t\t\t\t\t\t\t\t\n" +

                    "Most liked least disliked video\t17.01.12\t" +
                    "Most liked least disliked video title\t" + "2017-12-28T19:27:30.000Z\t\"most|\"\"most liked" +
                    "\"\"|\"\"most liked disliked\"\"\"\t1\t9999999999\t0\t\t\t\t\t\t\t\t\t\n" +

                    "First video by views\t17.01.12\t" +
                    "First video by views title\t" + "2017-12-30T19:27:30.000Z\t\"views|\"\"most viewed" +
                    "\"\"|\"\"video views\"\"\"\t9999999999\t600\t10\t\t\t\t\t\t\t\t\t\n" +

                    "Second video by views\t17.01.12\t" +
                    "Second video by views title\t" + "2016-12-30T19:27:30.000Z\t\"views|\"\"second most viewed" +
                    "\"\"|\"\"video views\"\"\"\t9999999998\t1000\t100\t\t\t\t\t\t\t\t\t\n" +

                    "Third video by views\t17.01.12\t" +
                    "Third video by views title\t" + "2016-09-30T19:27:30.000Z\t\"views|\"\"third most viewed" +
                    "\"\"|\"\"video views\"\"\"\t9999999997\t99\t1\t\t\t\t\t\t\t\t\t\n" +

                    "Most tagged video\t17.01.12\t" +
                    "Most tagged video title\t" + "2016-10-30T19:27:30.000Z\t\"the most tags|\"\"tag" +
                    "\"\"|\"\"many tags\"\"|\"\"most tags\"\"|\"\"tags\"\"|\"\"most\"\"|\"\"" +
                    "the video with most tags\"\"\"\t100000\t100\t16\t\t\t\t\t\t\t\t\t\n" +

                    "First video trending before 100K views\t17.01.12\t" +
                    "First video trending before 100K views title\t" + "2010-12-28T19:27:30.000Z\t" +
                    "\"trend|\"\"become trend before 100k views" +
                    "\"\"|\"\"100k views\"\"\"\t60000\t110\t10\t\t\t\t\t\t\t\t\t\n" +

                    "Most trending video\t17.06.12\t" +
                    "Most trending video title\t" + "2018-10-26T19:27:30.000Z\t\"most|\"\"most liked" +
                    "\"\"|\"\"most liked disliked\"\"\"\t10\t10\t0\t\t\t\t\t\t\t\t\t\n" +

                    "Most trending video\t17.06.10\t" +
                    "Most trending video title\t" + "2018-10-26T19:27:30.000Z\t\"most|\"\"most liked" +
                    "\"\"|\"\"most liked disliked\"\"\"\t10\t10\t0";

    private byte[] byteInput = input.getBytes();

    @Before
    public void before() throws IOException {
        /* List<TrendingVideo> list = new ArrayList<>();

        TrendingVideo leastLikedVideo = TrendingVideo.createTrendingVideo("Least liked video\t17.01.12\t" +
                "Least liked video title\t" + "2017-11-28T19:27:30.000Z\t\"least|\"\"least liked\"\"\"\t" +
                "1\t0\t9999999999");

        TrendingVideo mostLikedLeastDislikedVideo =
                TrendingVideo.createTrendingVideo("Most liked least disliked video\t17.01.12\t" +
                        "Most liked least disliked video title\t" +
                        "2017-12-28T19:27:30.000Z\t\"most|\"\"most liked" +
                        "\"\"|\"\"most liked disliked\"\"\"\t1\t9999999999\t0");

        TrendingVideo firstVideoByViews = TrendingVideo.createTrendingVideo("First video by views\t17.01.12\t" +
                "First video by views title\t" + "2017-12-30T19:27:30.000Z\t\"views|\"\"most viewed" +
                "\"\"|\"\"video views\"\"\"\t9999999999\t600\t10");

        TrendingVideo secondVideoByViews = TrendingVideo.createTrendingVideo("Second video by views\t17.01.12\t" +
                "Second video by views title\t" + "2016-12-30T19:27:30.000Z\t\"views|\"\"second most viewed" +
                "\"\"|\"\"video views\"\"\"\t9999999998\t1000\t100");

        TrendingVideo thirdVideoByViews = TrendingVideo.createTrendingVideo("Third video by views\t17.01.12\t" +
                "Third video by views title\t" + "2016-09-30T19:27:30.000Z\t\"views|\"\"third most viewed" +
                "\"\"|\"\"video views\"\"\"\t9999999997\t99\t1");

        TrendingVideo mostTaggedVideo = TrendingVideo.createTrendingVideo("Most tagged video\t17.01.12\t" +
                "Most tagged video title\t" + "2016-10-30T19:27:30.000Z\t\"the most tags|\"\"tag" +
                "\"\"|\"\"many tags\"\"|\"\"most tags\"\"|\"\"tags\"\"|\"\"most\"\"|\"\"" +
                "the video with most tags\"\"\"\t100000\t100\t16");

        TrendingVideo firstVideoTrendingBefore100KViews =
                TrendingVideo.createTrendingVideo("First video trending before 100K views\t17.01.12\t" +
                        "First video trending before 100K views title\t" + "2010-12-28T19:27:30.000Z\t" +
                        "\"trend|\"\"become trend before 100k views" +
                        "\"\"|\"\"100k views\"\"\"\t60000\t110\t10");

        TrendingVideo mostTrendingVideo = TrendingVideo.createTrendingVideo("Most trending video\t17.06.12\t" +
                "Most trending video title\t" + "2018-10-26T19:27:30.000Z\t\"most|\"\"most liked" +
                "\"\"|\"\"most liked disliked\"\"\"\t10\t10\t0");
        TrendingVideo mostTrendingVideoDuplicate =
                TrendingVideo.createTrendingVideo("Most trending video\t17.06.10\t" +
                        "Most trending video title\t" + "2018-10-26T19:27:30.000Z\t\"most|\"\"most liked" +
                        "\"\"|\"\"most liked disliked\"\"\"\t10\t10\t0");

        list.add(leastLikedVideo);
        list.add(mostLikedLeastDislikedVideo);
        list.add(firstVideoByViews);
        list.add(secondVideoByViews);
        list.add(thirdVideoByViews);
        list.add(mostTaggedVideo);
        list.add(firstVideoTrendingBefore100KViews);
        list.add(mostTrendingVideo);
        list.add(mostTrendingVideoDuplicate);

        youtubeTrendsExplorer = new YoutubeTrendsExplorer(list); */

        youtubeTrendsExplorer = new YoutubeTrendsExplorer(new ByteArrayInputStream(byteInput));
    }

    @Test
    public void testFindIdOfLeastLikedVideo() {
        String expected = "Least liked video";
        assertEquals(expected, youtubeTrendsExplorer.findIdOfLeastLikedVideo());
    }

    @Test
    public void testFindIdOfMostLikedLeastDislikedVideo() {
        String expected = "Most liked least disliked video";
        assertEquals(expected, youtubeTrendsExplorer.findIdOfMostLikedLeastDislikedVideo());
    }

    @Test
    public void testFindDistinctTitlesOfTop3VideosByViews() {
        List<String> expected =
                Arrays.asList("First video by views title",
                        "Second video by views title", "Third video by views title");
        assertEquals(expected, youtubeTrendsExplorer.findDistinctTitlesOfTop3VideosByViews());
    }

    @Test
    public void testFindIdOfMostTaggedVideo() {
        String expected = "Most tagged video";
        assertEquals(expected, youtubeTrendsExplorer.findIdOfMostTaggedVideo());
    }

    @Test
    public void testFindTitleOfFirstVideoTrendingBefore100KViews() {
        String expected = "First video trending before 100K views title";
        assertEquals(expected, youtubeTrendsExplorer.findTitleOfFirstVideoTrendingBefore100KViews());
    }

    @Test
    public void testFindIdOfMostTrendingVideo() {
        String expected = "Most trending video";
        assertEquals(expected, youtubeTrendsExplorer.findIdOfMostTrendingVideo());
    }
}

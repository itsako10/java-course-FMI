package upr1;

import java.util.Arrays;

public class WordAnalyzer {
    public static char[] makeCharSet(char[] arr) {
        Arrays.sort(arr);

        String res = "";
        int j = 1;
        for(int i = 0; i < arr.length;) {
            res = res + arr[i];
           while(j < arr.length && arr[i] == arr[j]) {
               ++j;
           }
           i = j;
           ++j;
        }
        return res.toCharArray();
    }

    public static String getSharedLetters(String word1, String word2) {
        char[] str1 = word1.toLowerCase().toCharArray();
        char[] str2 = word2.toLowerCase().toCharArray();

        str1 = makeCharSet(str1);
        str2 = makeCharSet(str2);

        String result = "";

        int i = 0;
        int j = 0;
        while (i < str1.length && j < str2.length) {
            if (str1[i] == str2[j]) {
                result = result + str1[i];
                ++i;
                ++j;
            } else if (str1[i] < str2[j]) {
                ++i;
            } else {
                ++j;
            }
        }
        return result;
    }
}

package upr1;

public class FunnelChecker {
    public static boolean isFunnel(String str1, String str2) {
//        if(str1.substring(1).equals(str2)){
//            return true;
//        }
        //работи и като пусна цикъла от 0, а не от 1, и така избягвам
        //горе закоментираната проверка
        for(int i = 0; i < str1.length(); ++i) {
            String helper = str1.substring(0, i) + str1.substring(i + 1);
            if(helper.equals((str2))){
                return true;
            }
        }
        return false;
    }
}





package upr1;

public class FractionSimplifier{
    public static int gcd(int a, int b){
        if(a == 0) {
            return b;
        }

        if(b == 0) {
            return a;
        }

        if(a > b) {
            return gcd(a - b, b);
        }
        else {
            return gcd(a, b - a);
        }
    }

    public static String simplify(String fraction){
        String[] numbers = fraction.split("/");
        int num1 = Integer.parseInt(numbers[0]);
        int num2 = Integer.parseInt(numbers[1]);
        int gcd = gcd(num1, num2);
        num1 = num1 / gcd;
        num2 = num2 / gcd;

        if(num2 == 1){
            return Integer.toString(num1);
        }
        return Integer.toString(num1) + "/" + Integer.toString((num2));
    }
}

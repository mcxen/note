package template;

/**
 * 可以处理小数的大数加法(可以处理特殊字符)
 *
 * 美团二面手撕，华为机试
 */
public class BigNumberPlus {

    public static String bigNumberPlus (String s1, String s2) {
        String s1Big = s1.split("\\.")[0];
        String s1Small = null;
        if (s1.split("\\.").length != 1) {
            s1Small = s1.split("\\.")[1];
        }

        String s2Big =  s2.split("\\.")[0];
        String s2Small = null;
        if (s2.split("\\.").length != 1) {
            s2Small = s2.split("\\.")[1];
        }

        String ans;
        String small = smallFun(s1Small, s2Small);
        int c = Integer.parseInt(small.split("\\.")[0]);
        String big = bigFun(s1Big, s2Big, c);
        if (small.split("\\.").length == 1) {
            ans = big;
        } else {
            ans = big + "." +  small.split("\\.")[1];
        }
        return ans;
    }

    public static String bigFun(String s1, String s2, int smallC) {
        if (s1 == null || s1.length() == 0) return s2;
        if (s2 == null || s2.length() == 0) return s1;
        StringBuilder ans = new StringBuilder();
        int c = smallC;
        int i = s1.length() - 1;
        int j = s2.length() - 1;
        while (i >= 0 && j >= 0) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(j);
            int sum = calculate(c1, c2) + c;
            c = sum / 10;
            ans.insert(0, sum % 10);
            i--;
            j--;
        }
        while (i >= 0) {
            char c1 = s1.charAt(i);
            int sum = Character.getNumericValue(c1) + c;
            c = sum / 10;
            ans.insert(0, sum % 10);
            i--;
        }

        while (j >= 0) {
            char c2 = s2.charAt(j);
            int sum = Character.getNumericValue(c2) + c;
            c = sum / 10;
            ans.insert(0, sum % 10);
            j--;
        }
        if (c != 0) {
            ans.insert(0, c);
        }
        return ans.toString();
    }

    public static String smallFun(String s1, String s2) {
        if (s1 == null && s2 == null) return "0.";
        if (s1 == null || s1.length() == 0) {
            StringBuilder stringBuilder = new StringBuilder(s2);
            stringBuilder.insert(0, "0.");
            return stringBuilder.toString();
        }
        if (s2 == null || s2.length() == 0) {
            StringBuilder stringBuilder = new StringBuilder(s1);
            stringBuilder.insert(0, "0.");
            return stringBuilder.toString();
        }
        String s;
        if (s1.length() < s2.length()) {
            s = s2.substring(s1.length());
            s2 = s2.substring(0, s1.length());
        } else {
            s = s1.substring(s2.length());
            s1 = s1.substring(0, s2.length());
        }

        StringBuilder ans = new StringBuilder();
        int c = 0;
        int i = s1.length() - 1;
        int j = s2.length() - 1;
        while (i >= 0 && j >= 0) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(j);
            int sum = calculate(c1, c2) + c;
            c = sum / 10;
            ans.insert(0, sum % 10);
            i--;
            j--;
        }
        ans.insert(0, ".");
        ans.insert(0, c);
        ans.append(s);

        int k = ans.length() - 1;
        while (k >= 0 && ans.charAt(k) == '0') {
            k--;
        }
        ans.delete(k + 1, ans.length());
        return ans.toString();
    }

    public static int calculate(char c1, char c2) {
        if (Character.isDigit(c1) && Character.isDigit(c2)) {
            return Character.getNumericValue(c1) + Character.getNumericValue(c2);
        } else {
            // 特殊字符的加法规则，出自华为机试
            if (c1 == '!' && c2 == '!') {
                return 0;
            } else if ((c1 == '!' && c2 == '@') || (c1 == '@' && c2 == '!')) {
                return 13;
            } else if ((c1 == '!' && c2 == '#') || (c1 == '#' && c2 == '!')) {
                return 4;
            } else if (c1 == '@' && c2 == '@') {
                return 7;
            } else if ((c1 == '@' && c2 == '#') || c1 == '#' && c2 == '@') {
                return 20;
            } else if (c1 == '#' && c2 == '#') {
                return 5;
            } else {
                return -1;
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(bigNumberPlus("5.2", "5.00020"));;
    }
}

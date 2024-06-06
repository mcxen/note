package template;

import javax.lang.model.type.ArrayType;
import java.util.Arrays;

/**
 * 各类特殊DP模板
 */
public class DP {

    /**
     * 数位DP
     * isLimit 表示当前是否受到了 nn 的约束。若为真，则第 ii 位填入的数字至多为 s[i]s[i]，
     * 否则至多为 99。例如 n=234n=234，如果前面填了 2323，那么最后一位至多填 44；如果前面填的不是 2323，
     * 那么最后一位至多填 99。如果在受到约束的情况下填了 s[i]s[i]，那么后续填入的数字仍会受到 nn 的约束。
     * isNumisNum 表示 ii 前面的数位是否填了数字。若为假，则当前位可以跳过（不填数字），或者要填入的数字至少
     * 为 11；若为真，则必须填数字，且要填入的数字从 00 开始。这样我们可以控制构造出的是一位数/两位数/三位数等
     * 等。对于本题而言，要填入的数字可直接从 digitsdigits 中选择。
     *
     */
    static class NumberDP {
        char s[];
        int dp[][];

        /**
         * 统计小等于n的数字中1出现的次数
         * @param n
         * @return
         */
        public  int countDigitOne(int n) {
            s = String.valueOf(n).toCharArray();
            int m = s.length;
            dp = new int[m + 5][m + 5];
            for (int i = 0; i < dp.length; i++)
                Arrays.fill(dp[i], -1);
            return numDp(0, 0, true);
        }
        int numDp(int i, int cnt2, boolean isLimit) {
            if (i == s.length) return cnt2;
            if (!isLimit && dp[i][cnt2] >= 0) {
                System.out.println(i + "  " + cnt2);
                return dp[i][cnt2];
            }
            int res = 0;
            int up = isLimit ? s[i] - '0' : 9;
            for (int d = 0; d <= up; d++) {
                res += numDp(i + 1, cnt2 + (d == 2 ? 1 : 0), isLimit && d == up);
            }
            if (!isLimit) dp[i][cnt2] = res;
            return res;
        }

        int f(int i, int pre1, boolean isLimit) {
            if (i == s.length) return 1;
            if (!isLimit && dp[i][pre1] >= 0) {
                return dp[i][pre1];
            }
            int res = 0;
            int up = isLimit ? s[i] - '0' : 1;
            for (int d = 0; d <= up; d++) {
                if (pre1 == 1 && d == 1) break;
                res += f(i + 1, d, isLimit && d == up);
            }
            if (!isLimit) dp[i][pre1] = res;
            return res;
        }

        int numDp(int i, int mask, boolean isLimit, boolean isNum) {
            if (i == s.length)  {
                return isNum ? 1 : 0;
            }
            if (!isLimit && isNum && dp[i][mask] >= 0) {
                return dp[i][mask];
            }
            int res = 0;
            if (!isNum) {
                res = numDp(i + 1, mask, false, false);
            }
            int up = isLimit ? s[i] - '0' : 9;
            for (int d = (isNum ? 0 : 1); d <= up; d++) {
                if ((mask >> d & 1) == 0) {
                    res += numDp(i + 1, mask | (1 << d), isLimit && d == up, true);
                }

            }

            if (!isLimit && isNum) dp[i][mask] = res;
            return res;
        }
    }

    /**
     * 买卖股票的最佳时机
     */
    static class gupiaoDP {
        int [] prices;
        int [][][] dp;
        public int maxProfit(int k, int[] prices) {
            this.prices = prices;
            int n = prices.length;
            dp = new int[n][k + 1][2];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < dp[0].length; j++)
                    Arrays.fill(dp[i][j], -1);
            }
            return dfs(n - 1, k, false);
        }

        public int dfs (int i, int k, boolean isHold) {

            if (k < 0)
                return Integer.MIN_VALUE / 2;
            if (i < 0) {
                if (isHold) return Integer.MIN_VALUE / 2;
                else  return 0;
            }
            if (dp[i][k][isHold ? 1 : 0] != -1) return dp[i][k][isHold ? 1 : 0];
            if (isHold)
                return dp[i][k][1] = Math.max(dfs(i - 1, k, true), dfs(i - 1, k, false) - prices[i]);
            return dp[i][k][0] = Math.max(dfs(i - 1, k, false), dfs(i - 1, k - 1, true) + prices[i]);
        }
    }

}

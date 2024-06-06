package template;

import java.util.HashSet;
import java.util.Set;

/**
 * 宫水三叶的字符串hash + 前缀和
 * 比滚动hash简单很多
 */
public class StringHash {

    long[] h, p;

    // 找出最长的重复出现的子序列
    public String longestDupSubstring(String s) {
        int P = 1313131, n = s.length();
        h = new long[n + 10]; p = new long[n + 10];
        p[0] = 1;
        for (int i = 0; i < n; i++) {
            h[i + 1] = h[i] * P + s.charAt(i);
            p[i + 1] = p[i] * P;
        }

        String ans = "";
        int l = 0, r = n;
        // 二分枚举
        while (l < r) {
            int mid = l + r + 1 >> 1;
            String t = check(s, mid);
            if (t.length() != 0) l = mid;
            else r = mid - 1;
            ans = t.length() > ans.length() ? t : ans;
        }
        return ans;
    }

    // 检查字符串s中是否有长为len的重复出现的子序列，存在就返回
    String check(String s, int len) {
        int n = s.length();
        Set<Long> set = new HashSet<>();
        for (int i = 1; i + len - 1 <= n; i++) {
            int j = i + len - 1;
            long cur = h[j] - h[i - 1] * p[j - i + 1];
            if (set.contains(cur)) return s.substring(i - 1, j);
            set.add(cur);
        }
        return "";
    }
}

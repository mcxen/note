package template;

import java.util.HashSet;
import java.util.Set;

/**
 * 宫水三叶的字符串hash + 前缀和
 * 比滚动hash简单很多
 * 1044. 最长重复子串
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
class Solution {
    private static final long PRIME = 313;
    public String longestDupSubstring(String s) {
        //字符串哈希算法
        // hash = hash * PRIME + char -(pow * lastChar)
        // pow = Pow(prime,k)k个窗口大小，k次的prime
        String ans = "";
        int l = 0,r = s.length()-1;
        while (l<=r){
            int mid = (r+l+1)/2;
            String x = check(s,mid);
            if (!x.equals("")){
                l = mid+1;
                ans = x;
            }else {
                r = mid-1;
            }
        }
        return ans;
    }
    private String check(String s,int len){
        //查找len的长度，是不是有重复的字串，
        HashSet<Long> set = new HashSet<>();
        int n = s.length();
        long hash = 0,pow= 1;//hash记录对应字符串的哈希值，pow表示要减去的值
        for(int i=0;i<len;i++){
            hash = hash*PRIME + s.charAt(i);
            pow *= PRIME;//到时候移除滑动窗口时，lastChar要×这个
        }
        String ans = "";
        set.add(hash);
        for(int i=len;i<n;i++){
            hash = hash*PRIME + s.charAt(i) - s.charAt(i-len)*pow;
            String cur = s.substring(i-len+1,i+1);
            //再检测一下从头查找相同的子串，它的位置不是当前 i 的位置，说明确实是相同的子串
            if (set.contains(hash)&&s.indexOf(cur)!=i){
                ans = cur;
                return ans;
            }
            set.add(hash);
        }
        return ans;
    }
}
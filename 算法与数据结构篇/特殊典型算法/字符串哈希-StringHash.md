

# 方法、二分查找 + 自定义哈希

为什么可以使用 二分查找 + 自定义哈希 的方式解决。

**首先，我们来看一下二分查找。**

如果一个字符串中有可重复的子串，那么，我们可以从 (n-1) 的长度开始向下依次寻找每个长度是否存在重复的子串，比如，“abcabcdabbcdxyzx” 这个字符串我们可以先看长度 15 的子串存不存在，再看长度 14 的子串存不存在，。。。，直到看长度为 3 的子串才发现存在。

为什么是 n-1？举个例子，给定字符串为："aaaa"，最长重复子串是 "aaa"，长度为 3，而原字符串长度为 4。

可以看到，上面的过程是比较慢的，所以，我们要想办法提高效率。

这里我们就可以使用二分法来查找这个长度，比你依次慢慢缩短长度是有很大提升的。

举个例子：给定字符串为 "abcabcdd"，长度为8，先试下长度为 4 的有没有重复子串，没有，对不对，再试下长度为 2 的有没有重复子串，有重复子串 "ab" 或者 "bc" 但不是最长的，然后再试下长度为 3 的有没有重复子串，正好也有，那最长的应该改成是长度为 3 的那个重复子串，也就是 "abc"。

### 自定义哈希

**然后，我们再来看为什么要使用自定义哈希。**

其实，我一开始看了下 Java 中 String 类型的 hashCode() 方法：

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    
    /** The value is used for character storage. */
    private final char value[];

    /** Cache the hash code for the string */
    private int hash; // Default to 0
    
	public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }
}
```

发现它的 hash 值对于同一个字符串是有缓存的，而且，我们知道，相等的字符串在 JVM 中只存在一份，所以，我想能不能利用这个缓存帮我通过所有测试用例，然后，我就写了第一版的代码：

```java
class Solution {
    public String longestDupSubstring(String s) {
        String ans = "";
        int l = 0, r = s.length() - 1;
        while (l <= r) {
            int mid = (l + r + 1) / 2;
            String x = find(s, mid);
            if (!x.equals("")) {
                l = mid + 1;
                ans = x;
            } else {
                r = mid - 1;
            }
        }
        return ans;
    }

    private String find(String s, int len) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i <= s.length() - len; i++) {
            String substr = s.substring(i, i + len);
            int hash = substr.hashCode();
            if (set.contains(hash) && s.indexOf(substr) != i) {
                return substr;
            }
            set.add(hash);
        }
        return "";
    }
}
```

结果卡在了第 65 个用例上

投机取巧看来是不行了，还是老老实实地自定义哈希吧。

**最后，如何自定义哈希？**

其实，我们可以参考 Java 中 hashCode() 方法结合滑动窗口来写，问题在于，当滑动窗口往前移动的时候，如何把之前计算过的字母的 hash 值去掉？

```markdown
假设，h0 = 0，每个字符的hash为其值本身，我们需要求长度为3的子串的hash：
 a b c a b c d a
```
```c++
hash(a): h = 31 * h0 + 'a'
hash(ab): h = 31 * (31 * h0 + 'a') + 'b'
hash(abc): h = 31 * (31 * (31 * h0 + 'a') + 'b') + 'c'
hash(bca): h = 31 * h + 'a' - ?
```
```
这里应该减多少？
这里应该减 31 * 31 * 31 * 'a'
因为第一个 a 的 hash 值在计算到 hash(bca) 时贡献了 31^3 * 'a' 这么大，
也就是上面公式中的：31 * (31 * (31 * h0 + 'a') + 'b') + 'c') + 'a' - ?
```

好了，理清了上面这几个问题，再来看代码就简单多了.

### 完整实现：

```java
class Solution {

    private static final long PRIME = 31;

    public String longestDupSubstring(String s) {
        String ans = "";
        int l = 0, r = s.length() - 1;
        while (l <= r) {
            // mid 是要找的长度
            int mid = (l + r + 1) / 2;
            // 看是否能找到这么长的重复子串
            String x = find(s, mid);
            if (!x.equals("")) {
                // 找到了再看更长的子串，更短的不用看了
                l = mid + 1;
                ans = x;
            } else {
                // 没找到再看更短的子串
                r = mid - 1;
            }
        }
        return ans;
    }

    private String find(String s, int len) {
        String ans = "";
        Set<Long> set = new HashSet<>();
        long hash = 0;
        long power = 1;
        // 先计算前len个字符组成的子串的hash
        // 与java的String的hashCode()方法一样
        for (int i = 0; i < len; i++) {
            hash = hash * PRIME + s.charAt(i);
            power *= PRIME;
        }

        set.add(hash);
        
        // 滑动窗口向后移，每次移动要把移出窗口的减去，再加上新的
        for (int i = len; i < s.length(); i++) {
            hash = hash * PRIME + s.charAt(i) - power * s.charAt(i - len);
            // 如果包含相同的hash说明之前可能出现过相同的子串
            // 再检测一下从头查找相同的子串，它的位置不是当前 i 的位置，说明确实是相同的子串
            // 否则，可能是因为hash冲突导致的误判
            // 针对本题，不加indexOf这个判断也可以过，是因为没有出现这样的用例
            // 但是，为了逻辑更严谨，还是应该加上这个判断
            if (set.contains(hash) && s.indexOf(ans = s.substring(i - len + 1, i + 1)) != i) {
                return ans;
            }
            set.add(hash);
        }

        return ans;
    }
}
```

- 时间复杂度：O(logn∗n)，二分查找的时间复杂度为 O(logn)，每个长度都要用滑动窗口计算hash值的时间复杂度为 O(n)，所以，总体时间复杂度为 O(logn∗n)，也可以写成 O(nlogn)。
- 空间复杂度：O(n)，这里不是 O(logn∗n)，因为每次二分都会重新创建新的 Set，原来的 Set 会被释放回收掉。

### [1044. 最长重复子串](https://leetcode.cn/problems/longest-duplicate-substring/)

给你一个字符串 `s` ，考虑其所有 *重复子串* ：即 `s` 的（连续）子串，在 `s` 中出现 2 次或更多次。这些出现之间可能存在重叠。

返回 **任意一个** 可能具有最长长度的重复子串。如果 `s` 不含重复子串，那么答案为 `""` 。

**示例 1：**

```
输入：s = "banana"
输出："ana"
```

**示例 2：**

```
输入：s = "abcd"
输出：""
```



```java
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
```


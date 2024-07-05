## KMP算法

### KMP代码

```java
public class kmp {
    public static void main(String[] args) {
        String str1 = "BBCABCDABABCDABCDABDE";
        String str2 = "ABCDABD";
        System.out.println(Arrays.toString(kmpNext(str2)));
        System.out.println(kmpSearch(str1,str2,kmpNext(str2)));
    }
    public static int kmpSearch(String s1,String s2,int[] next){
        for (int i = 0,j=0; i < s1.length(); i++) {
            //不匹配就获取上一次的部分匹配表的上一位
            while (j>0&&s1.charAt(i)!=s2.charAt(j)) j =next[j-1];
            if (s1.charAt(i)==s2.charAt(j)) j++;
            if (j==s2.length()) return i-j+1;
        }
        return -1;
    }
    public static int[] kmpNext(String dest){
        int[] next = new int[dest.length()];
        next[0] = 0;//[ABCDABD],[A]的部分匹配表是0
        int j = 0;
        for (int i = 1; i < dest.length(); i++) {
            while (j>0&&dest.charAt(i)!=dest.charAt(j)){
                j = next[j-1];//看作是j以前的小串去匹配i的大串
            }
            if (dest.charAt(i)==dest.charAt(j)) j++;
            next[i]=j;
        }
        return next;
    }

}
```





### 部分匹配表

"部分匹配"的实质是，有时候，字符串头部和尾部会有重复。比如，"ABCDAB"之中有两个"AB"，那么它的"部分匹配值"就是2（"AB"的长度）。搜索词移动的时候，第一个"AB"向后移动4位（字符串长度-部分匹配值），就可以来到第二个"AB"的位置。



![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-14.png)

下面介绍《部分匹配表》是如何产生的。

首先，要了解两个概念："前缀"和"后缀"。 "前缀"指除了最后一个字符以外，一个字符串的全部头部组合；"后缀"指除了第一个字符以外，一个字符串的全部尾部组合。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-15.png)

"部分匹配值"就是"前缀"和"后缀"的最长的共有元素的长度。以"ABCDABD"为例，

```java
　　－　"A"的前缀和后缀都为空集，共有元素的长度为0；

　　－　"AB"的前缀为[A]，后缀为[B]，共有元素的长度为0；

　　－　"ABC"的前缀为[A, AB]，后缀为[BC, C]，共有元素的长度0；

　　－　"ABCD"的前缀为[A, AB, ABC]，后缀为[BCD, CD, D]，共有元素的长度为0；

　　－　"ABCDA"的前缀为[A, AB, ABC, ABCD]，后缀为[BCDA, CDA, DA, A]，共有元素为"A"，长度为1；

　　－　"ABCDAB"的前缀为[A, AB, ABC, ABCD, ABCDA]，后缀为[BCDAB, CDAB, DAB, AB, B]，共有元素为"AB"，长度为2；

　　－　"ABCDABD"的前缀为[A, AB, ABC, ABCD, ABCDA, ABCDAB]，后缀为[BCDABD, CDABD, DABD, ABD, BD, D]，共有元素的长度为0。
```

### 匹配流程


![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-1-20240415185713673.png)

首先，字符串 "BBC ABCDAB ABCDABCDABDE" 的第一个字符与搜索词 "ABCDABD" 的第一个字符，进行比较。因为 B 与 A 不匹配，所以搜索词后移一位。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-2.png)

因为 B 与 A 不匹配，搜索词再往后移。


![img](https://pdai.tech/images/alg/alg-kpm-3.png)

就这样，直到字符串有一个字符，与搜索词的第一个字符相同为止。

![img](https://pdai.tech/images/alg/alg-kpm-4.png)

接着比较字符串和搜索词的下一个字符，还是相同。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-5.png)

直到字符串有一个字符，与搜索词对应的字符不相同为止。

![img](https://pdai.tech/images/alg/alg-kpm-6.png)



这时，最自然的反应是，将搜索词整个后移一位，再从头逐个比较。这样做虽然可行，但是效率很差，因为你要把 "搜索位置" 移到已经比较过的位置，重比一遍。

![img](https://pdai.tech/images/alg/alg-kpm-7.png)

一个基本事实是，当空格与 D 不匹配时，你其实知道前面六个字符是 "ABCDAB"。KMP 算法的想法是，**设法利用这个已知信息，不要把 "搜索位置" 移回已经比较过的位置**，继续把它向后移，这样就提高了效率。

<img src="https://pdai.tech/images/alg/alg-kpm-8.png" alt="img" style="zoom:50%;" />

怎么做到这一点呢？可以针对搜索词，算出一张《部分匹配表》（Partial Match Table）。

<img src="https://pdai.tech/images/alg/alg-kpm-9.png" alt="img" style="zoom:67%;" />

已知空格与 D 不匹配时，前面六个字符 "ABCDAB" 是匹配的。查表可知，最后一个匹配字符 B 对应的 "部分匹配值" 为 2，因此按照下面的公式算出向后移动的位数：

**移动位数 = 已匹配的字符数 - 对应的部分匹配值**

因为 6 - 2 等于 4，所以将搜索词向后移动 4 位。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-10.png)

因为空格与Ｃ不匹配，搜索词还要继续往后移。这时，已匹配的字符数为 2（"AB"），对应的 "部分匹配值" 为 0。所以，移动位数 = 2 - 0，结果为 2，于是将搜索词向后移 2 位。

![img](https://pdai.tech/images/alg/alg-kpm-11.png)

因为空格与 A 不匹配，继续后移一位。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/alg-kpm-12.png)

逐位比较，直到发现 C 与 D 不匹配。于是，移动位数 = 6 - 2，继续将搜索词向后移动 4 位。

![img](https://pdai.tech/images/alg/alg-kpm-13.png)

逐位比较，直到搜索词的最后一位，**发现完全匹配，于是搜索完成。**如果还要继续搜索（即找出全部匹配），移动位数 = 7 - 0，再将搜索词向后移动 7 位，这里就不再重复了。

![img](https://pdai.tech/images/alg/alg-kpm-14.png)

### 459.重复的子字符串

[力扣题目链接](https://leetcode.cn/problems/repeated-substring-pattern/)

给定一个非空的字符串，判断它是否可以由它的一个子串重复多次构成。给定的字符串只含有小写英文字母，并且长度不超过10000。

示例 1:    
输入: "abab"    
输出: True      
解释: 可由子字符串 "ab" 重复两次构成。   

示例 2:     
输入: "aba"  
输出: False       

示例 3:      
输入: "abcabcabcabc"   
输出: True      
解释: 可由子字符串 "abc" 重复四次构成。 (或者子字符串 "abcabc" 重复两次构成。)

在一个串中查找是否出现过另一个串，这是KMP的看家本领。那么寻找重复子串怎么也涉及到KMP算法了呢？

KMP算法中next数组为什么遇到字符不匹配的时候可以找到上一个匹配过的位置继续匹配，靠的是有计算好的前缀表。 前缀表里，统计了各个位置为终点字符串的最长相同前后缀的长度。

那么 最长相同前后缀和重复子串的关系又有什么关系呢。 

可能很多录友又忘了 前缀和后缀的定义，再回顾一下： 

* 前缀是指不包含最后一个字符的所有以第一个字符开头的连续子串；
* 后缀是指不包含第一个字符的所有以最后一个字符结尾的连续子串

在由重复子串组成的字符串中，最长相等前后缀不包含的子串就是最小重复子串，这里拿字符串s：abababab 来举例，ab就是最小重复单位，如图所示： 

![图三](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/20220728205249-20240416213459911.png)

如何找到最小重复子串

这里有同学就问了，为啥一定是开头的ab呢。 其实最关键还是要理解 最长相等前后缀，如图： 

![图四](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/20220728212157.png)

步骤一：因为 这是相等的前缀和后缀，t[0] 与 k[0]相同， t[1] 与 k[1]相同，所以 s[0] 一定和 s[2]相同，s[1] 一定和 s[3]相同，即：，s[0]s[1]与s[2]s[3]相同 。 

步骤二： 因为在同一个字符串位置，所以 t[2] 与 k[0]相同，t[3] 与 k[1]相同。 

步骤三： 因为 这是相等的前缀和后缀，t[2] 与 k[2]相同 ，t[3]与k[3] 相同，所以，s[2]一定和s[4]相同，s[3]一定和s[5]相同，即：s[2]s[3] 与 s[4]s[5]相同。

步骤四：循环往复。 

所以字符串s，s[0]s[1]与s[2]s[3]相同， s[2]s[3] 与 s[4]s[5]相同，s[4]s[5] 与 s[6]s[7] 相同。 

正是因为 最长相等前后缀的规则，当一个字符串由重复子串组成的，最长相等前后缀不包含的子串就是最小重复子串。  



```java
class Solution {
    public boolean repeatedSubstringPattern(String s) {
        //KMP算法
        int n = s.length();
        int[] next = new int[n];
        int j= 0;
        for (int i = 1; i < n; i++) {
            while (j>0&&s.charAt(i)!=s.charAt(j)){
                j  = next[j-1];//回退
            }
            if (s.charAt(i)==s.charAt(j)){
                j++;
            }
            next[i]=j;
        }
        if (next[n -1]!=0){
            return n%(n-next[n-1])==0;
        }else {
            return false;
        }
    }
}
```
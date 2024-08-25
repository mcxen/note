# PDD

# 第2题-正整数数列

### 题目内容

塔子哥有一列正整数组成的数列，支持两种操作：

选取一个偶数，使其值减半

选取两个数字，移除并替换为两个数字的和

塔子哥最终希望能够得到一个全为奇数的数列，请计算最少需要操作几次

### 输入描述

第一行一个数字$T$，代表测试用例组数$(0<t≤10)$

对于每个测试用例：

第一行为$n$，代表数组长度，$0<n≤10^5)$

第二行$n$个正整数，

### 输出描述

对于每个测试用例，输出一个数字，代表最少需要操作次数

### 示例1

**输入**

```
3
3
2 4 4
2
1 9
5
1 2 3 4 5
```

**输出**

```
3
0
2
```

**说明**

## 思路:思维

1.如果有奇数:直接合并是最少的，因为只要一次就能减少一个奇数，而除2操作至少需要1次才能消除一个偶数 2.如果没有奇数:先找到含最少2因子的数，把他除2得到奇数，然后按照情况1来做。

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PDD2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        int T = in.nextInt();
        while (T-->0){
            int n = in.nextInt();
            long[] nums = new long[n];
            int ops = 0;
            int odd = 0,fine = 0;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                nums[i] = in.nextLong();
                if (nums[i]%2==0){
                    fine++;
                }else {
                    odd++;
                }
                int cur = 0;
                //最快除以2得到奇数的情况。也就是2的因子最小的
                while (nums[i]%2==0){
                    nums[i]/=2;
                    cur++;
                }
                min = Math.min(min,cur);
            }
            if (odd==n){
                System.out.println(0);
            } else if (fine == n) {
                System.out.println("min = " + min);
                System.out.println(min+fine-1);
            }else {
                System.out.println(fine);
            }
        }
    }
}

```

‍

‍

# 第4题-字符串

## 题目内容

给定长度为$n$的$01$串，定义一次操作为，将整个字符串按顺序分为两部分，将两部分各自翻转后再按原顺序拼接。

提问，进行任意次的操作后，可以得到的最长的连续的$01$交替的子串有多长。

例：原$01$串为$01001$，可以先将原串分为$010$和$01$两部分，分别翻转得到$010$和$10$,按原顺序拼接后得到$01010$，此时最长的连续交替子串为$01010$，长度为$5$

## 输入描述

第一行输入$n$表示输入的$01$串的长度。（$1≤n≤2*10^6$）

第二行输入长度为$n$的$01$串

## 输出描述

输出一个数字表示可能得到的最长的交替$01$子串的长度。

### 样例1

**输入**

```
5
10010
```

**输出**

```
5
```

**说明**

原字符串分为$10$和$010$，分别翻转得到$01$和$010$，拼接后为$01010$

## 思路：思维题

1.考虑将例子中的10010 复制一份得到1001010010。

2.将$10010$分为$10$与$010$翻转并拼接变为$01010$。

这个操作实际上就是$1001010010$中的第3到第7位。

进而我们发现可以将字符串看成一个环(首尾相连)，那么任意一次操作都可以在环上找到对应的子串，于是就可以化环为链，在环上找最长的连续交替01串。

这里实际就是将字符串 s 进行 s +\= s 操作实际上是将字符串 s 自己拼接到自身的末尾，即生成一个长度加倍的新字符串。这个操作在某些特定的算法中可以模拟“环”的效果，尤其在处理循环或环形结构的问题时特别有用。

> 这个题目是寻找最长01串里面交替的01串，那s+\=s的话，0101加一倍，01010101不就是长度为8了嘛？
>
> `假设你有一个字符串 s = "0101"，执行 s += s 之后，字符串变成 s = "01010101"。这时候，如果直接在这个加倍后的字符串上查找最长的交替 01 串，确实会找到长度为8的交替串。`​
>
> 查找到加倍后的字符串中的最长交替 01 串之后，代码使用 min(ans, n) 确保最终的输出不会超过原始字符串的长度 n。

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        String s = sc.next();
        s += s; // 化环成链
        int ans = 1;
        int tmp = 1;
      
        for (int i = 1; i < s.length(); i++) { // 找最长01串
            if (s.charAt(i) == s.charAt(i - 1)) {
                tmp = 1;
            } else {
                tmp++;
                ans = Math.max(ans, tmp);
            }
        }
      
        System.out.println(Math.min(ans, n)); // 最长串长不能超过n
        sc.close();
    }
}
```

‍

1\

题目要求：

小M对 gcd（最大公约数）很感兴趣，她会询问你  t  次。每次询问给出一个大于 1 的正整数  n ，你是否找到一个数字  m  （2 ≤  m  ≤  n ），使得  gcd(n, m)  为素数。

输入描述：

	•	每个测试文件中包含多个组测试数据。第一行输入一个整数  t （1 ≤  t  ≤ 100），代表数据组数。
	•	每组测试数据在一行上输入一个整数  n （2 ≤  n  ≤  10^5 ）代表给定的数字。

输出描述：

	•	对于每一组测试数据，在一行上输出一个整数，代表数字  m 。
	•	如果有多种合法答案，您可以输出任意一种。

```java
public class MT1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int t = in.nextInt();
            for (int i = 0; i < t; i++) {
                int n = in.nextInt();
                System.out.println(find(n));
            }
        }
    }
    static int find(int n){
        for (int m = 2; m < n + 1; m++) {
            if (ifPrime(gcd(n,m))){
                return m;
            }
        }
        return -1;
    }

    private static boolean ifPrime(int gcd) {
        if (gcd<=1) return false;
        if (gcd==2||gcd==3) return true;
        if (gcd%2==0||gcd%3==0) return false;
        for (int i = 5; (i*i) <= gcd; i+=6) {
            if (gcd%i==0||gcd%(i+2)==0) return false;
        }
        return true;
    }

    private static int gcd(int a, int b) {
        while (b!=0){
            int tmp = b;
            b = a%b;
            a = tmp;
        }
        return a;
    }
}
```







2、

```java
public class MT2 {
    //最小极差就是0，所有元素都等于
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int n = in.nextInt();
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) {
                nums[i] = in.nextInt();
            }
            Arrays.sort(nums);
            int minOps = Integer.MAX_VALUE;
//            int maxDif = nums[n-1]-nums[0];
//            while (nums[n-1]-nums[0]>0){
//                nums[n-1]--;
//                nums[0]++;
//                minOps++;
//                Arrays.sort(nums);
//                if (maxDif == nums[n-1]-nums[0]&&maxDif==1){
//                    break;
//                }
//                maxDif = nums[n-1]-nums[0];
//            }
            for (int i = 0; i < n - 1; i++) {
                int curops = (nums[i]-nums[0])+(nums[n-1]-nums[i+1]);
                minOps = Math.min(minOps,curops);
            }
            // 4 4 5
            // 5 4 4 -> 4 4 5
            // 1 2 3 4 5
            // 2 2 3 4 4 +1
            // 3 2 3 4 3 +1
            // 2 3 3 3 4
            // 3 3 3 3 3 +1
            System.out.println(minOps);
        }
    }
}
```





3、

小M和T在玩一个游戏，游戏中有一个长度为  n  的数组 \( $a_1, a_2, \dots, a_n$ \) 和一个固定的整数  k 。游戏规则如下，双方都会执行最优策略：

1.	第一步，M选择一个非空的区间 [l, r]，将这个区间中的所有数字都乘上  k 。
2.	第二步，T选择一个非空的区间 [$l{\prime}, r{\prime}$]，将这个区间中的所有数字都乘上  k 。

记  $\text{sum} = \sum_{i=1}^n a_i$ ，M想要让 sum 尽可能大，T则想让 sum 尽可能小。你需要求出最终的 sum 的值。

输入描述：

第一行输入两个整数  n  和  k  (1 ≤  n  ≤ 1000; -10^5 ≤  k  ≤  10^5) 代表数组长度和固定的整数。
•	第二行输入  n  个整数 \( $a_1, a_2, \dots, a_n$ \) ($-10^5 ≤  a_i  ≤  10^5$) 代表数组。

```java
import java.util.Scanner;

public class MT3Plus1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int n = in.nextInt();
            int k = in.nextInt();
            int[] nums = new int[n];
            for (int i = 0; i < n; i++) {
                nums[i] = in.nextInt();
            }
            int maxSum = Integer.MIN_VALUE;
            for (int l1 = 0; l1 < n; l1++) {
                for (int r1 = l1; r1 < n; r1++) {
                    int[] mod = nums.clone();
                    for (int i = l1; i <= r1; i++) {
                        mod[i]*=k;
                    }
                    int minSum = Integer.MAX_VALUE;
                    for (int l2 = 0; l2 < n; l2++) {
                        for (int r2 = l2; r2 < n; r2++) {
                            int cursum = 0;
                            for (int i = 0; i < n; i++) {
                                if (i>=l2&&i<=r2){
                                    cursum+=mod[i]*k;
                                }else {
                                    cursum+=mod[i];
                                }
                            }
                            minSum = Math.min(minSum,cursum);
                        }
                    }
                    maxSum = Math.max(maxSum,minSum);
                }
            }
            System.out.println(maxSum);
        }
    }
}

```


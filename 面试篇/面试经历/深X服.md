# 公司背景

中国卓越雇主、A股上市公司，云计算、网络安全万亿赛道

总部位于深圳，全球8000+名员工，业务覆盖全球50多个国家和地区，拥有海内外超10w家政府、教育、医疗、知名互联网企业等客户。



# 提前批

## 笔试

### 1、LeetCode 76 最小覆盖子串讲解思路

[76. 最小覆盖子串](https://leetcode.cn/problems/minimum-window-substring/)

给你一个字符串 `s` 、一个字符串 `t` 。返回 `s` 中涵盖 `t` 所有字符的最小子串。如果 `s` 中不存在涵盖 `t` 所有字符的子串，则返回空字符串 `""` 。

**注意：**

- 对于 `t` 中重复字符，我们寻找的子字符串中该字符数量必须不少于 `t` 中该字符数量。
- 如果 `s` 中存在这样的子串，我们保证它是唯一的答案。

**示例 1：**

```
输入：s = "ADOBECODEBANC", t = "ABC"
输出："BANC"
解释：最小覆盖子串 "BANC" 包含来自字符串 t 的 'A'、'B' 和 'C'。
```

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class SXF {
    //最小匹配的字符串
    public String substr (String a, String b) {
        char[] s = a.toCharArray();
        int m = s.length;
        int ansLeft = -1,ansRight = m;
        int left = 0,less = 0;
        int[] cntS = new int[128];
        int[] cntT = new int[128];
        for (char c : b.toCharArray()) {
            if (cntT[c]++==0){
                less++;
            }
        }
        for (int right = 0; right < m; right++) {
            char c = s[right];
            if (++cntS[c]==cntT[c]){
                less--;
            }
            // 当less为0时，说明当前窗口已经包含了所有目标字符
            while (less==0){
                if (right-left<ansRight-ansLeft){ // 更新最优解的左右边界
                    ansLeft = left;
                    ansRight = right;
                }
                char x = s[left++];// 移动窗口的左边界
                if (cntS[x]--==cntT[x]){
                    less++;
                }
            }
        }
        return ansLeft<0?"":a.substring(ansLeft,ansRight+1);
    }


}
```

```java
if (cntS[x]-- == cntT[x]) {
    less++;
}
```

如果上述比较成立（即窗口中字符`x`的计数在递减之前正好等于目标字符串`b`中字符`x`的计数），则说明窗口在收缩后不再满足包含所有目标字符的条件，需要增加`less`计数。



### 2、任务分割

> https://blog.csdn.net/qq_51625007/article/details/133266924

有 n 个任务，序号从 1 到 n,每个任务需要的编辑时间为ti分钟。小明和小白需要在对其中k 个任务中进行编辑。编辑的过程如下: n 个任务按照顺序排列，他们删除 n-k 个任务，不改变剩下的k 个任务的顺序。然后小明选取全部任务的前面一部分任务(可能不选或所有任务)，小白选取剩余的。相当于将任务从某个位置分割成两部分，第一部分给小明，第二部分给小白。

比如说是6个任务，1 1 1 1 8 10 删除掉了6-3 3个任务，剩下的还有 1  1  1 

然后最短时间是  1+1 = 2;

> 再比如：5 3
> 1 13 5 12 3
>
> 1 3 5 12 13 
>
> 删除5-3= 2个任务,留下3个任务
>
> 1 3 5  结果是6

之后他们分别对各自的任务进行编辑，编辑需的时间取决于两者中较长的那个。
请帮助小明和小白选择任务和分割片式使编辑尽可能早地完成。

输入描述
第一行包含一个整数T(1 <=T<= 10,表示测试用例的数量
每个测过用例的第一行包含两个整数n和k(1<=k<=n<= 3x105)，**表示完整任务集中的任务数量**与**小明和小回将进行编辑的任务数量。**
第二行包含n 个整数t1,t2,tn(1<= ti<= 109),表示每个编辑所需的时间。所有测试用例中n的总和天百3x105
输出描述
对于每个测试用例输出一个整数，表示如果小明和小白选择n 个任务中的 k 个问题进行编辑，并将它们分配给各白完成编辑所需的最短时间。

```
示例1

输入
5
6 3
8 1 10 1 1 1
5 3
1 13 5 12 3
10 6
10 8 18 13 3 8 6 4 14 12
10 5
9 9 2 11 14 33 4 99 14 12
1 1
1

输出
2
6
21
18
1
```



解法：
留下k个较小的数，再进行分割（前缀和计算）
但是，最优解不一定是留下k个较小的数

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
/*
5
6 3
8 1 10 1 1 1
5 3
1 13 5 12 3
10 6
10 8 18 13 3 8 6 4 14 12
10 5
9 9 2 11 14 33 4 99 14 12
1 1
1
 */
class Main4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int T = scanner.nextInt();

        for (int t = 0; t < T; t++) {
            int n = scanner.nextInt();
            int k = scanner.nextInt();
            int[] tasks = new int[n];
            for (int i = 0; i < n; i++) {
                tasks[i] = scanner.nextInt();
            }

            int ans = minEditTime(n, k, tasks);
            System.out.println(ans);
        }
    }

    public static int minEditTime(int n, int k, int[] tasks) {

        ArrayList<Integer> list=new ArrayList<>();
        for (int i = 0; i < tasks.length; i++) {
            list.add(tasks[i]);
        }

        Arrays.sort(tasks);

        for (int i = 0; i < n-k; i++) {
            list.remove(new Integer(tasks[tasks.length-1-i]));
        }//删除掉后面的几个任务。

//        System.out.println(list);

        Integer[] kTasks=new Integer[k];
        list.toArray(kTasks);//得到前面k个任务，然后分割得到最小的和

        return solve(kTasks);

    }

    public static int solve(Integer[] tasks){
        int res=Integer.MAX_VALUE;

        //前缀和
        int[] dp=new int[tasks.length];
        dp[0]=tasks[0];
        for (int i = 1; i < tasks.length; i++) {
            dp[i]=dp[i-1]+tasks[i];
        }

        for (int i = 0; i < dp.length; i++) {
            //计算当前分割点i的两个子数组的和。
            //取两个子数组和中的较大值。这代表了当前分割点i下，两组任务时间和的最大值。
            //取当前res和新计算出的不均衡值中的较小值。
            //这确保了res始终保持为所有可能分割点中的最小不均衡值。
            res=Math.min(res,Math.max(dp[i],dp[dp.length-1]-dp[i]));
            //dp[dp.length-1] - dp[i]：表示第二个子数组的和
        }

        return res;
    }


}
```



### 3、判断IP的联通关系

在计算机中，主机与主机之间通过p地址与网络来连接彼此，任意一台主机会通过ping命令去测试是否与另一台主机连通。而当给定了大批量的网络地址对后，网络管理员也需要快速地判断任意一对ip之间是否存在连通性。

 

例3

例如:ip为203.0.113.0的主机和ip为198.51.100.0的机器存在连通性，而ip为198.51.100.0的机器又与10.0.0.0这台机器存在连通性，那么由于网络连通的传递性，203.0.113.0就与10.0.0.0存在连通可能性。

而ip为172.16.0.0的主机与45.79.0.0的主机存在连通性192.0.2.0的主机与104.236.0.0的主机存在连通性，其中一对ip中没有任意一个ip能与另一对ip的其中一个ip连通，因

2

此172.16.0.0和192.0.2.0就不存在连通性。现在给定n任意IP地址，判断它们之间是否能够连通，以及最短的连通跳数(跳数:连通所经过的主机数，如果A-B直连，则定义其为1，A-B经过C连接，则为2，以此类推，主机相同时其跳数为0)

第一行包含两个整数n和m，表示已知的lP地址数量和连通关系

↓输入描述

接下来n行，每行包含一个字符串和一个整数，表示一个IP地址数量。

和它的编号(编号范围[1,n]且不会重复)。

接下来m行，每行包含两个整数a和b，表示IP地址对应的编号。

一个整数q，表示需要判断连通性的IP地址数和b之间有连通关系。

接下来q行，每行包含两个字符串，表示需要判断连通性的两!接下来一行包含一量。

 



```java
import java.util.*;

public class SXF3 {
   public static HashMap<String, List<String>> graph = new HashMap<>();

    static String[] ips;
    static HashMap<String,Integer> map = new HashMap<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();//ip
        int m = scanner.nextInt();
        scanner.nextLine();
        ips = new String[n];
        for (int i = 0; i < n; i++) {
            String s = scanner.nextLine();
//            System.out.println("s.split(\" \")[0] = " + s.split(" ")[0]);
            s = s.split(" ")[0];
//            System.out.println("s = " + s);
            ips[i] = s;
            map.put(s,i);
            graph.put(s,new LinkedList<>());
        }

        for (int i = 0; i < m; i++) {
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            String s1 = ips[a-1];
            String s2 = ips[b-1];
            graph.get(s1).add(s2);
            graph.get(s2).add(s1);
        }
//        for (int i = 0; i < n; i++) {
//            System.out.println(graph.get(ips[i]).toString());
//        }
        int q = scanner.nextInt();
        for (int i = 0; i < q; i++) {
            String s1 = scanner.next();
            String s2 = scanner.next();
//            System.out.println("s1 = " + s1);
//            System.out.println("s2 = " + s2);
            System.out.println(isConeect(s1,s2));
        }
    }
    static class State{
        int id;
        int distFormStart;

        public State(int id, int distFormStart) {
            this.id = id;
            this.distFormStart = distFormStart;
        }
    }
    private static int isConeect(String s1, String s2) {
        if (!graph.containsKey(s1)||!graph.containsKey(s2)) return -1;
        int[] distTo = new int[graph.size()];
        Arrays.fill(distTo,Integer.MAX_VALUE);
        int start = 0;
        int end = 0;
        start =map.get(s1)-1;
        end = map.get(s2)-1;
        distTo[start] = 0;
        PriorityQueue<State> pq = new PriorityQueue<>((a, b) -> (a.distFormStart - b.distFormStart));
        pq.offer(new State(start,0));
        while (!pq.isEmpty()){
            State curState = pq.poll();
            int id = curState.id;
            int curDist = curState.distFormStart;
            if (curDist>distTo[id]){
                continue;
            }
            for (String s : graph.get(s1)) {
                Integer neiId = map.get(s);
                int distToNei = distTo[id] + 1;
                if (distTo[neiId]>distToNei){
                    distTo[neiId]=distToNei;
                    pq.offer(new State(neiId,distToNei));
                }
            }
        }
        return distTo[end]==Integer.MAX_VALUE?-1: distTo[end];
    }
}

```





### 4、汉诺塔的规则:
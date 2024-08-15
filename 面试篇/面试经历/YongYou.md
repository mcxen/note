## Yongyou

> 2024-8

### 2、公司电力网络

公司负责维护一个nn的网格组成，每个单元格代表一个区域。区域中有些有电力设施（用 1 表示），有些则没有（用 0 表示）。为了保证公司的电力供应，必须让每个没有电力设施的区域能够尽量靠近一个有电力设施的区域。

你需要找到一个没有电力设施的区域，这个区域到最近的有电力设施的区域的距离是最大的，并返回这个距离。如果整个公司都是有电力设施的区域或者没有任何电力设施的区域，输出 -1。

输入描述：

1. 输入一个整数 N，表示网格的大小。
2. 输入一个 n*n的矩阵，矩阵中的每个元素表示该区域是否有电力设施（0 代表没有电力设施，1 代表有电力设施）。

输出描述： 输出一个整数 distance，代表所有无电力设施区域中到最近的有电力设施区域距离的最大值。

这道题目要求在一个  的网格中找到没有电力设施的区域中，距离最近的有电力设施的区域的最大距离。题目的关键在于利用广度优先搜索（BFS）算法从所有有电力设施的区域（标记为 1）同时出发，逐层扩展，直至找到最远的无电力设施区域（标记为 0）。最终结果是这个最远区域的最大距离。如果整个网格中没有无电力设施的区域或者全部都是无电力设施的区域，返回 -1。



```java
public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int n = in.nextInt();
            int[][] nums = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nums[i][j] = in.nextInt();
                }
            }
            Queue<int[]> queue = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (nums[i][j]==1){
                        queue.offer(new int[]{i,j});//记录为1的所有的城市
                    }
                }
            }
            if (queue.isEmpty()||queue.size()==n*n){
                System.out.println(-1);
                return;
            }
            int[][] dir = {{0,1},{1,0},{0,-1},{-1,0}};
            int[] cur = null;
            while (!queue.isEmpty()){
                cur = queue.poll();
                int x = cur[0],y = cur[1];
                for (int[] d : dir) {
                    int newx = x + d[0];
                    int newy = y + d[1];
                    if (newx>=0&&newx<n&&newy>=0&&newy<n&&nums[newx][newy]==0){
                        nums[newx][newy] = nums[x][y]+1;//如果在BFS的时候发现一个没有电的城市
                        //就把城市的电力改为2；
                        queue.offer(new int[]{newx,newy});
                    }
                }
            }
            System.out.println(nums[cur[0]][cur[1]]-1);
        }


    }
```





### 3、赛车游戏

这道题目描述了一场赛车比赛，小友需要在比赛中从起点出发，尽可能少地停靠加油站，以确保能够到达终点。题目要求计算小友最少需要停靠几次加油站才能顺利到达终点。

题目总结：

1. 输入：
   - 第一行：目标地点 target 的距离，单位为公里。
   - 第二行：初始燃料 startFuel，单位为升。
   - 第三行：加油站列表 stations，每个加油站以 “距离,燃料” 的形式表示，距离是指加油站距起点的距离，燃料表示该加油站提供的燃料量。
2. 任务：
   - 小友的目标是到达终点。每行驶一公里会消耗一升燃料。
   - 小友可以选择在某些加油站停靠加油。
   - 问题是：小友最少需要停几次加油站，才能确保能够到达终点？如果无法到达，返回 -1。
3. 输出：
   - 最少停靠的加油站次数。如果无法到达终点，输出 -1。

示例解释：

- 示例 1：
  - 输入：target = 10, startFuel = 5, stations = [[5,5]]
  - 输出：1
  - 解释：小友起始燃料为5升，可以到达第一个加油站（5公里处），然后加油5升，之后可以到达终点。



```java
public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNext()) { // 注意 while 处理多个 case
            int target = in.nextInt();
            int startFuel = in.nextInt();
//            in.next();
            List<int[]> list = new LinkedList<>();
            while (in.hasNext()){
                String[] split = in.next().split(",");
                System.out.println("split = " + split[0]);
                list.add(new int[]{Integer.parseInt(split[0]),Integer.parseInt(split[1])});
            }
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> (b - a));
            int fuel = startFuel,stops = 0,i = 0;
            while (fuel<target){
                while (i<list.size()&&list.get(i)[0]<=fuel){
                    maxHeap.offer(list.get(i)[1]);
                    //这里直接就是找出所有的当前能跑的路程范围内能跑到的最远的距离。
                    i++;
                }
                if (maxHeap.isEmpty()){
                    System.out.println(-1);
                    return;
                }
                fuel+=maxHeap.poll();
                stops++;
            }
            System.out.println(stops);
        }
    }
```





### 4、桌面图案

1. 输入：
   - 第一行表示桌面上初始图案的排列情况（如 BCCLLBB）。
   - 第二行表示小友手中的图案情况（如 BCLCB）。
2. 规则：
   - 你可以将手中的图案插入到桌面图案的任何位置，如果在插入后，桌面上出现了连续三个或三个以上相同的图案，那么这些图案会被移除。
   - 如果移除图案后，桌面上再次出现连续三个或更多相同图案的情况，则继续移除，直到无法再移除为止。
   - 如果最终桌面上的所有图案都被移除，则小友获胜。
3. 输出：
   - 你需要计算要清空桌面上所有图案所需的最少插入手中的图案次数。
   - 如果无法清空桌面上的所有图案，输出 -1。

示例解释：

- 示例1：输入 BCCLLBB 和 BCLCB，结果为 2。通过插入图案后，可以清空所有桌面图案。
- 示例2：输入 BCCLLB 和 CL，结果为 -1。即便插入手中图案，也无法清空所有桌面图案。



```java
public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String initial = in.next();
        String hand = in.next();
        System.out.println("hand = " + hand);
        System.out.println("in = " + initial);
        int dfs = dfs(initial, hand.toCharArray(), 0);
        dfs = dfs==Integer.MAX_VALUE?-1:dfs;
        System.out.println("dfs = " + dfs);
    }

    static int dfs(String board,char[] hand,int step){
        board = removeConsective(board);
        if (board.isEmpty()) return step;
        int minSteps = Integer.MAX_VALUE;
        for (int i = 0; i < board.length(); i++) {
            for (int j = 0; j < hand.length; j++) {
                if (hand[j]==' ') continue;//跳过使用过的
                //拼接进去
                String newboard = board.substring(0, i)+hand[j]+board.substring(i);
                newboard = removeConsective(newboard);
                char c = hand[j];
                hand[j] = ' ';
                int result = dfs(newboard, hand, step + 1);
                if (result!=Integer.MAX_VALUE) minSteps = Math.min(minSteps,result);
                hand[j] = c;
            }
        }
        return minSteps;
    }
    static String removeConsective(String board){
        //删除连续的三个以上的自负
        StringBuilder sb = new StringBuilder(board);
        boolean removed = true;
        while (removed){
            removed  = false;
            for (int i = 0; i < sb.length() - 2; i++) {
                int j = i;
                while (j<sb.length()&&sb.charAt(j)==sb.charAt(i)){
                    j++;
                }
                if (j-i>=3){
                    sb.delete(i,j);
                    removed = true;
                    break;
                }
            }
        }
        return sb.toString();
    }
```




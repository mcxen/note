#综合复习

# 基础

本区域讲解基本图论算法，之后会提取出来。

[[图论1-图论基础]]

[[图论2-拓扑排序]]

[[图论3-二分图的判定]] 就是解决一个图中，各个节点是不是可以分成两个部分，二分图怎么判断，其实就是让 `traverse` 函数一边遍历节点，一边给节点染色，尝试让每对相邻节点的颜色都不一样**。

[[图论4-Union-Find 并查集算法#LeetCode 684. 冗余连接]] 主要是用于解决树的环的问题，- 依次对每个边的两个顶点进行并查集合并。当遇到一个边的两个顶点已经合并过，发现了环，返回这条边。并查集就是find祖先节点。


[[图论5-最小生成树- kruskal]] **Kruskal 算法用到了[[图论4-Union-Find 并查集算法]]，来保证挑选出来的这些边组成的一定是一棵「树」，而不会包含环或者形成一片「森林」。

## 邻接矩阵表示方式

参考：
[[图论1-图论基础#^3678f6]]

```java
//就是需要回溯遍历每个节点的时候就用这个
List<int[]>[] graph;//这里表示每个节点都有一个List<int[]> 存储的是该节点的邻接的节点和权重{[1,2],[3,2]}也就是邻接了1和3，权重分别为2和2.


List<int[]> graph;// 存储的是每个几点都有一个int[],存储的该节点和其他节点的边的信息，[1,3,3]也就是1和3权重为3，这个需要遍历才能知道每个节点都有哪些邻接的节点。
// 邻接矩阵
```

比如题目给的例子，输入的邻接表 `graph = [[1,2,3],[0,2],[0,1,3],[0,2]]`，也就是这样一幅图：

![](https://pic1.zhimg.com/v2-a1fdaac4849609eb85a7a7654dbb608c_b.jpg)


这个是无权连接图，所以每个节点相连的点为int[]表达就行，如果是有权,`graph = {{[1,1],[2,3],[3,1]},{...}}，这样就是List<int[]>[]` 就是保存需要保存每个节点的id和权值，而且是idx就是当前节点的id，这个样子方便遍历某个节点的所有邻接的点。用于



**连接所有点的最小费用题目里面**

给你一个`points` 数组，表示 2D 平面上的一些点，其中 `points[i] = [xi, yi]` 。

连接点 `[xi, yi]` 和点 `[xj, yj]` 的费用为它们之间的 **曼哈顿距离** ：`|xi - xj| + |yi - yj|` ，其中 `|val|` 表示 `val` 的绝对值。

请你返回将所有点连接的最小总费用。只有任意两点之间 **有且仅有** 一条简单路径时，才认为所有点都已连接。

**示例 1：**

![img](./assets/README/d.png)

```
输入：points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
输出：20
解释：
```

这里如果是用最少生成树的kruskal算法的法，就是用edge列表表示就行，遍历edges，如果不能join这个并查集uf.join ，就是

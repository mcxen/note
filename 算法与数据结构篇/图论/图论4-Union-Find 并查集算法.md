
今天讲讲 Union-Find 算法，也就是常说的并查集算法，主要是解决图论中「动态连通性」问题的。名词很高端，其实特别好理解，等会解释，另外这个算法的应用都非常有趣。

说起这个 Union-Find，应该算是我的「启蒙算法」了，因为《算法4》的开头就介绍了这款算法，可是把我秀翻了，感觉好精妙啊！后来刷了 LeetCode，并查集相关的算法题目都非常有意思，而且《算法4》给的解法竟然还可以进一步优化，只要加一个微小的修改就可以把时间复杂度降到 O(1)。

什么叫动态连通性？

### 一、问题介绍

简单说，动态连通性其实可以抽象成给一幅图连线。比如下面这幅图，总共有 10 个节点，他们互不相连，分别用 0~9 标记：

![](./assets/图论4-Union-Find 并查集算法/1716801639614-1cd699eb-a256-4785-b7f5-fc8115fe3052.jpeg)

现在我们的 Union-Find 算法主要需要实现这两个 API：  

```java
class UF {
    /* 将 p 和 q 连接 */
    public void union(int p, int q);
    /* 判断 p 和 q 是否连通 */
    public boolean connected(int p, int q);
    /* 返回图中有多少个连通分量 */
    public int count();
}
```

这里所说的「连通」是一种等价关系，也就是说具有如下三个性质：

**1、自反性**：节点`p`和`p`是连通的。

**2、对称性**：如果节点`p`和`q`连通，那么`q`和`p`也连通。

**3、传递性**：如果节点`p`和`q`连通，`q`和`r`连通，那么`p`和`r`也连通。

比如说之前那幅图，0～9 任意两个**不同**的点都不连通，调用`connected`都会返回 false，连通分量为 10 个，实际就是小单元

如果现在调用`union(0, 1)`，那么 0 和 1 被连通，连通分量降为 9 个。

再调用`union(1, 2)`，这时 0,1,2 都被连通，调用`connected(0, 2)`也会返回 true，连通分量变为 8 个。

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801639676-f84b04f7-4572-407e-99a5-971ffd0281dc.jpeg)

判断这种「等价关系」非常实用，比如说编译器判断同一个变量的不同引用，比如社交网络中的朋友圈计算等等。  

这样，你应该大概明白什么是动态连通性了，Union-Find 算法的关键就在于`union`和`connected`函数的效率。那么用什么模型来表示这幅图的连通状态呢？用什么数据结构来实现代码呢？

### 二、基本思路

注意我刚才把「模型」和具体的「数据结构」分开说，这么做是有原因的。因为我们使用森林（若干棵树）来表示图的动态连通性，用数组来具体实现这个森林。

怎么用森林来表示连通性呢？我们设定树的每个节点有一个指针指向其父节点，如果是根节点的话，这个指针指向自己。

比如说刚才那幅 10 个节点的图，一开始的时候没有相互连通，就是这样：

```java
class UF {
    // 记录连通分量
    private int count;
    // 节点 x 的节点是 parent[x]
    private int[] parent;

    /* 构造函数，n 为图的节点总数 */
    public UF(int n) {
        // 一开始互不连通
        this.count = n;
        // 父节点指针初始指向自己
        parent = new int[n];
        for (int i = 0; i < n; i++)
            parent[i] = i;
    }

    /* 其他函数 */
}
```

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801639709-811dfdba-1506-43d2-84ab-13c48e4cebd6.jpeg)

**如果某两个节点被连通，则让其中的（任意）一个节点的根节点接到另一个节点的根节点上**：

```java
public void union(int p, int q) {
    int rootP = find(p);
    int rootQ = find(q);
    if (rootP == rootQ)
        return;
    // 将两棵树合并为一棵
    parent[rootP] = rootQ;
    // parent[rootQ] = rootP 也一样
    count--; // 两个分量合二为一
}

/* 返回某个节点 x 的根节点 */
private int find(int x) {
    // 根节点的 parent[x] == x
    while (parent[x] != x)
        x = parent[x];
    return x;
}

/* 返回当前的连通分量个数 */
public int count() { 
    return count;
}
```

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801639805-15a93d4e-0189-4162-b0bc-5cb08739ddbc.jpeg)

**这样，如果节点**`**p**`**和**`**q**`**连通的话，它们一定拥有相同的根节点**：

```
public boolean connected(int p, int q) {
    int rootP = find(p);
    int rootQ = find(q);
    return rootP == rootQ;
}
```

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801639809-7ffa38e0-2d31-4915-9eef-59d713d9c0bf.jpeg)

至此，Union-Find 算法就基本完成了。是不是很神奇？竟然可以这样使用数组来模拟出一个森林，如此巧妙的解决这个比较复杂的问题！

那么这个算法的复杂度是多少呢？我们发现，主要 API`connected`和`union`中的复杂度都是`find`函数造成的，所以说它们的复杂度和`find`一样。

`find`主要功能就是从某个节点向上遍历到树根，其时间复杂度就是树的高度。我们可能习惯性地认为树的高度就是`logN`，但这并不一定。`**logN**`**的高度只存在于平衡二叉树，对于一般的树可能出现极端不平衡的情况，使得「树」几乎退化成「链表」，树的高度最坏情况下可能变成**`**N**`**。**

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801640025-d750ad3d-7d8b-4514-b50c-afcf42e6e63f.jpeg)

所以说上面这种解法，`find`,`union`,`connected`的时间复杂度都是 O(N)。这个复杂度很不理想的，你想图论解决的都是诸如社交网络这样数据规模巨大的问题，对于`union`和`connected`的调用非常频繁，每次调用需要线性时间完全不可忍受。  

**问题的关键在于，如何想办法避免树的不平衡呢**？只需要略施小计即可。

### 三、平衡性优化

我们要知道哪种情况下可能出现不平衡现象，关键在于`union`过程：

```java
public void union(int p, int q) {
    int rootP = find(p);
    int rootQ = find(q);
    if (rootP == rootQ)
        return;
    // 将两棵树合并为一棵
    parent[rootP] = rootQ;
    // parent[rootQ] = rootP 也可以
    count--;
```

我们一开始就是简单粗暴的把`p`所在的树接到`q`所在的树的根节点下面，那么这里就可能出现「头重脚轻」的不平衡状况，比如下面这种局面：

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801640258-4621ac7e-a6dd-454d-9926-570f768dced0.jpeg)

长此以往，树可能生长得很不平衡。**我们其实是希望，小一些的树接到大一些的树下面，这样就能避免头重脚轻，更平衡一些**。==解决方法是额外使用一个`size`数组，记录每棵树包含的节点数，我们不妨称为「重量」：==  

```java
class UF {
    private int count;
    private int[] parent;
    // 新增一个数组记录树的“重量”
    private int[] size;

    public UF(int n) {
        this.count = n;
        parent = new int[n];
        // 最初每棵树只有一个节点
        // 重量应该初始化 1
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }
    /* 其他函数 */
}
```

比如说`size[3] = 5`表示，以节点`3`为根的那棵树，总共有`5`个节点。这样我们可以修改一下`union`方法：

```java
public void union(int p, int q) {
    int rootP = find(p);
    int rootQ = find(q);
    if (rootP == rootQ)
        return;

    // 小树接到大树下面，较平衡
    if (size[rootP] > size[rootQ]) {
        parent[rootQ] = rootP;
        size[rootP] += size[rootQ];
    } else {
        parent[rootP] = rootQ;
        size[rootQ] += size[rootP];
    }
    count--;
}
```

这样，通过比较树的重量，就可以保证树的生长相对平衡，树的高度大致在`logN`这个数量级，极大提升执行效率。

此时，`find`,`union`,`connected`的时间复杂度都下降为 O(logN)，即便数据规模上亿，所需时间也非常少。

### 四、路径压缩

这步优化特别简单，所以非常巧妙。我们能不能进一步压缩每棵树的高度，使树高始终保持为常数？

![](https://cdn.nlark.com/yuque/0/2024/jpeg/1389077/1716801640148-7476d66f-b8c8-458c-aa60-d586822e8160.jpeg)

这样`find`就能以 O(1) 的时间找到某一节点的根节点，相应的，`connected`和`union`复杂度都下降为 O(1)。  

要做到这一点，非常简单，只需要在`find`中加一行代码：

```java
private int find(int x) {
    while (parent[x] != x) {
        // 进行路径压缩
        parent[x] = parent[parent[x]];
        x = parent[x];
    }
    return x;
}
```

这个操作有点匪夷所思，看个 GIF 就明白它的作用了（为清晰起见，这棵树比较极端）：



![](https://cdn.nlark.com/yuque/0/2024/gif/1389077/1716801640694-22b2a899-9b94-451a-8dc4-b2618f3551cf.gif)

可见，调用`find`函数每次向树根遍历的同时，顺手将树高缩短了，最终所有树高都不会超过 3（`union`的时候树高可能达到 3）。

PS：读者可能会问，这个 GIF 图的`find`过程完成之后，树高恰好等于 3 了，但是如果更高的树，压缩后高度依然会大于 3 呀？不能这么想。这个 GIF 的情景是我编出来方便大家理解路径压缩的，但是实际中，每次`find`都会进行路径压缩，所以树本来就不可能增长到这么高，你的这种担心应该是多余的。

### 五、完整代码模版

我们先来看一下完整代码：

```java
class UF {
    // 连通分量个数
    private int count;
    // 存储一棵树
    private int[] parent;
    // 记录树的“重量”
    private int[] size;

    public UF(int n) {
        this.count = n;
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ)
            return;

        // 小树接到大树下面，较平衡
        if (size[rootP] > size[rootQ]) {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        } else {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        }
        count--;
    }
  /*
  public boolean union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ)
            return false;

        // 小树接到大树下面，较平衡
        if (size[rootP] > size[rootQ]) {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        } else {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        }
        count--;
        return true;
    }
    */

    public boolean connected(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        return rootP == rootQ;
    }

    private int find(int x) {
        while (parent[x] != x) {
            // 进行路径压缩
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
```

Union-Find 算法的复杂度可以这样分析：构造函数初始化数据结构需要 O(N) 的时间和空间复杂度；连通两个节点`union`、判断两个节点的连通性`connected`、计算连通分量`count`所需的时间复杂度均为 O(1)。


### LeetCode 684. 冗余连接

[684. 冗余连接](https://leetcode-cn.com/problems/redundant-connection/)

树可以看成是一个连通且 **无环** 的 **无向** 图。（关键是这一句，剩余部分钥匙联通没有环的）

给定往一棵 `n` 个节点 (节点值 `1～n`) 的树中添加一条边后的图。添加的边的两个顶点包含在 `1` 到 `n` 中间，且这条附加的边不属于树中已存在的边。图的信息记录于长度为 `n` 的二维数组 `edges` ，`edges[i] = [ai, bi]` 表示图中在 `ai` 和 `bi` 之间存在一条边。

请找出一条可以删去的边，删除后可使得剩余部分是一个有着 `n` 个节点的树。如果有多个答案，则返回数组 `edges` 中最后出现的那个。

**示例 1：**

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1626676174-hOEVUL-image.png)

```cmd
输入: edges = [[1,2], [1,3], [2,3]]  
输出: [2,3]
```

**示例 2：**

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1626676179-kGxcmu-image.png)

```cmd
输入: edges = [[1,2], [1,3], [2,3]]  
输出: [2,3]
```

分析

- 依次对每个边的两个顶点进行并查集合并。
  
- 当遇到一个边的两个顶点已经合并过，发现了环，返回这条边。
  
- 输出参数只有 `edges` 而没有N。对于有 `N` 个节点的树，应该有 `N-1` 条边，再加上附加的一条边，得到N条边。因此 `edges` 的size即为N。
  

代码

```java
class Solution {  
​  
    int[] parent;  
​  
    public int[] findRedundantConnection(int[][] edges) {  
        // N = edges.length  
        parent = new int[edges.length + 1];  
        for (int i = 0; i < parent.length; ++i) {  
            parent[i] = i;  
        }  
      // 2. 遍历二维数组的顶点对  
        for (int[] edge : edges) {  
          // 如果能加入到并查集返回true，否则返回false  
            if (!union(edge[0], edge[1])) {  
                return edge;  
            }  
        }  
        return new int[]{-1,-1};  
    }  
​  
    public int findRoot(int x) {  
        while (x != parent[x]) {  
            parent[x] = parent[parent[x]];  
            x = parent[x];  
        }  
        return x;  
    }  
​  
    public boolean union(int x, int y) {  
        x = findRoot(x);  
        y = findRoot(y);  
        if (x == y) {  
            return false;  
        }  
        parent[x] = y;  
        return true;  
    }  
}

```

#### 标准答案：

```java
class Solution {  
    public int[] findRedundantConnection(int[][] edges) {  
        UF uf = new UF(edges.length + 1);  
        for (int[] edge : edges) {  
            if (!uf.union(edge[0],edge[1])){  
                return edge;  
            }  
        }  
        return new int[]{-1,-1};  
    }  
    class UF{  
        int count;//联通量  
        int[] parent;  
        // 记录树的“重量”  
        private int[] size;  
        UF(int n){  
            parent = new int[n];  
            size = new int[n];  
            this.count = n;  
            for (int i = 0; i < n; i++) {  
                parent[i]=i;//初始化自己的祖先为自己  
                size[i]=1;  
            }  
        }  
        boolean union(int p,int q){  
            int rootP = find(p);  
            int rootQ = find(q);  
            if (rootQ==rootP) return false;  
            if (size[rootP]>size[rootQ]){  
                parent[rootQ]=rootP;  
                size[rootP]+=size[rootQ];  
                // 小树接到大树下面，较平衡  
            }else {  
                parent[rootP]=rootQ;  
                size[rootQ]+=size[rootP];  
            }  
            count--;  
            return true;  
        }  
        int find(int p){  
            //查找祖先  
            while (p!=parent[p]){  
                parent[p]=parent[parent[p]];  
                p = parent[p];  
            }  
            return p;  
        }  
    }  
}
```
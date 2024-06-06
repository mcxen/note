package template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * 并查集模板
 */
class UnionFind {
    int[] parent;
    int[] size;
    int n;
    // 当前连通分量数目
    int setCount;

    public UnionFind(int n) {
        this.n = n;
        this.setCount = n;
        this.parent = new int[n];
        this.size = new int[n];
        Arrays.fill(size, 1);
        for (int i = 0; i < n; ++i) {
            parent[i] = i;
        }
    }

    public int findset(int x) {
        return parent[x] == x ? x : (parent[x] = findset(parent[x]));
    }

    public boolean unite(int x, int y) {
        x = findset(x);
        y = findset(y);
        if (x == y) {
            return false;
        }
        if (size[x] < size[y]) {
            int temp = x;
            x = y;
            y = temp;
        }
        parent[y] = x;
        size[x] += size[y];
        --setCount;
        return true;
    }

    public boolean connected(int x, int y) {
        x = findset(x);
        y = findset(y);
        return x == y;
    }
}

/**
 * 并查集模板 hashmap
 * 上一版使用于知道节点的取值范围的场景，这一版使用Hashmap可以处理不知道取值范围的情况
 */
class UnionFindMap {
    // 记录每个节点的父节点
    private Map<Integer, Integer> parent;
    // 记录节点所在连通分量的节点个数
    private Map<Integer, Integer> count;

    public UnionFindMap(int[] nums) {
        parent = new HashMap<>();
        count = new HashMap<>();
        // 初始化父节点为自身
        for (int num : nums) {
            parent.put(num, num);
            count.put(num, 1);
        }
    }

    // 寻找x的父节点，实际上也就是x的最远连续右边界
    public Integer find(int x) {
        if (!parent.containsKey(x)) {
            return null;
        }
        // 遍历找到x的父节点
        while (x != parent.get(x)) {
            // 进行路径压缩
            parent.put(x, parent.get(parent.get(x)));
            x = parent.get(x);
        }
        return x;
    }

    // 合并两个连通分量，用来将num并入到num+1的连续区间中
    // 返回值为x所在连通分量的节点个数
    public int union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return count.get(rootX);
        }
        parent.put(rootX, rootY);
        // 更新该根结点连通分量的节点个数
        count.put(rootY, count.get(rootX) + count.get(rootY));
        return count.get(rootY);
    }
}

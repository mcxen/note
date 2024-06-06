package template;

/**
 *  线段树（动态开点）
 **/
public class SegmentTreeDynamic {
    class Node {
        // 左右孩子节点
        Node left, right;
        // 当前节点的值
        int val;
        // 懒惰标记
        int add;
    }
    private int N = (int) 1e9;

    private Node root = new Node();

    // 在区间 [start, end] 中更新区间 [l, r] 的值，将区间 [l, r] + val
    public void update(Node node, int start, int end, int l, int r, int val) {
        // 找到满足要求的区间
        if (l <= start && end <= r) {
            // 区间节点加上更新值
            // 注意：需要 * 该子树所有叶子节点
            node.val += (end - start + 1) * val;
            // 添加懒惰标记
            // 对区间进行「加减」的更新操作，懒惰标记需要累加，不能直接覆盖
            node.add += val;
            return ;
        }

        int mid = (start + end) >> 1;
        // 下推标记
        // mid - start + 1：表示左孩子区间叶子节点数量
        // end - mid：表示右孩子区间叶子节点数量

        pushDown(node, mid - start + 1, end - mid);
        // [start, mid] 和 [l, r] 可能有交集，遍历左孩子区间
        if (l <= mid) update(node.left, start, mid, l, r, val);
        // [mid + 1, end] 和 [l, r] 可能有交集，遍历右孩子区间
        if (r > mid) update(node.right, mid + 1, end, l, r, val);
        // 向上更新
        pushUp(node);
    }

    // 在区间 [start, end] 中查询区间 [l, r] 的结果，即 [l ,r] 保持不变
    public int query(Node node, int start, int end, int l, int r) {
        if (l <= start && end <= r) return node.val;
        int mid = (start + end) >> 1, ans = 0;
        pushDown(node, mid - start + 1, end - mid);
        if (l <= mid) ans += query(node.left, start, mid, l, r);
        if (r > mid) ans += query(node.right, mid + 1, end, l, r);
        return ans;
    }

    // 向上更新
    private void pushUp(Node node) {
        node.val = node.left.val + node.right.val;
    }

    // leftNum 和 rightNum 表示左右孩子区间的叶子节点数量
    // 因为如果是「加减」更新操作的话，需要用懒惰标记的值 * 叶子节点的数量
    private void pushDown(Node node, int leftNum, int rightNum) {
        // 动态开点
        if (node.left == null) node.left = new Node();
        if (node.right == null) node.right = new Node();
        // 如果 add 为 0，表示没有标记
        if (node.add == 0) return ;
        // 注意：当前节点加上标记值✖️该子树所有叶子节点的数量
        node.left.val += node.add * leftNum;
        node.right.val += node.add * rightNum;
        // 把标记下推给孩子节点
        // 对区间进行「加减」的更新操作，下推懒惰标记时需要累加起来，不能直接覆盖
        node.left.add += node.add;
        node.right.add += node.add;
        // 取消当前节点标记
        node.add = 0;
    }
}


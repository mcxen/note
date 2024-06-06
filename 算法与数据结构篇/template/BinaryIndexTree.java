package template;

/**
 * 树状数组
 */
class BinaryIndexTree {
    // 树状数组
    int [] tree;
    // 原数组
    int [] nums;

    public BinaryIndexTree(int[] nums) {
        int n = nums.length;
        tree = new int[n + 1];
        this.nums = nums;
        for (int i = 0; i < nums.length; i++) {
            add(i + 1, nums[i]);
        }
    }

    // 更新操作
    public void update(int index, int val) {
        add(index + 1, val - nums[index]);
        nums[index] = val;
    }

    // add操作，给下标给index的点加上val。注意是加上。
    public void add(int index, int val) {
        while (index < tree.length) {
            tree[index] += val;
            index += lowBit(index);
        }
    }

    // 求前缀和操作。即从下标为0到下标为index的元素的和。
    public int sum(int index) {
        int sum = 0;
        while (index > 0) {
            sum += tree[index];
            index -= lowBit(index);
        }
        return sum;
    }

    // 区间和
    public int sumRange(int left, int right) {
        return sum(right + 1) - sum(left);
    }

    public int lowBit(int x) {
        return x & -x;
    }
}

package template;

/**
 * 前缀和数组
 */
public class PreSumArray {

    // 记录前缀和的数组
    private int[] preSum;

    public PreSumArray(int[] nums) {
        // preSum 从 1 开始，避免越界问题
        preSum = new int[nums.length + 1];
        for (int i = 1; i < preSum.length; i++) {
            preSum[i] = preSum[i - 1] + nums[i - 1];
        }
    }

    public int sumRange(int left, int right) {
        return preSum[right + 1] - preSum[left];
    }
}

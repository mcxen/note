package template;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.TreeSet;

/**
 *  单调栈分为单调递增栈和单调递减栈，通过使用单调栈我们可以访问到最近一个比它大（小）的元素。
 *     单调递增栈：单调递增栈就是从栈底到栈顶数据是依次递增，通常是寻找某方向第一个比它小的元素。
 *     单调递减栈：单调递减栈就是从栈底到栈顶数据是依次递减，通常是寻找某方向第一个比它大的元素。
 */
public class MonotonicStack {



    // 寻找左边第一个小于它的数
    public static int[] findLeftSmall(int[] nums) {
        int n = nums.length;
        int [] ans = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {//单调栈模板（注意是数值）
            while (!stack.isEmpty() && stack.peek() >= nums[i]) stack.poll();
            if (!stack.isEmpty()) ans[i] = stack.peekFirst();
            else ans[i] = -1;
            stack.push(nums[i]);
        }
        return ans;
    }

    // 寻找左边第一个大于它的数
    public static int[] findLeftBig(int[] nums) {
        int n = nums.length;
        int [] ans = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {//单调栈模板（注意是数值）
            while (!stack.isEmpty() && stack.peek() <= nums[i]) stack.poll();
            if (!stack.isEmpty()) ans[i] = stack.peekFirst();
            else ans[i] = -1;
            stack.push(nums[i]);
        }
        return ans;
    }

    // 寻找左边第一个小于它的数的下标
    public static int[] findLeftSmallIndex(int[] nums) {
        int n = nums.length;
        int [] ans = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) stack.poll();
            if (!stack.isEmpty()) ans[i] = stack.peekFirst();
            else ans[i] = -1;
            stack.push(i);
        }
        return ans;
    }

    // 寻找右边第一个大于它的数
    public static int[] findRightBig(int [] nums) {
        int n = nums.length;
        int [] ans = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && stack.peek() <= nums[i]) stack.poll();
            if (!stack.isEmpty()) ans[i] = stack.peek();
            else ans[i] = -1;
            stack.push(nums[i]);
        }
        return ans;
    }


    // 寻找右边大于它的数中的最小值  不需要使用单调栈
    public static int[] findRightBigSmall(int [] nums) {
        int n = nums.length;
        int [] ans = new int[n];
        TreeSet<Integer> treeSet = new TreeSet<>();
        for (int i = n - 1; i >= 0; i--) {
            Integer num = treeSet.ceiling(nums[i]);
            if (num == null) {
                ans[i] = -1;
            } else {
                ans[i] = num;
            }
            treeSet.add(nums[i]);
        }
        return ans;
    }





    // 寻找左边第一关小于它的数的下标

    public static void main(String[] args) {
        System.out.println(Arrays.toString(findLeftBig(new int[]{2, 1, 1, 3, 6, 5})));
    }
}

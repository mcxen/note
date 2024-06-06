package template;

/**
 * 常见二分查找
 */
public class BinarySearch {

    /**
     * >= target
     * > target 转换为 >= target + 1
     * < target 转化为 >= target 左边那个下标
     * <= target 转化为 > taeget 左边那个下标
     * @param nums
     * @param target
     * @return
     */
    public int lower_bound1(int [] nums, int target) {
        int n = nums.length;
        int left = 0;
        int right = n - 1; // 区间[left, right]
        while (left <= right) {
            int mid = left + (right - left) / 2;
            // 注意这个地方，有些题的check()条件是小于等于为true，返回值会有所不同
            if (nums[mid] < target) {
                left = mid + 1;  // [mid + 1, right]
            } else {
                right = mid - 1; // [left, mid - 1]
            }
        }
        return left;
    }

    public int lower_bound2(int [] nums, int target) {
        int n = nums.length;
        int left = 0;
        int right = n;  // 区间[left, right)
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;  // [mid + 1, right)
            } else {
                right = mid; // [left, mid - 1)
            }
        }
        return left; // right
    }

    public int lower_bound3(int [] nums, int target) {
        int n = nums.length;
        int left = -1;
        int right = n;  // 区间(left, right)
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid;  // (mid + 1, right)
            } else {
                right = mid; // (left, mid - 1)
            }
        }
        return right;
    }
}

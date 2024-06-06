package template;


import java.util.Arrays;
import java.util.Random;

/**
 * 各种排序
 */
public class Sort {

    public static void main(String[] args) {
        QuickSort quickSort = new QuickSort();
        HeapSort heapSort = new HeapSort();
        System.out.println(Arrays.toString(heapSort.sortArray(new int[]{2, 5, 3, 6, 8, 6, 2})));
    }


    /**
     * 快速排序
     */
    static public class QuickSort {

        public int[] sortArray(int[] nums) {
            randomizedQuicksort(nums, 0, nums.length - 1);
            return nums;
        }

        public void randomizedQuicksort(int[] nums, int l, int r) {
            if (l < r) {
                int pos = randomizedPartition(nums, l, r);
                randomizedQuicksort(nums, l, pos - 1);
                randomizedQuicksort(nums, pos + 1, r);
            }
        }

        public int randomizedPartition(int[] nums, int l, int r) {
            int i = new Random().nextInt(r - l + 1) + l; // 随机选一个作为我们的主元
            swap(nums, r, i);
            return partition(nums, l, r);
        }

        public int partition(int[] nums, int l, int r) {
            int pivot = nums[r];
            int i = l - 1;
            for (int j = l; j <= r - 1; ++j) {
                if (nums[j] <= pivot) {
                    i = i + 1;
                    swap(nums, i, j);
                }
            }
            swap(nums, i + 1, r);
            return i + 1;
        }

        private void swap(int[] nums, int i, int j) {
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
    }


    /**
     * 堆排序
     */
    public static class HeapSort {

        public int[] sortArray(int[] nums) {
            heapSort(nums);
            return nums;
        }

        public void heapSort(int[] nums) {
            int len = nums.length - 1;
            buildMaxHeap(nums, len);
            for (int i = len; i >= 1; --i) {
                swap(nums, i, 0);
                len -= 1;
                maxHeapify(nums, 0, len);
            }
        }

        public void buildMaxHeap(int[] nums, int len) {
            for (int i = len / 2; i >= 0; --i) {
                maxHeapify(nums, i, len);
            }
        }

        public void maxHeapify(int[] nums, int i, int len) {
            for (; (i << 1) + 1 <= len;) {
                int lson = (i << 1) + 1;
                int rson = (i << 1) + 2;
                int large;
                if (lson <= len && nums[lson] > nums[i]) {
                    large = lson;
                } else {
                    large = i;
                }
                if (rson <= len && nums[rson] > nums[large]) {
                    large = rson;
                }
                if (large != i) {
                    swap(nums, i, large);
                    i = large;
                } else {
                    break;
                }
            }
        }

        private void swap(int[] nums, int i, int j) {
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
    }


    /**
     * 归并排序
     */
    class MergeSort {
        int[] tmp;

        public int[] sortArray(int[] nums) {
            tmp = new int[nums.length];
            mergeSort(nums, 0, nums.length - 1);
            return nums;
        }

        public void mergeSort(int[] nums, int l, int r) {
            if (l >= r) {
                return;
            }
            int mid = (l + r) >> 1;
            mergeSort(nums, l, mid);
            mergeSort(nums, mid + 1, r);
            int i = l, j = mid + 1;
            int cnt = 0;
            while (i <= mid && j <= r) {
                if (nums[i] <= nums[j]) {
                    tmp[cnt++] = nums[i++];
                } else {
                    tmp[cnt++] = nums[j++];
                }
            }
            while (i <= mid) {
                tmp[cnt++] = nums[i++];
            }
            while (j <= r) {
                tmp[cnt++] = nums[j++];
            }
            for (int k = 0; k < r - l + 1; ++k) {
                nums[k + l] = tmp[k];
            }
        }

    }




}

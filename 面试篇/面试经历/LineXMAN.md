

在Java中，变量的存放位置(内存区域)可以根据其类型和声明方式进行分类:
1，成员变量(Instance Variables):
成员变量指的是类中定义的变量，不使用static修饰，每个对象 (实例)都会有一份副本，存放在堆内存中。在类A中，私有成员变量“a属于这个类别，它会在每个类的实例化对象中有一份副本存放在堆内存中。
2.局部变量(Local Variables):
局部变量是在方法、构造方法或者语句块中定义的变量。它们在方法执行的时候创建，方法执行结束后会被销毁，存放在栈内存中，
在方法B中，变量“b是一个局部变量，存放在栈内存中。它在方法B执行时创建，方法B执行结束后销毁。
3.常量(Constants):
使用、finaL关键字修饰的常量在编译期间会被分配到常量池中。常量池可以是堆内存的一部分，也可以是特定于方法区的常量池。在类A中，常量“c是一个字符串常量，因为它被声明为、finaL string c =“cc"，，所以它会被分配到常量池中。
综上所述，变量a存放在堆内存的对象实例中，变量b存放在栈内存中，而变量C作为常量被分配到常量池中。



## 四个题目：

### 1.元素的后面比它大且最小

给一个数组 nums，请找出数组中每个元素的后面比它大且最小的元素(最接近的)，若不存在则为-1。并返回每个元素对应的值组成的那个数组。

数据范围:数组长度不超过10^5。数组的每个元素都是不超过 10^9 的正整数。
示例1
输入 [4,1,2,3] 输出[-1,2,3,-1]

示例 2
输入[11,13,10,5,12,21,3] 输出[12,21,12,12,21,-1,-1]

```java
public ArrayList<Integer> findNext (ArrayList<Integer> nums) {
        int n = nums.size();
        ArrayList<Integer> ans = new ArrayList<>(Collections.nCopies(n,-1));
        LinkedList<Integer> stack = new LinkedList<>();//存储递减的数字
        ArrayDeque<Integer> subs = new ArrayDeque<>();//存储辅助
//        LinkedList<Integer> list = new LinkedList<>();
        int max = Integer.MIN_VALUE;
        for (int i = n-1; i >= 0; i--) {
            if (nums.get(i)>max){
                stack.addFirst(nums.get(i));
                max = Math.max(max,nums.get(i));
                continue;
            }
//            max = Math.max(max,nums.get(i));

            while (!stack.isEmpty()&&nums.get(i)>=stack.peek()){
                subs.push(stack.pop());//存储一下
            }//大于当前位置的都弹出去了，剩下就是一个递减栈，peek存储的就是刚好大于i的值了
            if (!stack.isEmpty()){
                //此时就可以更新i了
                ans.set(i,stack.peek());
            }
            //插入新元素到该插入的位置，有点插入排序的意思
            stack.push(nums.get(i));
            while (!subs.isEmpty()){
                stack.push(subs.pop());
            }
        }
        return ans;
    }
```



### 2.反转字符串部分

```java
public String reverseString (String words) {        // write code here
        StringBuilder sb = new StringBuilder();
        String[] ss = words.split(" ");
        for (String s : ss) {
            StringBuilder ssb = new StringBuilder(s);
            sb.append(ssb.reverse().toString()).append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
```





### 4.序列话二叉树

```java
class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }
    String Serialize(TreeNode root) {
        if (root==null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()){
            TreeNode pop = queue.pop();
            if (pop!=null){
                sb.append(""+pop.val);
                queue.offer(pop.left);
                queue.offer(pop.right);
            }else {
                sb.append("#");
            }
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }
    TreeNode Deserialize(String str) {
        if (str=="") return null;
        String substring = str.substring(1, str.length() - 1);
        String[] list = substring.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(list[0]));
        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty()){
            TreeNode poll = queue.poll();
            if (!"#".equals(list[i])){
                poll.left =  new TreeNode(Integer.parseInt(list[i]));
                queue.offer(poll.left);
            }
            i++;
            if (!"#".equals(list[i])){
                poll.right =  new TreeNode(Integer.parseInt(list[i]));
                queue.offer(poll.right);
            }
            i++;
        }
        return root;
    }
```


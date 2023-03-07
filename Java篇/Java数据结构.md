# 基础知识

## 类继承树

![](https://gcore.jsdelivr.net/gh/mcxen/image@main/20220815143113.png)
由继承树看出，三者都是Collection的间接实现类。

ArrayDeque实现Deque接口，Stack继承于Vector，LinkedList实现Deque与List接口。
方法参照表

| Stack	|ArrayDeque	|LinkedList|
|----|----|----|
| push(e)	|addFirst(e)/offerFirst(e)	|addFirst(e)/offerFirst(e)|
| pop()	|removeFirst()/pollFirst()	|removeFirst()/pollFirst()|
| peek()	|getFirst()/peekFirst()	|getFirst()/peekFirst()|

```java
LinkedList<Integer> stack = new LinkedList<>();

stack.addLast(1);   // 元素 1 入栈
stack.addLast(2);   // 元素 2 入栈
stack.removeLast(); // 出栈 -> 元素 2
stack.removeLast(); // 出栈 -> 元素 1

```


## 位 bit 

原码：第一位表示符号，其余表示值
反码：正数的补码反码是其本身，负数的反码是符号位保持不变，其余位取反。
补码：正数的补码是其本身，负数的补码是在其反码的基础上 + 1

# 链表leetcode笔记
[原文档地址](https://labuladong.github.io/algo/2/19/18/)

## 虚拟头节点 86

如果不使用 dummy 虚拟节点，代码会复杂很多，而有了 dummy 节点这个占位符，可以避免处理空指针的情况，在最后只需要返回 dummy,next 降低代码的复杂性
    
``` java
    ListNode dummy = new ListNode(-1);
    ListNode l = dummy;
    ---
    return dummy.next;//就是返回l；
```

## 环

### 判断是否有环

```java
boolean hasCycle(ListNode head) {
    // 快慢指针初始化指向 head
    ListNode slow = head, fast = head;
    // 快指针走到末尾时停止
    while (fast != null && fast.next != null) {
        // 慢指针走一步，快指针走两步
        slow = slow.next;
        fast = fast.next.next;
        // 快慢指针相遇，说明含有环
        if (slow == fast) {
            return true;
        }
    }
    // 不包含环
    return false;
}


```

### 找环的起点
``` java
ListNode detectCycle(ListNode head) {
    ListNode fast, slow;
    fast = slow = head;
    while (fast != null && fast.next != null) {
        fast = fast.next.next;
        slow = slow.next;
        if (fast == slow) break;
    }
    // 上面的代码类似 hasCycle 函数
    if (fast == null || fast.next == null) {
        // fast 遇到空指针说明没有环
        return null;
    }

    // 重新指向头结点
    slow = head;
    // 快慢指针同步前进，相交点就是环起点
    while (slow != fast) {
        fast = fast.next;
        slow = slow.next;
    }
    return slow;
}

```
fast 一定比 slow 多走了 k 步，这多走的 k 步其实就是 fast 指针在环里转圈圈，所以 k 的值就是环长度的「整数倍」。

假设相遇点距环的起点的距离为 m，那么结合上图的 slow 指针，环的起点距头结点 head 的距离为 k - m，也就是说如果从 head 前进 k - m 步就能到达环起点。

巧的是，如果从相遇点继续前进 k - m 步，也恰好到达环起点。因为结合上图的 fast 指针，从相遇点开始走k步可以转回到相遇点，那走 k - m 步肯定就走到环起点了：


### 找两个链表的交点
可以直接把两个链表虚拟串在一块
``` java

    l1 = (l1==null)?headB:l1.next;
    l2 = (l2==null)?headA:l2.next;

    //下面这个直接超时了

    // if(l1.next==null) l1=headB;
    // else l1=l1.next;
    // if(l2.next==null) l2=headA;
    // else l2=l2.next;
```





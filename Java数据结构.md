# 基础知识
    ## 位 bit 
        原码：第一位表示符号，其余表示值
        反码：正数的补码反码是其本身，负数的反码是符号位保持不变，其余位取反。
        补码：正数的补码是其本身，负数的补码是在其反码的基础上 + 1
# 链表leetcode笔记
    虚拟头节点 86
        如果不使用 dummy 虚拟节点，代码会复杂很多，而有了 dummy 节点这个占位符，可以避免处理空指针的情况，在最后只需要返回 dummy,next 降低代码的复杂性
    
    ``` java
    ListNode dummy = new ListNode(-1);
    ListNode l = dummy;
    ---
    return dummy.next;//就是返回l；
    ```





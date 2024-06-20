## 基本逻辑：

<img src="./assets/LFU缓存/CleanShot 2024-06-20 at 14.44.39@2x.png" alt="CleanShot 2024-06-20 at 14.44.39@2x" style="zoom:33%;" />

想象你有好几摞书。

get：把一本书（key） 抽出来，放在它右边这摞书的最上面。

例如从「看过1次」中抽出一本书，放在「看过2 次」的最上面。

put：放入一本新书。

如果已经有这本书（key），

就把它抽出来放在它右边这摞书的最上面，并替换这本书的 value。

例如把一本书的第二版替换成第三版。

如果没有这本书（key），就放在「看过1 次」的最上面。

在放之前，先检查是否已经有 capacity 本书， **如果有，就把最左边那摞书的最下面的书移除。**



**如何实现「一摞书」？**

题目要求 get 和 put 都是 O(1) 时间复杂度，
这可以用双向链表实现，每个节点都表示一本书。

```
[哨兵] <-> [ ] <-> [ ] <-> [ ] <-> [ ]
 ^                                  |
  ----------------------------------
```

每个节点都有两个指针 `prev` 和 `next` 分别指向前一个和下一个节点。
此外，链表中还包含一个哨兵节点 `dummy`，
这可以让每个节点的 `prev` 和 `next` 都不为空，从而简化代码逻辑。

<img src="./assets/LFU缓存/CleanShot 2024-06-20 at 14.46.08@2x.png" alt="CleanShot 2024-06-20 at 14.46.08@2x" style="zoom:50%;" />

<img src="./assets/LFU缓存/CleanShot 2024-06-20 at 14.46.24@2x.png" alt="CleanShot 2024-06-20 at 14.46.24@2x" style="zoom:50%;" />

**如何实现「好几摞书」？**

用哈希表实现。

```java
Map<Integer, LinkedHashSet<Node>> freqMap; // 存储每个频次对应的双向链表
LinkedHashSet<Node> set = freqMap.get(1);//比如这个就是看了一次的哈希表
```

哈希表的键是看书的次数，

哈希表的值是这摞书对应的双向链表。

**如何快速找到 「抽出来的书」？**

把 key 映射到双向链表中的对应节点。

这也可以用哈希表实现。

**如何快速找到「最左边那摞书」？ MinFreq**

例如，没有看过1 次的书（全都看过至少两遍以上），要移除的书在 「看过2次」中。

我们可以维护一个变量` minFreq `表示最小看过次数。

get：如果把一本书抽出来后，这摞书变成空的，

并且这摞书在最左边（或者说这本书的看过次数等于 minFreq） 那么把 ` minFreq ` 增加一，否则不变。

put：如果已经有这本书，做法和 get 一样。

如果没有，那么会在「看过1 次」上面放入一本新书， 所以把 ` minFreq ` 更新为1。



**问：一个双向链表需要几个哨兵节点？**

**答：** 一个就够了。一开始哨兵节点 `dummy` 的 `prev` 和 `next` 都指向 `dummy`。随着节点的插入，`dummy` 的 `next` 指向链表的第一个节点（最上面的书），`prev` 指向链表的最后一个节点（最下面的书）。

**问：为什么 `minFreq` 一定对应着最左边的非空的那摞书？**

**答：** 在添加一本新书的情况下，这本新书一定是放在 `freq=1` 的那摞书上，此时我们把 `minFreq` 置为 1。在「抽出一本书且这摞书变成空」的情况下，我们会把这本书放到它右边这摞书的最上面。如果变成空的那摞书是最左边的，我们还会把 `minFreq` 加一。所以无论如何，`minFreq` 都会对应着最左边的非空的那摞书。

**问：有没有一些让代码变快的方法？**

**答：** 有两处「移除空链表」的逻辑是可以去掉的，代码（可能）会更快。



### [460. LFU 缓存](https://leetcode.cn/problems/lfu-cache/)

请你为 [最不经常使用（LFU）](https://baike.baidu.com/item/缓存算法)缓存算法设计并实现数据结构。

实现 `LFUCache` 类：

- `LFUCache(int capacity)` - 用数据结构的容量 `capacity` 初始化对象
- `int get(int key)` - 如果键 `key` 存在于缓存中，则获取键的值，否则返回 `-1` 。
- `void put(int key, int value)` - 如果键 `key` 已存在，则变更其值；如果键不存在，请插入键值对。当缓存达到其容量 `capacity` 时，则应该在插入新项之前，移除最不经常使用的项。在此问题中，当存在平局（即两个或更多个键具有相同使用频率）时，应该去除 **最久未使用** 的键。

为了确定最不常使用的键，可以为缓存中的每个键维护一个 **使用计数器** 。使用计数最小的键是最久未使用的键。

当一个键首次插入到缓存中时，它的使用计数器被设置为 `1` (由于 put 操作)。对缓存中的键执行 `get` 或 `put` 操作，使用计数器的值将会递增。

函数 `get` 和 `put` 必须以 `O(1)` 的平均时间复杂度运行。

 

**示例：**

```
输入：
["LFUCache", "put", "put", "get", "put", "get", "get", "put", "get", "get", "get"]
[[2], [1, 1], [2, 2], [1], [3, 3], [2], [3], [4, 4], [1], [3], [4]]
输出：
[null, null, null, 1, null, -1, 3, null, -1, 3, 4]
```



### 基于哈希链表

```java
class LFUCache {
    private static class Node {
        int key, value, freq = 1; // 新书只读了一次
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> keyToNode = new HashMap<>();
    private final Map<Integer, Node> freqToDummy = new HashMap<>();
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        Node node = getNode(key);
        return node != null ? node.value : -1;
    }

    public void put(int key, int value) {
        Node node = getNode(key);
        if (node != null) { // 有这本书
            node.value = value; // 更新 value
            return;
        }
        if (keyToNode.size() == capacity) { // 书太多了
            Node dummy = freqToDummy.get(minFreq);
            Node backNode = dummy.prev; // 最左边那摞书的最下面的书
            keyToNode.remove(backNode.key);
            remove(backNode); // 移除
            if (dummy.prev == dummy) { // 这摞书是空的
                freqToDummy.remove(minFreq); // 移除空链表
            }
        }
        node = new Node(key, value); // 新书
        keyToNode.put(key, node);
        pushFront(1, node); // 放在「看过 1 次」的最上面
        minFreq = 1;
    }

    private Node getNode(int key) {
        if (!keyToNode.containsKey(key)) { // 没有这本书
            return null;
        }
        Node node = keyToNode.get(key); // 有这本书
        remove(node); // 把这本书抽出来
        Node dummy = freqToDummy.get(node.freq);
        if (dummy.prev == dummy) { // 抽出来后，这摞书是空的
            freqToDummy.remove(node.freq); // 移除空链表
            if (minFreq == node.freq) { // 这摞书是最左边的
                minFreq++;
            }
        }
        pushFront(++node.freq, node); // 放在右边这摞书的最上面
        return node;
    }

    // 创建一个新的双向链表
    private Node newList() {
        Node dummy = new Node(0, 0); // 哨兵节点
        dummy.prev = dummy;
        dummy.next = dummy;
        return dummy;
    }

    // 在链表头添加一个节点（把一本书放在最上面）
    private void pushFront(int freq, Node x) {
        Node dummy = freqToDummy.computeIfAbsent(freq, k -> newList());
        x.prev = dummy;
        x.next = dummy.next;
        x.prev.next = x;
        x.next.prev = x;
    }

    // 删除一个节点（抽出一本书）
    private void remove(Node x) {
        x.prev.next = x.next;
        x.next.prev = x.prev;
    }
}

```


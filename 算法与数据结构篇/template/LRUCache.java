package template;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 手写LRU  最近最少使用
 */
public class LRUCache {
    static class DLinkedNode {
        int key;
        int value;
        // long expetime;
        DLinkedNode prev;
        DLinkedNode next;

        public DLinkedNode() {
        }

        public DLinkedNode(int _key, int _value, long e) {
            key = _key;
            value = _value; //expetime = e;}
        } // 当前时间是什么？
    }
        private Map<Integer, DLinkedNode> cache = new HashMap<Integer, DLinkedNode>();
        // 可以试试这样  过期时间
        private long ttl;

        public int size;
        private int capacity;
        private DLinkedNode head, tail;

        // 构造函数
        public LRUCache(int capacity) {
            this.size = 0;
            this.capacity = capacity;
            // this.ttl = ttl;
            // 使用伪头部和伪尾部节点
            head = new DLinkedNode();
            tail = new DLinkedNode();
            head.next = tail;
            tail.prev = head;
        }

        // get方法
        public int get(int key) {
            DLinkedNode node = cache.get(key);

            if (node == null) {
                return -1;
            }
            // 过期的key就删掉

/*            if (node.expetime < System.currentTimeMillis()) {
                removeNode(node);
                size--;
                cache.remove(node.key);
                return -1;
            }*/
            // 如果 key 存在，先通过哈希表定位，再移到头部
            moveToHead(node);
            return node.value;
        }

        // 这个就是set方法
        public void put(int key, int value) {

            DLinkedNode node = cache.get(key);
            if (node == null) {
                // 如果 key 不存在，创建一个新的节点  加上过期时间
                DLinkedNode newNode = new DLinkedNode(key, value, System.currentTimeMillis() + ttl);
                // 添加进哈希表
                cache.put(key, newNode);
                // 添加至双向链表的头部
                addToHead(newNode);
                ++size;
                if (size > capacity) {
                    // 如果超出容量，遍历找到过期的  他题目里说随机删  没懂怎么随机
/*                    DLinkedNode p = head.next;
                    while (p != null && p != tail) {
                        if (p.expetime < System.currentTimeMillis() + ttl) {
                            removeNode(p);
                            size--;
                            cache.remove(p.key);
                            return;
                        }
                        p = p.next;
                    }*/

                    DLinkedNode tail = removeTail();
                    // 删除哈希表中对应的项
                    cache.remove(tail.key);
                    --size;
                }
            } else {
                // 如果 key 存在，先通过哈希表定位，再修改 value，并移到头部
                node.value = value;
                moveToHead(node);
            }
        }

        // 添加到链表头
        private void addToHead(DLinkedNode node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        // 移除节点
        private void removeNode(DLinkedNode node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        // 移动到链表头等价于移除+添加到头
        private void moveToHead(DLinkedNode node) {
            removeNode(node);
            addToHead(node);
        }

        private DLinkedNode removeTail() {
            DLinkedNode res = tail.prev;
            removeNode(res);
            return res;
        }
}



package dataStructure.algorithm;

/**
 * LFU 缓存淘汰算法
 * 当缓存容量满时，淘汰掉被访问次数最少的数据。
 * 如果访问次数一样少，则淘汰掉最久没被访问的那一个
 * @author luminion
 * @since 1.0.0
 */
import java.util.HashMap;
import java.util.Map;

public class LFUCache {

    // 1. 定义缓存节点（必须包含 freq 频率，以及 key 用来反向清理 Map）
    class Node {
        int key, value, freq;
        Node prev, next;
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1; // 新元素的初始频率是 1
        }
    }

    // 2. 定义双向链表（带虚拟头尾节点，存放频次相同的 Node，维护其内部的 LRU 顺序）
    class DoublyLinkedList {
        Node head, tail;
        int size;
        public DoublyLinkedList() {
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
        }
        // 头插法：最新访问的放在头部
        public void addFirst(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }
        // 删除任意节点
        public void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }
        // 淘汰并返回最老的节点（即尾部节点）
        public Node removeLast() {
            if (size == 0) return null;
            Node last = tail.prev;
            remove(last);
            return last;
        }
    }

    // --- 全局属性 ---
    private int capacity;
    private int minFreq; // 核心变量：时刻记录当前的最小频率
    private Map<Integer, Node> keyToNode; // K: 缓存Key, V: 数据节点
    private Map<Integer, DoublyLinkedList> freqToList; // K: 频率, V: 该频率下的双向链表

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToNode = new HashMap<>();
        this.freqToList = new HashMap<>();
    }

    // ================= 核心逻辑 =================

    public int get(int key) {
        if (!keyToNode.containsKey(key)) return -1;
        Node node = keyToNode.get(key);
        // 数据被访问了，增加它的频率！
        increaseFreq(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        // 1. 如果数据已经存在，更新值，并增加频率
        if (keyToNode.containsKey(key)) {
            Node node = keyToNode.get(key);
            node.value = value;
            increaseFreq(node);
            return;
        }

        // 2. 如果是新数据，需要判断容量是否已满
        if (keyToNode.size() >= capacity) {
            // 找出最小频率对应的链表，淘汰该链表中最老的那个节点！
            DoublyLinkedList minList = freqToList.get(minFreq);
            Node deadNode = minList.removeLast();
            keyToNode.remove(deadNode.key); // 极其关键：从大 Map 中清除记录
        }

        // 3. 真正插入新数据
        Node newNode = new Node(key, value);
        keyToNode.put(key, newNode);
        // 新数据的频率永远是 1，获取频率为 1 的链表
        DoublyLinkedList list1 = freqToList.computeIfAbsent(1, k -> new DoublyLinkedList());
        list1.addFirst(newNode);
        // 插入新元素后，全局最小频率必定重置为 1 ！！！
        minFreq = 1;
    }

    // ================= 辅助逻辑 =================

    // 极其关键：处理频率提升的逻辑
    private void increaseFreq(Node node) {
        int oldFreq = node.freq;
        DoublyLinkedList oldList = freqToList.get(oldFreq);
        oldList.remove(node);

        // 如果旧链表空了，且旧链表刚好等于全局最小频率，那就说明最小频率被拔高了！
        if (oldList.size == 0 && oldFreq == minFreq) {
            minFreq++;
        }

        // 将节点加入到新频率对应的链表中
        node.freq++;
        DoublyLinkedList newList = freqToList.computeIfAbsent(node.freq, k -> new DoublyLinkedList());
        newList.addFirst(node);
    }
}

package dataStructure.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单LRU缓存实现
 *
 * @author luminion
 * @date 2026-03-03
 */
public class LRUCache<K, V> {

    /**
     * 双向链表节点
     *
     * @author luminion
     * @since 1.0.0
     */
    private class Node {
        K key;
        V value;
        Node prev;
        Node next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;        // 最大容量
    private final Map<K, Node> map;    // key → 节点映射

    // 虚拟头尾节点（哨兵），简化边界处理
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        // 初始化虚拟头尾节点
        head = new Node(null, null);
        tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * 查询
     */
    public V get(K key) {
        Node node = map.get(key);

        if (node == null) {
            return null; // 不存在
        }

        // 移到链表头部（最近访问）
        moveToHead(node);

        return node.value;
    }

    /**
     * 插入 / 更新
     */
    public void put(K key, V value) {
        Node node = map.get(key);

        if (node != null) {
            // key已存在 → 更新值，移到头部
            node.value = value;
            moveToHead(node);
        } else {
            // key不存在 → 新建节点
            Node newNode = new Node(key, value);

            map.put(key, newNode);
            addToHead(newNode); // 加到头部

            // 超出容量 → 淘汰尾部节点
            if (map.size() > capacity) {
                Node tail = removeTail();
                map.remove(tail.key); // 从map中也删掉
            }
        }
    }

    /**
     * 添加节点到头部
     *
     * head ↔ [newNode] ↔ [原来第一个节点]
     */
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    /**
     * 删除节点（从链表中摘除）
     */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /**
     * 移动节点到头部
     * = 先删除 + 再加到头部
     */
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * 删除尾部节点（最久没访问的）
     */
    private Node removeTail() {
        Node tailNode = tail.prev; // 真正的尾部节点
        removeNode(tailNode);
        return tailNode;
    }

    /**
     * 打印链表（方便调试）
     */
    public void print() {
        Node cur = head.next;
        System.out.print("head ↔ ");
        while (cur != tail) {
            System.out.print("[" + cur.key + ":" + cur.value + "] ↔ ");
            cur = cur.next;
        }
        System.out.println("tail");
    }
}
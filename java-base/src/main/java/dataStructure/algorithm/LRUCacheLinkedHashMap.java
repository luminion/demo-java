package dataStructure.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LinkedHashMap 底层本来就是一个哈希表 + 双向链表。
 * 构造它的时候，把 accessOrder 参数设置为 true，它就会按照访问顺序（而不是插入顺序）来维护链表。
 *
 * 你只需要继承它，并重写一个极小的方法即可实现 LRU
 * @author luminion
 * @since 1.0.0
 */
public class LRUCacheLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    public LRUCacheLinkedHashMap(int capacity) {
        // 第三个参数 true 代表开启 LRU 访问顺序（最近访问的放到链表尾部）
        // 初始容量设为 capacity，负载因子 0.75
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 核心方法：当 Map 内部元素个数大于容量时，自动删除最老的元素
        return size() > capacity;
    }

    public static void main(String[] args) {
        LRUCacheLinkedHashMap<Integer, Integer> cache = new LRUCacheLinkedHashMap<>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // 访问1，1变成最新
        cache.put(3, 3);                  // 容量满，淘汰最老的2
        System.out.println(cache.get(2)); // 返回 null (被淘汰了)
    }
}
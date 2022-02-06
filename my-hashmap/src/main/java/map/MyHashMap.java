package main.java.map;

import java.util.List;

public class MyHashMap<K, V> {

    public final int DEFAULT_SLOTSIZE = 64;
    public final int SKIPLISTIFY_SIZE = 8;
    public final int SKIPLIST_MAX_LEVEL = 16;
    public final int THRESHOLD = 16;
    public final float LOADFACTOR = 0.75f;
    public final int MAX_CAPACITY = 1 << 30;
    private int slotSize = 0;
    private Node<K, V>[] slots;
    private int size;

    public MyHashMap() {
        slots = (Node<K, V>[]) (new Node[DEFAULT_SLOTSIZE]);
    }

    final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    public void put(K key, V val) {
        if (key == null) return;
        int hash = hash(key);
        int n = slots.length;
        int idx = hash & (n - 1);
        if (slots[idx] == null) {
            slots[idx] = new Node<>(key, val, hash);
            slotSize++;
        } else if (slots[idx] instanceof SkipListNode) {
            // TODO 跳表put实现
        } else {
            Node<K, V> cur = slots[idx];
            while (cur != null) {
                if (cur.hash == hash) {
                    cur.val = val;
                    return;
                }
                cur = cur.next;
            }
            cur = new Node<>(key, val, hash);
            cur.size = slots[idx].size + 1;
            cur.next = slots[idx]; // 头插
            slots[idx] = cur;
            if (cur.size >= SKIPLISTIFY_SIZE) {
                // TODO 跳表化实现
                slots[idx] = skipListify(cur);
            }
            size++;
        }
        if ((float) (slotSize / n) > LOADFACTOR || slots[idx].size > THRESHOLD) {
            slots = resize();
        }
    }

    private Node<K, V>[] resize() {
        Node<K, V>[] table;
        slotSize = 0;
        if ((table = slots) == null || table.length == 0) {
            table = (Node<K, V>[]) new Node[DEFAULT_SLOTSIZE];
        } else if (table.length > MAX_CAPACITY) {
            return slots;
        } else {
            int newCap = table.length << 1; // 两倍扩容
            table = (Node<K, V>[]) new Node[newCap];
            for (int i = 0; i < slots.length; i++) {
                if (slots[i] == null) continue;
                if (slots[i] instanceof SkipListNode) {
                    // TODO 跳表扩容
                } else {
                    Node<K, V> cur = slots[i];
                    while (cur != null) {
                        Node<K, V> next = cur.next;
                        int idx = cur.hash & (newCap - 1);
                        cur.size = 0;
                        cur.next = null;
                        if (table[idx] == null) {
                            slotSize++;
                        } else {
                            cur.size = table[idx].size + 1;
                            cur.next = table[idx];
                        }
                        table[idx] = cur;
                        cur = next;
                    }
                }
            }
        }
        return table;
    }

    private Node<K, V> skipListify(Node<K, V> slot) {
        return null;
    }


    class Node<K, V> {
        K key;
        V val;
        int hash;
        int size; // 当前node下面挂了多少个节点
        Node<K, V> next;

        public Node() {
        }

        public Node(K key, V val, int hash) {
            this.key = key;
            this.val = val;
            this.hash = hash;
        }
    }

    final class SkipListNode<K, V> extends Node<K, V> {
        List<SkipListNode<K, V>> nexts;
        int height;

    }
}

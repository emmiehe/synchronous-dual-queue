import java.util.concurrent.atomic.AtomicReference;

public class SynchronousDualQueue<T> {

    private static class NULL {
        NULL() {
        }
    }

    private enum NodeType {ITEM, RESERVATION};

    private class Node {
        volatile NodeType type;
        volatile AtomicReference<T> item;
        volatile AtomicReference<Node> next;

        Node(T myItem, NodeType myType) {
            item = new AtomicReference<T>(myItem);
            next = new AtomicReference<Node>(null);
            type = myType;
        }
    }

    private AtomicReference<Node> head;
    private AtomicReference<Node> tail;
    private final T obj_null;

    public SynchronousDualQueue() {
        Node sentinel = new Node(null, NodeType.ITEM);
        head = new AtomicReference<Node>(sentinel);
        tail = new AtomicReference<Node>(sentinel);
        obj_null = (T) new NULL();
    }

    public void enq(T e) {
        Node offer = new Node(e, NodeType.ITEM);
        while (true) {
            Node t = tail.get(), h = head.get();
            if (h == t || t.type == NodeType.ITEM) {
                Node n = t.next.get();
                if (t == tail.get()) {
                    if (n != null) {
                        tail.compareAndSet(t, n);
                    } else if (t.next.compareAndSet(n, offer)) {
                        tail.compareAndSet(t, offer);
                        while (offer.item.get() == e);
                        h = head.get();
                        if (offer == h.next.get())
                            head.compareAndSet(h, offer);
                        return;
                    }
                }
            } else {
                Node n = h.next.get();
                if (t != tail.get() || h != head.get() || n == null) {
                    continue;
                }
                boolean success = n.item.compareAndSet(obj_null, e);
                head.compareAndSet(h, n);
                if (success)
                    return;
            }
        }
    }

    public void deq() {
        Node offer = new Node(obj_null, NodeType.RESERVATION);
        while (true) {
            Node t = tail.get(), h = head.get();
            if (h == t || t.type == NodeType.ITEM) {
                Node n = t.next.get();
                if (t == tail.get()) {
                    if (n != null) {
                        tail.compareAndSet(t, n);
                    } else if (t.next.compareAndSet(n, offer)) {
                        tail.compareAndSet(t, offer);
                        while (offer.item.get() == obj_null);
                        h = head.get();
                        if (offer == h.next.get())
                            head.compareAndSet(h, offer);
                        return;
                    }
                }
            } else {
                Node n = h.next.get();
                if (t != tail.get() || h != head.get() || n == null) {
                    continue;
                }
                boolean success = n.item.compareAndSet(n.item.get(), obj_null);
                head.compareAndSet(h, n);
                if (success)
                    return;
            }
        }
    }
}

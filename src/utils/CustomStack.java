package utils;

/**
 * Custom Stack (LIFO) tự cài đặt bằng Linked Node.
 * KHÔNG dùng java.util.Stack, java.util.Deque, hay bất kỳ Collection có sẵn nào.
 * Generic <T> để tái sử dụng cho nhiều kiểu dữ liệu.
 */
public class CustomStack<T> {

    /**
     * Node nội bộ — mỗi node chứa 1 phần tử dữ liệu và con trỏ tới node bên dưới.
     * Dùng static nested class vì không cần truy cập instance của CustomStack.
     */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> top;  // Con trỏ tới phần tử trên cùng
    private int size;     // Số phần tử hiện có trong stack

    public CustomStack() {
        this.top = null;
        this.size = 0;
    }

    // Đẩy phần tử lên đỉnh stack. O(1).
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top; // Node mới trỏ xuống node cũ
        top = newNode;      // Cập nhật đỉnh stack
        size++;
    }

    /**
     * Lấy và XÓA phần tử trên cùng. O(1).
     * Trả null nếu stack rỗng (không throw — caller tự check isEmpty()).
     */
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T data = top.data;
        top = top.next; // Dời đỉnh xuống node bên dưới
        size--;
        return data;
    }

    /**
     * Xem phần tử trên cùng mà KHÔNG xóa. O(1).
     * Trả null nếu stack rỗng.
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return top.data;
    }

 
    //Kiểm tra stack có rỗng không.
    public boolean isEmpty() {
        return top == null;
    }

    //Trả về số phần tử hiện có.
    public int size() {
        return size;
    }

    //Xóa toàn bộ phần tử trong stack. O(1).
    //Dùng khi cần clear Redo Stack sau hành động mới.
    public void clear() {
        top = null;
        size = 0;
    }
}

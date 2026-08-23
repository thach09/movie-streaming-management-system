package utils;

/**
 * Custom Exception dùng để ném ra khi dữ liệu đầu vào không hợp lệ
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
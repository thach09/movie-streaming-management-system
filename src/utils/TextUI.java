package utils;

/**
 * Tiện ích dựng giao diện CLI dạng khung (box) với căn lề đúng theo
 * "độ rộng hiển thị" của từng ký tự (emoji = 2 cột, dấu kết hợp = 0).
 *
 * Mục đích: khắc phục tình trạng viền khung (╔═╗ ║ ╚═╝) bị lệch cột
 * khi nội dung chứa emoji hoặc ký tự Unicode có độ rộng hiển thị khác nhau.
 */
public final class TextUI {

    /** Chiều rộng bên trong khung (số cột nằm giữa 2 viền ║). */
    public static final int BOX_WIDTH = 34;

    private TextUI() {
        // Lớp tiện ích — không cho phép khởi tạo đối tượng.
    }

    // ===================== MÀU SẮC ANSI =====================
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW_BOLD = "\u001B[1;33m";
    public static final String CYAN_BOLD = "\u001B[1;36m";

    /** Bọc chuỗi bằng mã màu ANSI và tự reset ở cuối. */
    public static String colored(String ansiCodes, String text) {
        return ansiCodes + text + RESET;
    }

    public static String cyan(String text) { return colored(CYAN, text); }
    public static String green(String text) { return colored(GREEN, text); }
    public static String red(String text) { return colored(RED, text); }
    public static String yellowBold(String text) { return colored(YELLOW_BOLD, text); }
    public static String cyanBold(String text) { return colored(CYAN_BOLD, text); }

    /** Tiêu đề dạng "--- TITLE ---" có màu. */
    public static String header(String title) {
        return colored(CYAN_BOLD, "--- " + title + " ---");
    }

    /** Dựng sẵn một khung menu hoàn chỉnh (có màu) từ danh sách mục. */
    public static String menuBox(String title, String[] items) {
        StringBuilder sb = new StringBuilder();
        sb.append(cyan(top())).append('\n');
        sb.append(yellowBold(center(title))).append('\n');
        sb.append(cyan(separator())).append('\n');
        for (String item : items) {
            sb.append(cyan(line(item))).append('\n');
        }
        sb.append(cyan(bottom()));
        return sb.toString();
    }

    /**
     * Tính độ rộng hiển thị thực tế của một chuỗi trên console.
     * - Emoji / ký tự wide (CJK, ...) = 2 cột.
     * - Dấu kết hợp (combining diacritic) = 0 cột.
     * - Còn lại = 1 cột.
     */
    public static int displayWidth(String text) {
        if (text == null) {
            return 0;
        }
        // Bỏ mã màu ANSI trước khi đo (mã màu không chiếm cột hiển thị).
        text = stripAnsi(text);
        int width = 0;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            int codePoint = text.codePointAt(i);
            int type = Character.getType(codePoint);

            // Dấu kết hợp không chiếm cột riêng (ví dụ dấu trên nguyên âm).
            if (type == Character.NON_SPACING_MARK
                    || type == Character.ENCLOSING_MARK
                    || type == Character.COMBINING_SPACING_MARK) {
                continue;
            }

            width += isWide(codePoint) ? 2 : 1;

            // Nếu là surrogate pair (emoji), bỏ qua code unit thứ 2.
            if (Character.isSupplementaryCodePoint(codePoint)) {
                i++;
            }
        }
        return width;
    }

    /** Loại bỏ các chuỗi escape màu ANSI (ví dụ ESC[36m, ESC[0m). */
    private static String stripAnsi(String text) {
        return text.replaceAll("\u001B\\[[0-9;]*m", "");
    }

    /** Xác định một code point có chiếm 2 cột hiển thị hay không. */
    private static boolean isWide(int codePoint) {
        // Emoji & pictographs.
        if ((codePoint >= 0x1F000 && codePoint <= 0x1FAFF)
                || (codePoint >= 0x2600 && codePoint <= 0x27BF)
                || (codePoint >= 0x2B00 && codePoint <= 0x2BFF)) {
            return true;
        }
        // CJK và các ký tự wide thông dụng.
        return (codePoint >= 0x1100 && codePoint <= 0x115F)
                || (codePoint >= 0x2E80 && codePoint <= 0x303E)
                || (codePoint >= 0x3041 && codePoint <= 0x33FF)
                || (codePoint >= 0x3400 && codePoint <= 0x4DBF)
                || (codePoint >= 0x4E00 && codePoint <= 0x9FFF)
                || (codePoint >= 0xAC00 && codePoint <= 0xD7A3)
                || (codePoint >= 0xF900 && codePoint <= 0xFAFF)
                || (codePoint >= 0xFF00 && codePoint <= 0xFF60)
                || (codePoint >= 0xFFE0 && codePoint <= 0xFFE6);
    }

    /**
     * Đệm khoảng trắng vào bên phải để chuỗi đạt đúng targetWidth cột hiển thị.
     * Nếu chuỗi đã dài hơn targetWidth thì trả về nguyên gốc (không cắt bớt).
     */
    public static String padRight(String text, int targetWidth) {
        int current = displayWidth(text);
        if (current >= targetWidth) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text.length() + (targetWidth - current));
        sb.append(text);
        for (int i = 0; i < targetWidth - current; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /** Viền trên của khung. */
    public static String top() {
        return "╔" + repeat("═", BOX_WIDTH) + "╗";
    }

    /** Viền dưới của khung. */
    public static String bottom() {
        return "╚" + repeat("═", BOX_WIDTH) + "╝";
    }

    /** Viền phân cách giữa tiêu đề và nội dung. */
    public static String separator() {
        return "╠" + repeat("═", BOX_WIDTH) + "╣";
    }

    /** Dòng nội dung nằm giữa 2 viền ║ (căn trái). */
    public static String line(String content) {
        return "║" + padRight(content, BOX_WIDTH) + "║";
    }

    /** Dòng tiêu đề nằm giữa 2 viền ║ (căn giữa). */
    public static String center(String text) {
        int textWidth = displayWidth(text);
        if (textWidth >= BOX_WIDTH) {
            return line(text);
        }
        int left = (BOX_WIDTH - textWidth) / 2;
        int right = BOX_WIDTH - textWidth - left;
        return "║" + repeat(" ", left) + text + repeat(" ", right) + "║";
    }

    private static String repeat(String value, int count) {
        StringBuilder sb = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(value);
        }
        return sb.toString();
    }
}

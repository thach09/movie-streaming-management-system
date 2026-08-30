package utils;

import java.util.Scanner;

public class InputValidator {

    // 1. Đọc chuỗi không được để rỗng (non-empty string)
    public static String readString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: Dữ liệu nhập vào không được để trống! Vui lòng nhập lại");
        }   
    }

    // 2. Đọc số nguyên dương (> 0)
    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if(value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Error: Giá trị phải nằm trong khoảng từ %d đến %d!\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Error: Vui lòng nhập vào một số nguyên hợp lệ!");
            }
        }
    }

    // 3. Đọc số thực trong khoảng [min, max] (Dùng cho Rating từ 0.0 - 10.0)
    public static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                double value = Double.parseDouble(input);
                if(value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Error: Giá trị phải nằm trong khoảng từ %.1f đến %.1f!\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Error: Vui lòng nhập vào một số thực hợp lệ!");
            }
        }
    }

    // 4. Dọn dẹp input tìm kiếm/lọc: bỏ ký tự nháy đơn ' thừa + trim.
    // CHỈ dùng cho input tìm kiếm/lọc, KHÔNG dùng cho input tạo/sửa dữ liệu.
    public static String sanitizeSearchInput(String rawInput) {
        if (rawInput == null) return null;
        String s = rawInput.trim();
        // Chỉ bỏ dấu nháy đơn THỪA ở ĐẦU/CUỐI (do lỡ tay gõ),
        // giữ nguyên dấu nháy bên trong (vd: "Mr. Bean's Holiday").
        while (s.startsWith("'")) {
            s = s.substring(1);
        }
        while (s.endsWith("'")) {
            s = s.substring(0, s.length() - 1);
        }
        return s.trim();
    }
 
}
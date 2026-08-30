package utils;

import model.Movie;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ReportExporter {
    /**
     * Xuất báo cáo lịch sử xem ra CSV. Mỗi dòng là 1 phim DISTINCT, kèm số lần xem.
     * Mọi field text (title, director, actors) được quote bằng dấu " và escape dấu "
     * bên trong bằng cách nhân đôi ("") — theo chuẩn RFC 4180.
     * Dùng Function<String, Movie> để tránh phụ thuộc ngược vào Controller.
     */
    public static boolean exportWatchHistoryReport(
            List<String> watchHistoryIds,
            java.util.function.Function<String, Movie> movieResolver,
            String filePath) {
        File file = new File(filePath);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        // Đếm số lần xem mỗi phim, giữ thứ tự xuất hiện đầu tiên
        List<String> orderedIds = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (String id : watchHistoryIds) {
            int found = -1;
            for (int i = 0; i < orderedIds.size(); i++) {
                if (orderedIds.get(i).equalsIgnoreCase(id)) {
                    found = i;
                    break;
                }
            }
            if (found >= 0) {
                counts.set(found, counts.get(found) + 1);
            } else {
                orderedIds.add(id);
                counts.add(1);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write("STT,Ma Phim,Ten Phim,The Loai,Dao Dien,Nam Phat Hanh,Rating,So Lan Xem");
            bw.newLine();

            int stt = 1;
            for (int i = 0; i < orderedIds.size(); i++) {
                Movie m = movieResolver.apply(orderedIds.get(i));
                if (m == null) continue;
                bw.write(stt + ","
                        + csvQuote(m.getId()) + ","
                        + csvQuote(m.getTitle()) + ","
                        + csvQuote(m.getCategoryId()) + ","
                        + csvQuote(m.getDirector()) + ","
                        + m.getReleaseYear() + ","
                        + m.getRating() + ","
                        + counts.get(i));
                bw.newLine();
                stt++;
            }
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi xuất báo cáo CSV: " + e.getMessage());
            return false;
        }
    }

    /** Quote 1 field CSV theo chuẩn RFC 4180: bọc trong "...", nhân đôi dấu " bên trong. */
    private static String csvQuote(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

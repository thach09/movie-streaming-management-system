package model;

/**
 * Đại diện cho 1 hành động trên Watchlist — dùng cho Undo/Redo.
 * Lưu trong RAM theo session, KHÔNG ghi xuống file.
 */
public class WatchlistAction {

    /**
     * Enum loại hành động: ADD (thêm phim) hoặc REMOVE (xóa phim).
     */
    public enum ActionType {
        ADD, REMOVE
    }

    private ActionType actionType;
    private String movieId;

    public WatchlistAction(ActionType actionType, String movieId) {
        this.actionType = actionType;
        this.movieId = movieId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getMovieId() {
        return movieId;
    }

    @Override
    public String toString() {
        return String.format("WatchlistAction [%s, movieId=%s]", actionType, movieId);
    }
}

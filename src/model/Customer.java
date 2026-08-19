package model;

import utils.ValidationException;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<String> watchlist;
    private List<String> favouriteList;
    private List<String> watchHistory;

    // Constructor mặc định
    public Customer () {
        super();
        try {
            this.setRole("CUSTOMER");
        } catch (ValidationException e) {
            // Không bao giờ xảy ra
        }
        this.watchlist = new ArrayList<>();
        this.favouriteList = new ArrayList<>();
        this.watchHistory = new ArrayList<>();
    }

    // Constructor đầy đủ tham số
    public Customer (String id, String username, String password, String fullName, String email, 
    List<String> watchlist, List<String> favouriteList, List<String> watchHistory) throws ValidationException {
        super(id, username, password, fullName, email, "CUSTOMER");
        this.watchlist = (watchlist != null) ? new ArrayList<>(watchlist) : new ArrayList<>();
        this.favouriteList = (favouriteList != null) ? new ArrayList<>(favouriteList) : new ArrayList<>();
        this.watchHistory = (watchHistory != null) ? new ArrayList<>(watchHistory) : new ArrayList<>();
    }

    // === Getters & Setters ===
    public List<String> getWatchlist() {
        return new ArrayList<>(watchlist); // Trả về bản sao để bảo vệ tính đóng gói
    }

     public void setWatchlist(List<String> watchlist) {
        this.watchlist = (watchlist != null) ? new ArrayList<>(watchlist) : new ArrayList<>();
    }
    public List<String> getFavouriteList() {
        return new ArrayList<>(favouriteList);
    }
    public void setFavouriteList(List<String> favouriteList) {
        this.favouriteList = (favouriteList != null) ? new ArrayList<>(favouriteList) : new ArrayList<>();
    }
    public List<String> getWatchHistory() {
        return new ArrayList<>(watchHistory);
    }
    public void setWatchHistory(List<String> watchHistory) {
        this.watchHistory = (watchHistory != null) ? new ArrayList<>(watchHistory) : new ArrayList<>();
    }
    @Override
    public String toString() {
        return String.format("Customer [%s, Watchlist=%d movies, Favorites=%d movies, History=%d movies]",
                super.toString(), watchlist.size(), favouriteList.size(), watchHistory.size());
    }
   
}

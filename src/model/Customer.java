package model;

import utils.ValidationException;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private List<String> watchlist;
    private List<String> favoriteList;
    private List<String> watchHistory;

    // Constructor mặc định
    public Customer () {
        super();
        this.role = "CUSTOMER";
        this.watchlist = new ArrayList<>();
        this.favoriteList = new ArrayList<>();
        this.watchHistory = new ArrayList<>();
    }

    // Constructor đầy đủ tham số
    public Customer (String id, String username, String password, String fullName, String email, 
    List<String> watchlist, List<String> favoriteList, List<String> watchHistory) throws ValidationException {
        super(id, username, password, fullName, email, "CUSTOMER");
        this.watchlist = (watchlist != null) ? watchlist : new ArrayList<>();
        this.favoriteList = (favoriteList != null) ? favoriteList : new ArrayList<>();
        this.watchHistory = (watchHistory != null) ? watchHistory : new ArrayList<>();
    }

    // === Getters & Setters ===
    public List<String> getWatchlist() {
        return watchlist;
    }

     public void setWatchlist(List<String> watchlist) {
        this.watchlist = (watchlist != null) ? watchlist : new ArrayList<>();
    }
    public List<String> getFavoriteList() {
        return favoriteList;
    }
    public void setFavoriteList(List<String> favoriteList) {
        this.favoriteList = (favoriteList != null) ? favoriteList : new ArrayList<>();
    }
    public List<String> getWatchHistory() {
        return watchHistory;
    }
    public void setWatchHistory(List<String> watchHistory) {
        this.watchHistory = (watchHistory != null) ? watchHistory : new ArrayList<>();
    }
    @Override
    public String toString() {
        return String.format("Customer [%s, Watchlist=%d movies, Favorites=%d movies, History=%d movies]",
                super.toString(), watchlist.size(), favoriteList.size(), watchHistory.size());
    }
   
}

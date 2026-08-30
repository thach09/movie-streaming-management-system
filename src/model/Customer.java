package model;

import java.util.ArrayList;
import java.util.List;
import utils.ValidationException;

public class Customer extends User {
    private List<String> watchlist;
    private List<String> favouriteList;
    private List<String> watchHistory;
    private List<WatchProgress> continueWatching;

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
        this.continueWatching = new ArrayList<>();
    }

    // Constructor đầy đủ tham số
    public Customer (String id, String username, String password, String fullName, String email, 
    List<String> watchlist, List<String> favouriteList, List<String> watchHistory) throws ValidationException {
        super(id, username, password, fullName, email, "CUSTOMER");
        this.watchlist = (watchlist != null) ? new ArrayList<>(watchlist) : new ArrayList<>();
        this.favouriteList = (favouriteList != null) ? new ArrayList<>(favouriteList) : new ArrayList<>();
        this.watchHistory = (watchHistory != null) ? new ArrayList<>(watchHistory) : new ArrayList<>();
        this.continueWatching = new ArrayList<>();
    }

    // Copy Constructor (deep copy 3 danh sách để không chia sẻ reference)
    public Customer(Customer source) {
        super(source); // Gọi copy constructor của User (protected)
        this.watchlist = new ArrayList<>(source.watchlist);
        this.favouriteList = new ArrayList<>(source.favouriteList);
        this.watchHistory = new ArrayList<>(source.watchHistory);
        this.continueWatching = new ArrayList<>();
        for (WatchProgress wp : source.continueWatching) {
            this.continueWatching.add(new WatchProgress(wp));
        }
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

    public List<WatchProgress> getContinueWatching() {
        List<WatchProgress> copy = new ArrayList<>();
        for (WatchProgress wp : continueWatching) {
            copy.add(new WatchProgress(wp));
        }
        return copy;
    }

    public void setContinueWatching(List<WatchProgress> continueWatching) {
        this.continueWatching = new ArrayList<>();
        if (continueWatching != null) {
            for (WatchProgress wp : continueWatching) {
                this.continueWatching.add(new WatchProgress(wp));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Customer [%s, Watchlist=%d movies, Favorites=%d movies, History=%d movies, ContinueWatching=%d]",
                super.toString(), watchlist.size(), favouriteList.size(), watchHistory.size(), continueWatching.size());
    }
   
}

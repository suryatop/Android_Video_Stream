package com.suryatop.youtube_clone.Models;

public class ContentModel {
    private String id;
    private String publisher;
    private String playlist;
    private String type;
    private String video;
    private String videoDescription;
    private String videoUrl;
    private String videoTags;
    private long views; // Change to long for views
    private String videoTitle;
    private String date;

    public ContentModel() {
        // Default constructor required for Firebase
    }

    public ContentModel(String id, String publisher, String playlist, String type,
                        String video, String videoDescription, String videoUrl,
                        long views, String videoTitle, String date) {
        this.id = id;
        this.publisher = publisher;
        this.playlist = playlist;
        this.type = type;
        this.video = video;
        this.videoDescription = videoDescription;
        this.videoUrl = videoUrl;
        this.views = views;
        this.videoTitle = videoTitle;
        this.date = date;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrg) {
        this.videoUrl = videoUrl;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

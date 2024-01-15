package com.rk.amii.models;

public class VideoModel {

    private final String videoURL;
    private final String videoTitle;
    private final String videoType;
    private int videoImage = 0;

    /**
     * Create a video item
     * @param videoURL video URL
     * @param videoTitle video title
     * @param videoType video type offline/online
     */
    public VideoModel(String videoURL, String videoTitle, String videoType)
    {
        this.videoURL = videoURL;
        this.videoTitle = videoTitle;
        this.videoType = videoType;
    }

    /**
     * Create a video item
     * @param videoURL video URL
     * @param videoTitle video title
     * @param videoImage video image path
     * @param videoType video type offline/online
     */
    public VideoModel(String videoURL, String videoTitle, String videoType, int videoImage)
    {
        this.videoURL = videoURL;
        this.videoTitle = videoTitle;
        this.videoType = videoType;
        this.videoImage = videoImage;
    }

    /**
     * Get the video URL
     * @return video URL
     */
    public String getVideoURL() {
        return this.videoURL;
    }

    /**
     * Get the video title
     * @return video title
     */
    public String getVideoTitle() { return this.videoTitle; }

    /**
     * Get the video type offline/online
     * @return video type
     */
    public String getVideoType() { return this.videoType; }

    /**
     * Get the video image
     * @return video image
     */
    public int getVideoImage() { return this.videoImage; }
}

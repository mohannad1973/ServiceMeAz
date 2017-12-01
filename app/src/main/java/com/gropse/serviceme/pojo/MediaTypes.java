package com.gropse.serviceme.pojo;

import java.io.Serializable;

/**
 * Created by user on 01-12-2017.
 */

public class MediaTypes implements Serializable {
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public MediaTypes(String fileId, boolean isVideo) {
        this.fileId = fileId;
        this.isVideo = isVideo;
    }

    private String fileId;
    private boolean isVideo;
}

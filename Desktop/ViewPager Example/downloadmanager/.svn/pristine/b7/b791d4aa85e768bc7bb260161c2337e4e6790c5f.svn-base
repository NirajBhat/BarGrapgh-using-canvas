package com.nexteducation.downloadmanager;

/**
 * Created by suresh on 2/10/2016.
 */
public class DownloadItemVO {
    private String id;
    private String fileType;
    private String status;
    private String url;
    private boolean signMismatch;
    private String signature;

    public long totalSize;
    public long downloadedSize;

    public DownloadItemVO(String id, String fileType, String status) {
        this.id = id;
        this.fileType = fileType;
        this.status = status;
    }

    public DownloadItemVO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getFileType() {
        return fileType;
    }

    public String getUrl() {
        if(url.startsWith("//")) {
            url = url.replace("//", "http://");
        }
        return url;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isSignMismatch() {
        return signMismatch;
    }

    public void setSignMismatch(boolean signMismatch) {
        this.signMismatch = signMismatch;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        DownloadItemVO downloadItemVO = (DownloadItemVO) o;
        if (downloadItemVO.getId().equalsIgnoreCase(this.id)) {
            return true;
        }
        return false;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package yuown.yuploader.model;

import yuown.yuploader.util.YuownUtils;

public class FileObject {
    private String fileName;
    private String fullPath;
    private long size;
    private String progress;
    private Status status;
    private long offset = 0;

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return this.fullPath;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public long getSize() {
        return this.size;
    }

    public String getKBSize() {
        return YuownUtils.longTo2Decimals(this.size, 1024) + " KB";
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getProgress() {
        return this.progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public boolean equals(Object obj) {
        if ((obj != null) && ((obj instanceof FileObject))) {
            FileObject o = (FileObject) obj;
            if (getFullPath().equals(o.getFullPath())) {
                return true;
            }
        }
        return false;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}

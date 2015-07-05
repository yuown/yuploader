package yuown.yuploader.model;

public class FileObject {
	private String fileName;
	private String fullPath;
	private long size;
	private String progress;
	private Status status;

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
		return this.size / 1024L * 100.0D / 100.0D + " KB";
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
}

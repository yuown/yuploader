package yuown.yuploader.model;

public enum Status {

	ADDED("ADDED"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED");

	private String status;

	private Status(String status) {
		this.status = status;
	}

	public String toString() {
		return status;
	};

}

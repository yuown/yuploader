package yuown.yuploader.model;

public class Theme {
	private String name;
	private String className;

	public Theme(String name, String className) {
		this.name = name;
		this.className = className;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String toString() {
		return this.name;
	}
}

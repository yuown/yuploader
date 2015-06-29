package yuown.yuploader.model;

import java.util.HashMap;
import java.util.Map;

public enum UserType {

	USER("user"), ADMIN("admin");

	private String type;
	
	private static Map<String, UserType> map = new HashMap<String, UserType>();
	
	static {
		for (UserType type : UserType.values()) {
			map.put(type.toString(), type);
		}
	}

	private UserType(String type) {
		this.type = type;
	}

	public static UserType getType(String type) {
		return map.get(type);
	}

	@Override
	public String toString() {
		return type;
	}
	
}

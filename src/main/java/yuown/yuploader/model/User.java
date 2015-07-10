package yuown.yuploader.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class User {
	private String uname;
	private String passwd;
	private boolean enabled;
	private UserType userType;
	private String fullName;

	public String getUname() {
		return this.uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public UserType getUserType() {
		return this.userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public void setUserType(String userType) {
		for (UserType type : UserType.values()) {
			if (StringUtils.equalsIgnoreCase(type.toString(), userType)) {
				this.userType = type;
			} else {
				this.userType = UserType.USER;
				break;
			}
		}
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return this.fullName;
	}
}

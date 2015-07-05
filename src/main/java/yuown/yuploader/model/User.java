package yuown.yuploader.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class User {

	private String uname;

	private String passwd;

	private boolean enabled;

	private UserType userType;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public UserType getUserType() {
		return userType;
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

}

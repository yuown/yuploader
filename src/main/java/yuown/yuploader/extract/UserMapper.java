package yuown.yuploader.extract;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import yuown.yuploader.model.User;

public class UserMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int arg1) throws SQLException {
		User user = new User();
		user.setUname(rs.getString("uname"));
		user.setPasswd(rs.getString("passwd"));
		user.setEnabled(rs.getBoolean("enabled"));
		user.setUserType(rs.getString("user_type"));
		user.setFullName(rs.getString("full_name"));
		return user;
	}

}

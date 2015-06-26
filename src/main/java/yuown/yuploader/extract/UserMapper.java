package yuown.yuploader.extract;

import org.springframework.jdbc.core.RowMapper;

import yuown.yuploader.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int arg1) throws SQLException {
        User user = new User();
        user.setUname(rs.getString("uname"));
        user.setPasswd(rs.getString("passwd"));
        user.setEnabled(rs.getBoolean("enabled"));
        return user;
    }

}

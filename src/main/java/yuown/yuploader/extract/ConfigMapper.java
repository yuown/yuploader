package yuown.yuploader.extract;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import yuown.yuploader.model.Config;
import yuown.yuploader.util.YuownUtils;

public class ConfigMapper implements RowMapper<Config> {

	@Override
	public Config mapRow(ResultSet rs, int arg1) throws SQLException {
		Config config = new Config();
		config.setName(rs.getString(YuownUtils.NAME));
		config.setValue(rs.getString(YuownUtils.VALUE));
		return config;
	}

}

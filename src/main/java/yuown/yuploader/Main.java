package yuown.yuploader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class Main {
	
	private static Uploader uploader;
	
	private static JdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		
		String VALIDATE_USER_QUERY = "select user_id from user_info where user_id = ?";
		ApplicationContext context =  new ClassPathXmlApplicationContext(new String[] {"yuploader.xml"});
		System.out.println("Main");
		uploader = (Uploader) context.getBean("uploader");
		uploader.print();
		
		jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
		
		jdbcTemplate.query(VALIDATE_USER_QUERY, new String[] {"user1"}, new ResultSetExtractor<String>() {

			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				System.out.println(rs.getString(0));
				return null;
			}
			
		});
	}

}

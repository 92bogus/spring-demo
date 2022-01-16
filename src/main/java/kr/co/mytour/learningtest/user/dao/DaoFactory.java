package kr.co.mytour.learningtest.user.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DaoFactory {
	/*
	@Bean
	public UserDaoJdbc userDao() {
		//return new UserDao(connectionMaker());
		UserDaoJdbc userDao = new UserDaoJdbc();
		//userDao.setConnectionMaker(connectionMaker());
		userDao.setDataSource(dataSource());
		
		return userDao;
	}
	
//	public AccountDao accountDao() {
//		return new AccountDao(connectionMaker());
//	}
	
//	public MessageDao messageDao() {
//		return new MessageDao(connectionMaker());
//	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
	
	@Bean
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost:3306/practice?characterEncoding=UTF8");
		dataSource.setUsername("root");
		dataSource.setPassword("1234");
		
		return dataSource;
	}

	 */
}

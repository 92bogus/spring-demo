package learningtest.user.dao;

import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import kr.co.mytour.learningtest.user.dao.CountingConnectionMaker;
import kr.co.mytour.learningtest.user.dao.CountingDaoFactory;
import kr.co.mytour.learningtest.user.dao.UserDaoJdbc;
import kr.co.mytour.learningtest.user.domain.User;

public class UserDaoConnectionCountingTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		UserDaoJdbc dao = context.getBean("userDao", UserDaoJdbc.class);
		
		User user1 = new User();
		user1.setId("signal");
		user1.setName("김신호");
		user1.setPassword("1234");
		
		dao.add(user1);
		
		User user2 = dao.get(user1.getId());
		System.out.println(user2.getId() + " 조회 성공");
		System.out.println(user2.getName());
		
		CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
		
		System.out.println("Connection counter: " + ccm.getCounter());
	}
}

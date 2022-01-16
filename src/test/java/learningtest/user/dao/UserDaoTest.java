package learningtest.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import learningtest.AppContext;
import learningtest.TestAppContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.domain.Level;
import kr.co.mytour.learningtest.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)	// 스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
//@ContextConfiguration(locations="/learningtest/test-applicationContext.xml")	// 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트 위치지정
@ContextConfiguration(classes= {AppContext.class/*, TestAppContext.class*/})
@ActiveProfiles("test")
//@DirtiesContext	// 해당 클래스 테스트에서는 컨텍스트 설정을 변경하기때문`	에 애플리케이션 컨텍스트를 공유하지 않게한다
public class UserDaoTest {
//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
////		UserDao dao = new DaoFactory().userDao();
//
//		//ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//		ApplicationContext context = new GenericXmlApplicationContext("/tour/user/applicationContext.xml");
//		UserDao dao = context.getBean("userDao", UserDao.class);	// bean의 이름은 메소드 명
//
//		User user = new User();
//		user.setId("whiteship");
//		user.setName("백기선");
//		user.setPassword("married");
//
//		dao.add(user);
//
//		System.out.println(user.getId() + " 등록 성공");
//
//		User user2 = dao.get(user.getId());
//		System.out.println(user2.getName());
//		System.out.println(user2.getPassword());
//
//		System.out.println(user2.getId() + " 조회 성공");
//
//	}

//	public static void main(String[] args) {
//		JUnitCore.main("sino.practice.tour.user.dao.UserDaoTest");
//	}
	
	/* 각 테스트에서 애플리케이션 컨텍스트를 공유하기 위한 static 필드, 메소드; 이 방법보단, 스프링이 제공해주는 테스트 컨텍스트 프레임워크 사용
	private static ApplicationContext context;
	
	@BeforeClass
	public void setUpAppContext() {
		this.context = new GenericXmlApplicationContext("/tour/user/applicationContext.xml");
	}
	*/
	@Autowired
	DefaultListableBeanFactory bf;

	@Test
	public void beans() {
		System.out.println(getClass().getName() + " Beans name start!!");
		for(String n : bf.getBeanDefinitionNames()) {
			System.out.println(n + " \t " + bf.getBean(n).getClass().getName());
		}
		System.out.println("End...");
	}

	@Autowired
	private UserDao dao;
	
	@Autowired
	DataSource dataSource; // SQLErrorCodeSQLExceptionTranslator는 DB종류를 알아내기 위해 ds를 필요로 함
	
	private User user1;
	private User user2;
	private User user3;
	
	@Autowired
	private ApplicationContext context;	// 테스트 컨텍스트에 의해 주입된다
	
	@Before
	public void setUp() {
		System.out.println(this.context);	// 각 메소드 별 컨텍스트와 this가 어떻게되는지 확인해보자; 컨텍스트는 동일
		System.out.println(this);	// this는 각 테스트 메소드 별 새로 생성되므로 다르다
		
		//ApplicationContext context = new GenericXmlApplicationContext("/tour/user/applicationContext.xml");
		//this.dao = context.getBean("userDao", UserDao.class);
		
		this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "email1@naver.com");
		this.user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "email2@naver.com");
		this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "email3@naver.com");
		
		//DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/practice?useSSL=false&characterEncoding=UTF8", "root", "1234", true);	// Test용 DataSource를 직접 DI 해준다
		//dao.setDataSource(dataSource);
	}
	
	@Test
	public void addAndGet()/* throws SQLException*/ {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
	}
	
	@Test
	public void count() /*throws SQLException*/ {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
		
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
		
	}
	
	@Test
	public void getAll() {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);	// id: gyumee
		List<User>users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);	// id: leegw700
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);	// id: bumjin
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}
	
	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
	}
	
	@Test(expected=DuplicateKeyException.class)
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
	@Test
	public void sqlExceptionTranslator() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		} catch(DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException) ex.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			
			assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
		}
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);	// 수정할 사용자
		dao.add(user2);	// 수정하지 않을 사용자
		
		user1.setName("오민규");
		user1.setPassword("springno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	
}

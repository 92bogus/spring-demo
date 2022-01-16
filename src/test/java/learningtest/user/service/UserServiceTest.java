package learningtest.user.service;

import static kr.co.mytour.learningtest.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static kr.co.mytour.learningtest.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import learningtest.AppContext;
import learningtest.TestAppContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.domain.Level;
import kr.co.mytour.learningtest.user.domain.User;
import kr.co.mytour.learningtest.user.service.TransactionHandler;
import kr.co.mytour.learningtest.user.service.UserService;
import kr.co.mytour.learningtest.user.service.UserServiceImpl;
import kr.co.mytour.learningtest.user.service.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations="/learningtest/test-applicationContext.xml")
@ContextConfiguration(classes = {AppContext.class/*, TestAppContext.class*/})
@ActiveProfiles("test")
public class UserServiceTest {
	List<User> users;
	
	@Autowired
	UserService userService;		// UserServiceTx // (Proxy) UserService
	
	@Autowired
	UserService testUserService;	// TestUserServiceImp;
	
//	@Autowired
//	UserServiceImpl userServiceImpl;	// UserServiceImpl
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	@Before
	public void setUp() {
		users = Arrays.asList(new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "email1@naver.com"),
							  new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "email2@naver.com"),
							  new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "email3@naver.com"),
							  new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "email4@naver.com"),
							  new User("green", "오민규", "p", Level.GOLD, 100, Integer.MAX_VALUE, "email5@naver.com")
		);
	}
	
	@Test
	@DirtiesContext
	public void upgradeLevels() /*throws Exception*/ {
		/*userDao.deleteAll();
		
		for(User user : users) {
			userDao.add(user);
		}
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userService.upgradeLevels();
		
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
		*/
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
		
	}
	
	// Mockito 목 오브젝트 프레임워크를 사용한 upgradeLevels() 테스트 
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		
		if(upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	// UserService 대역 클래스 
	public static class TestUserService extends UserServiceImpl {
		private String id;

		public TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	// UserService 대역 클래스
	public static class TestUserServiceImpl extends UserServiceImpl {
		private String id = "madnite1";
		
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException {
		
	}
	
//	@Test
//	@DirtiesContext
//	public void upgradeAllOrNothingUsingProxyFactoryBean() throws Exception {
//		// Target
//		TestUserService testUserService = new TestUserService(users.get(3).getId());
//		testUserService.setUserDao(this.userDao);
//		testUserService.setMailSender(this.mailSender);
//		
//		//TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
//		ProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", ProxyFactoryBean.class);
//		txProxyFactoryBean.setTarget(testUserService);
//		UserService txUserService = (UserService) txProxyFactoryBean.getObject();
//		
//		userDao.deleteAll();
//		for(User user : users) {
//			userDao.add(user);
//		}
//		
//		try {
//			txUserService.upgradeLevels();
//			fail("TestUserServiceException expected");
//		} catch(TestUserServiceException e) {
//			
//		}
//		
//		checkLevelUpgraded(users.get(1), false);
//	}
	
	@Test
	public void upgradeAllOrNothingUsingProxy() {
		// Target
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setMailSender(this.mailSender);
		
		// InvocationHandler
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTransactionManager(transactionManager);
		txHandler.setTarget(testUserService);
		txHandler.setPattern("upgradeLevels");
		
		// Proxied Object
		UserService txUserSerivce = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(), 
				new Class[] { UserService.class },
				txHandler);
		
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		try {
			txUserSerivce.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	@Test
	public void upgradeAllOrNothing() /*throws Exception*/ {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		//testUserService.setDataSource(this.dataSource);
		//testUserService.setTransactionManager(this.transactionManager);
		testUserService.setMailSender(this.mailSender);
		
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	@Test
	public void upgradeAllOrNothingUsingTestUserServiceImpl() {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	static class MockMailSender implements MailSender {
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]);
		}

		@Override
		public void send(SimpleMailMessage... mailMessage) throws MailException {
			
		}
	}
	
	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList<User>();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}
		
		@Override
		public void add(User user) {
			throw new UnsupportedOperationException();
		}

		@Override
		public User get(String id) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<User> getAll() {
			return this.users;
		}

		@Override
		public void deleteAll() {
			throw new UnsupportedOperationException();			
		}

		@Override
		public int getCount() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void update(User user) {
			updated.add(user);
		}
		
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
}

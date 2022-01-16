package kr.co.mytour.learningtest.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.domain.Level;
import kr.co.mytour.learningtest.user.domain.User;

//@Component
@Service("userService")
public class UserServiceImpl implements UserService {
	//트랜잭션 동기화에 사용할 Connection을 생성할 때 사용할 DataSource
//	private DataSource dataSource;
//	
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}

	@Autowired
	UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
// 나중에 DI를 통해 따로 작업해보기
//	UserLevelUpgradePolicy userLevelUpgradePolicy;
//	
//	public void setUserLvelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
//		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
//	}
	//private PlatformTransactionManager transactionManager;

//	public void setTransactionManager(PlatformTransactionManager transactionManager) {
//		this.transactionManager = transactionManager;
//	}

	@Autowired
	private MailSender mailSender;
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	/**
	 * 유저 레벨 업그레이드
	 */
	@Override
	public void upgradeLevels() /*throws Exception */ {
		
		// 트랜잭션 동기화 관리자; 로컬트랜잭션으로 DB가 여러개일 경우 사용할 수 없다. JTA를 이용한 글로벌 트랙잭션을 고려할 것.
//		TransactionSynchronizationManager.initSynchronization();
//		Connection c = DataSourceUtils.getConnection(dataSource);	// DataSourceUtils는 커넥션 생성 뿐 아니라 저장소에 바인등까지 같이 처리해준다.
//		c.setAutoCommit(false);
		
//		--> 글로벌 트랜잭션을 이용하기 위해 JTA API를 사용하면 다시 UserService가 UserDaoJDBC에 종속되버리는 현상이 발생함

//		--> JDBC, JTA, Hibernate, JPA, JDO, 심지어 JMS의 트랜잭션을 보면 공통적인 패턴이 있다.
//		--> 이 공통적인 점을 추상화한 스프링의 트랜잭션 서비스(PlatformTransactionManager)를 이용해본다.
		
//		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource); 
		
		//TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		//try {
		
			List<User> users = userDao.getAll();
	//		for(User user : users) {
	//			Boolean changed = null;
	//			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
	//				user.setLevel(Level.SILVER);
	//				changed = true;
	//			} else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
	//				user.setLevel(Level.GOLD);
	//				changed = true;
	//			} else if(user.getLevel() == Level.GOLD) {
	//				changed = false;
	//			} else {
	//				changed = false;
	//			}
	//			
	//			if(changed) {
	//				userDao.update(user);
	//			}
	//		}
		
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
			//upgradeLevelsInternal();
			//c.commit();
			//this.transactionManager.commit(status);
		//} catch(RuntimeException e) {
			//c.rollback();
			//this.transactionManager.rollback(status);
			//throw e;
		/*} /*finally {
			DataSourceUtils.releaseConnection(c, dataSource);
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}*/
	}
	
//	private void upgradeLevelsInternal() {
//		List<User> users = userDao.getAll();
//		
//		for(User user : users) {
//			if(canUpgradeLevel(user)) {
//				upgradeLevel(user);
//			}
//		}
//	}

	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		
		switch(currentLevel) {
			case BASIC:	return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	protected void upgradeLevel(User user) {
//		if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
//		else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
//		userDao.update(user);
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeEMail(user);
	}
	
	/**
	 * 유저 정보 추가
	 * @param user
	 */
	@Override
	public void add(User user) {
		if(user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
	
	private void sendUpgradeEMail(User user) {
/* JavaMail API를 이용한 방법*/
		//		Properties props = new Properties();
//		props.put("mail.smtp.host", "mail.ksug.org");
//		Session s = Session.getInstance(props, null);
//		
//		MimeMessage message = new MimeMessage(s);
//		
//		try {
//			message.setFrom(new InternetAddress("useradmin@ksug.org"));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//			message.setSubject("Upgrade 안내");
//			message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");
//			
//			Transport.send(message);
//		} catch(AddressException e) {
//			throw new RuntimeException(e);
//		} catch(MessagingException e) {
//			throw new RuntimeException(e);
//		} /*catch(UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		} */
		
		/* 스프링의 JavaMail 서비스를 추상화한 MailSender를 이용한 방법 */
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setHost("mail.server.com");
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");
		
		mailSender.send(mailMessage);
		
	}
}

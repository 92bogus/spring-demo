package learningtest;

import com.mysql.jdbc.Driver;
import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.dao.UserDaoJdbc;
import kr.co.mytour.learningtest.user.domain.User;
import kr.co.mytour.learningtest.user.service.DummyMailSender;
import kr.co.mytour.learningtest.user.service.UserService;
import kr.co.mytour.learningtest.user.service.UserServiceImpl;
import kr.co.mytour.learningtest.user.sqlservice.EmbeddedDbSqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.OxmSqlService;
import kr.co.mytour.learningtest.user.sqlservice.SqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.SqlService;
import learningtest.user.service.UserServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@ImportResource("/learningtest/test-applicationContext.xml")    // xml 설정정보
@Import({SqlServiceContext.class, TestAppContext.class, ProductionAppContext.class})    // 프로파일을 적용했으므로 모두 임포트해도 된다.                                // Java 설정정보
@ComponentScan(basePackages = "kr.co.mytour.learningtest.user") // 모든 클래스패스를 뒤지는 자동 빈등록은 부담이 많이가므로 패키지를 지정해준다.
public class AppContext {

    //@Autowired
    //SqlService sqlService;

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost:3306/practice?useSSL=false&characterEncoding=UTF8");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());

        return tm;
    }

//    @Bean
//    public UserDao userDao() {
//        UserDaoJdbc dao = new UserDaoJdbc();
//
//        //dao.setDataSource(dataSource()); // UserDAOJdbc에서 Autowired로 대체
//        //dao.setSqlService(this.sqlService);
//
//        return dao;
//    }
    /*@Autowired
    UserDao userDao;*/

    /*@Bean
    public UserService userService() {
        UserServiceImpl service = new UserServiceImpl();

        //service.setUserDao(userDao());

        //service.setUserDao(this.userDao);
        //service.setMailSender(mailSender());

        return service;
    }*/
    // 테스트용 빈 설정은 테스트용 컨텍스트 설정파일로 옮김
    /*@Bean
    public UserService testUserService() {
        UserServiceTest.TestUserServiceImpl testService = new UserServiceTest.TestUserServiceImpl();

        //testService.setUserDao(userDao());
        testService.setUserDao(this.userDao);
        testService.setMailSender(mailSender());

        return testService;
    }*/

    /*@Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }*/

    // SqlService 따로분리
}

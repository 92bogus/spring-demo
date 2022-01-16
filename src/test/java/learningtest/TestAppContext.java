package learningtest;

import kr.co.mytour.learningtest.user.dao.UserDao;
import kr.co.mytour.learningtest.user.service.DummyMailSender;
import kr.co.mytour.learningtest.user.service.UserService;
import learningtest.user.service.UserServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

@Configuration
@Profile("test")
public class TestAppContext {
    /*@Autowired
    UserDao userDao;*/

    @Bean
    public UserService testUserService() {
        UserServiceTest.TestUserServiceImpl testService = new UserServiceTest.TestUserServiceImpl();
//        testService.setUserDao(this.userDao);
//        testService.setMailSender(mailSender());
        return testService;
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

}

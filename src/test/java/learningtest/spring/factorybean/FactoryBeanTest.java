package learningtest.spring.factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/FactoryBeanTest-context.xml")
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;
	
	@Test
	public void getMessageFromFactoryBean() {
		Object message = context.getBean("message");
		assertThat(message, is(Message.class));
		assertThat(((Message) message).getText(), is("Factory Bean"));
	}
	
	@Test
	public void getFactoryBean() {
		Object factory = context.getBean("&message");	// 빈 명 앞에 '&'를 붙여주면 팩토리빈 자체를 가져온다 
		assertThat(factory, is(MessageFactoryBean.class));
	}
}

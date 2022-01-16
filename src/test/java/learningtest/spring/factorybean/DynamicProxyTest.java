package learningtest.spring.factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DynamicProxyTest {
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());	// 직접 구현했던 TxProxyFactoryBean과 달리 타겟을 직접참조한다 
		pfBean.addAdvice(new UppercaseAdvice());
		
		Hello proxiedHello = (Hello) pfBean.getObject();	// 직접 가져온다.스프링의 설정파일에서 팩토리빈을 등록해놓았을 때 가져오는 메소드 
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		// Target
		pfBean.setTarget(new HelloTarget());
		
		// 포인트컷
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		
	}

	@Test
	public void classNamePointcutAdvisor() {
		// 포인트컷 재정
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			@Override
			public ClassFilter getClassFilter() {
				return new ClassFilter() {

					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
					
				};
			}
		};
		
		classMethodPointcut.setMappedName("sayH*");
		
		// 테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		class HelloWorld extends HelloTarget { };
		
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToby extends HelloTarget { };
		
		checkAdviced(new HelloToby(), classMethodPointcut, true);
	}
	
	private void checkAdviced(Object target, NameMatchMethodPointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if(adviced) {
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		} else {
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}
	}

	// 어드바이스(부가기능)
	static class UppercaseAdvice implements MethodInterceptor {

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();	// 타깃오브젝트의 메소드 실행 
			return ret.toUpperCase();
		}
		
	}
	
	// 인터페이스 
	static interface Hello {
		String sayHello(String name);
		String sayHi(String name);
		String sayThankYou(String name);
	}
	
	// 구현체 
	static class HelloTarget implements Hello {

		@Override
		public String sayHello(String name) {
			return "Hello " + name;
		}

		@Override
		public String sayHi(String name) {
			return "Hi " + name;
		}

		@Override
		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
		
	}
}

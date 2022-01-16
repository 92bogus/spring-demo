/*
package learningtest.junit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

*/
/*
 * Purpose	  : JUnit의 테스트 메소드는 수행할 때 마다 새로운 오브젝트를 만드는지 확인, 스프링 테스트 컨텍스트가 주입해주는
 * 애플리케이션 컨텍스트는 각 테스트 메소드끼리 공유되는지 확인
 * Result     :  
 * Check Date : 
 *//*

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration	// Default 위치(클래스패스)에 있는 junit.xml 사용
public class JUnitTest {
	@Autowired ApplicationContext context;
	
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	static ApplicationContext contextObject = null;
	
	@Test
	public void test1() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(contextObject == null || contextObject == this.context, is(true));
		contextObject = this.context;
	}
	
	@Test 
	public void test2() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertTrue(contextObject == null || contextObject == this.context);
		contextObject = this.context;
	}
	
	@Test
	public void test3() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertThat(contextObject, either(is(nullValue())).or(is(this.context)));
		contextObject = this.context;
	}
}
*/

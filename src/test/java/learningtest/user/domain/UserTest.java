package learningtest.user.domain;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import kr.co.mytour.learningtest.user.domain.Level;
import kr.co.mytour.learningtest.user.domain.User;

public class UserTest {
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}
	
	@Test()
	public void  upgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if(level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
}

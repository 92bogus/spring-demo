package learningtest.spring.factorybean.embeddeddb;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbTest {
	EmbeddedDatabase db;
	JdbcTemplate template;
	
	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("classpath:/learningtest/embeddedDatabase/schema.sql")
				.addScript("classpath:/learningtest/embeddedDatabase/data.sql")
				.build();
	}
}

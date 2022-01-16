package learningtest.embeddeddb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmbeddedDbTest {
    EmbeddedDatabase db;    // DataSource를 상속하여 shutDown()이 추가된 인터페이스
    JdbcTemplate template;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:/learningtest/embeddeddb/schema.sql")
                .addScript("classpath:/learningtest/embeddeddb/data.sql")
                .build();

        template = new JdbcTemplate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void initData() {
        assertThat((int) template.queryForObject("select count(*) from sqlmap", Integer.class), is(2));

        List<Map<String, Object>> list = template.queryForList("select *  from sqlmap order by key_");
        assertThat((String) list.get(0).get("key_"), is("KEY1"));
        assertThat((String) list.get(0).get("sql_"), is("SQL1"));
        assertThat((String) list.get(1).get("key_"), is("KEY2"));
        assertThat((String) list.get(1).get("sql_"), is("SQL2"));
    }

    @Test
    public void insert() {
        template.update("insert into sqlmap(key_, sql_) values(?, ?)", "KEY3", "SQL3");

        assertThat((int) template.queryForObject("select count(*) from sqlmap", Integer.class), is(3));
    }
}

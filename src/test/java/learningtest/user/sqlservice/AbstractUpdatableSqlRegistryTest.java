package learningtest.user.sqlservice;

import kr.co.mytour.learningtest.user.sqlservice.ConcurrentHashMapSqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.SqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.UpdatableSqlRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public abstract class AbstractUpdatableSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;

    @Before
    public void setUp() {
        sqlRegistry = createUpdatableSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

    @Test
    public void find() {
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    protected void checkFindResult(String expected1, String expected2, String expected3) {
        assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
        assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
        assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
    }

    @Test(expected = SqlRegistry.SqlNotFoundException.class)
    public void unknownKey() {
        sqlRegistry.findSql("SQL9999!@#$");
    }

    @Test
    public void updateSingle() {
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFindResult("SQL1", "Modified2", "SQL3");
    }

    @Test
    public void updateMulti() {
        Map<String, String> sqlmap = new HashMap<String, String>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY3", "Modified3");

        sqlRegistry.updateSql(sqlmap);
        checkFindResult("Modified1", "SQL2", "Modified3");
    }

    @Test(expected = UpdatableSqlRegistry.SqlUpdateFailureException.class)
    public void updateWithNotExistingKey() {
        sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
    }
}

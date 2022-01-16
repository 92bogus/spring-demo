package learningtest.user.sqlservice;

import kr.co.mytour.learningtest.user.sqlservice.ConcurrentHashMapSqlRegistry;
import kr.co.mytour.learningtest.user.sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}

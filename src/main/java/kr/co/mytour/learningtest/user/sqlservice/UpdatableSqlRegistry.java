package kr.co.mytour.learningtest.user.sqlservice;

import java.util.Map;

public interface UpdatableSqlRegistry extends SqlRegistry {
    public void updateSql(String key, String sql) throws SqlUpdateFailureException;

    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;

    public class SqlUpdateFailureException extends RuntimeException {
        public SqlUpdateFailureException(String message) {
            super(message);
        }

        public SqlUpdateFailureException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

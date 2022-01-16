package kr.co.mytour.learningtest.user.sqlservice;

public interface SqlRegistry {
	void registerSql(String key, String sql);
	String findSql(String key) throws SqlNotFoundException;
	
	public class SqlNotFoundException extends RuntimeException {
		public SqlNotFoundException(String message) {
			super(message);
		}
		
		public SqlNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}

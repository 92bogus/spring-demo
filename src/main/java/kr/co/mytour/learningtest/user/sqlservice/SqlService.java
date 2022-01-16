package kr.co.mytour.learningtest.user.sqlservice;

public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException;
	
	public class SqlRetrievalFailureException extends RuntimeException {
		public SqlRetrievalFailureException(Throwable cause) {
			super(cause);
		}
		
		public SqlRetrievalFailureException(String message) {
			super(message);
		}
		
		public SqlRetrievalFailureException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}

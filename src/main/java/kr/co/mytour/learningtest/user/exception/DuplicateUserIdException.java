package kr.co.mytour.learningtest.user.exception;

public class DuplicateUserIdException extends RuntimeException {
	public DuplicateUserIdException(Throwable cause) {
		super(cause);
	}
}

package kr.co.mytour.learningtest.user.dao;

import java.sql.Connection;
import java.util.List;

import kr.co.mytour.learningtest.user.domain.User;

public interface UserDao {
	void add(/*Connection c, */ User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
	void update(User user);
}

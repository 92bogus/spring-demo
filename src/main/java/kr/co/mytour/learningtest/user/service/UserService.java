package kr.co.mytour.learningtest.user.service;

import kr.co.mytour.learningtest.user.domain.User;

public interface UserService {
	void add(User user);
	void upgradeLevels();
}

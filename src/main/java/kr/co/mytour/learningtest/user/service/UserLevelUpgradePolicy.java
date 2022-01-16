package kr.co.mytour.learningtest.user.service;

import kr.co.mytour.learningtest.user.domain.User;

public interface UserLevelUpgradePolicy {
	boolean canUpgradeLevel(User user);
	void upgradeLevel(User user);
}

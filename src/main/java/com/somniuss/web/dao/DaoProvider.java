package com.somniuss.web.dao;

import com.somniuss.web.dao.impl.NewsDaoImpl;
import com.somniuss.web.dao.impl.SQLSoundeffectsDao;
import com.somniuss.web.dao.impl.SQLUserDao;

public class DaoProvider {
	private static DaoProvider instance = new DaoProvider();

	private DaoProvider() {
	}

	private final UserDao userDao = new SQLUserDao();
	private final SoundeffectsDao soundeffectsDao = new SQLSoundeffectsDao();
	private final NewsDao newsDao = new NewsDaoImpl();

	public UserDao getUserDao() {
		return userDao;
	}

	public SoundeffectsDao getSQLSoundeffectsDao() {
		return soundeffectsDao;
	}

	public NewsDao getNewsDao() {
		return newsDao;
	}

	public static DaoProvider getInstance() {
		return instance;
	}

}

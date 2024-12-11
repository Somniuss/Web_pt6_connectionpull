package com.somniuss.web.dao;

import com.somniuss.web.bean.User;

public interface UserDao {
    User registration(String name, String email, String password) throws DaoException;

    User authorization(String login, String password) throws DaoException;

    boolean isUserExistsByEmail(String email) throws DaoException;

    boolean isUserExistsByName(String name) throws DaoException;
}

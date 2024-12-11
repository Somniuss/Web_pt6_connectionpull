package com.somniuss.web.dao.impl;

import com.somniuss.web.ConnectionPool.ConnectionPool;
import com.somniuss.web.ConnectionPool.ConnectionPoolException;
import com.somniuss.web.bean.User;
import com.somniuss.web.dao.DaoException;
import com.somniuss.web.dao.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDao implements UserDao {
    // Параметры для подключения к базе данных
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/soundeffects_management_v1?serverTimezone=Europe/Minsk";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";
    private static final int POOL_SIZE = 10;

    // Пул соединений
    private static ConnectionPool connectionPool;

    static {
        try {
            connectionPool = ConnectionPool.create(JDBC_URL, JDBC_USER, JDBC_PASSWORD, POOL_SIZE);
        } catch (ConnectionPoolException e) {
            throw new RuntimeException("Ошибка при инициализации пула соединений", e);
        }
    }

    @Override
    public User registration(String name, String email, String password) throws DaoException {
        User user = new User(name, email, password);

        try (Connection con = connectionPool.takeConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)")) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return user;
            } else {
                throw new DaoException("Ошибка при добавлении пользователя");
            }
        } catch (ConnectionPoolException e) {
            throw new DaoException("Ошибка пула соединений при регистрации пользователя", e);
        } catch (SQLException e) {
            throw new DaoException("Ошибка при выполнении SQL-запроса", e);
        }
    }

    @Override
    public boolean isUserExistsByName(String name) throws DaoException {
        return isUserExists("name", name);
    }

    @Override
    public boolean isUserExistsByEmail(String email) throws DaoException {
        return isUserExists("email", email);
    }

    private boolean isUserExists(String fieldName, String value) throws DaoException {
        String query = "SELECT 1 FROM users WHERE " + fieldName + " = ?";
        try (Connection con = connectionPool.takeConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Если запись найдена, возвращаем true
            }
        } catch (ConnectionPoolException e) {
            throw new DaoException("Ошибка пула соединений при проверке пользователя", e);
        } catch (SQLException e) {
            throw new DaoException("Ошибка при выполнении SQL-запроса", e);
        }
    }

    @Override
    public User authorization(String name, String password) throws DaoException {
        try (Connection con = connectionPool.takeConnection()) {
            String query = "SELECT * FROM users WHERE name = ? AND password = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new User(rs.getString("name"), rs.getString("email"), rs.getString("password"));
                    }
                    return null; // Пользователь не найден
                }
            }
        } catch (ConnectionPoolException e) {
            throw new DaoException("Ошибка пула соединений при авторизации", e);
        } catch (SQLException e) {
            throw new DaoException("Ошибка при выполнении SQL-запроса", e);
        }
    }
}

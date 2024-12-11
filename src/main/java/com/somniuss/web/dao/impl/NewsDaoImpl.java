package com.somniuss.web.dao.impl;

import com.somniuss.web.bean.News;
import com.somniuss.web.dao.NewsDao;
import com.somniuss.web.dao.DaoException;
import com.somniuss.web.ConnectionPool.ConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NewsDaoImpl implements NewsDao {

    // Параметры для подключения к базе данных
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/soundeffects_management_v1?serverTimezone=Europe/Minsk";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";
    private static final int POOL_SIZE = 10; // Размер пула соединений

    // Пул соединений
    private static ConnectionPool connectionPool;

    static {
        try {
            // Создаем пул соединений при инициализации класса
            connectionPool = ConnectionPool.create(JDBC_URL, JDBC_USER, JDBC_PASSWORD, POOL_SIZE);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при инициализации пула соединений", e);
        }
    }

    @Override
    public List<News> getAllNews() throws DaoException {
        List<News> newsList = new ArrayList<>();
        
        try (Connection connection = connectionPool.takeConnection(); // Получаем соединение из пула
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM news;"); // Указываем SQL-запрос
             ResultSet resultSet = statement.executeQuery()) {
            
            // Обрабатываем результат запроса
            while (resultSet.next()) {
                News news = new News();
                news.setTitle(resultSet.getString("title"));
                news.setContent(resultSet.getString("content"));
                newsList.add(news);
            }
        } catch (Exception e) {
            throw new DaoException("Ошибка при получении новостей", e);
        }
        
        return newsList;
    }
}

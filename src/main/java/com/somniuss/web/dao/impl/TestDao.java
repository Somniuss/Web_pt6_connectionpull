package com.somniuss.web.dao.impl;

import java.util.List;

import com.somniuss.web.bean.News;
import com.somniuss.web.dao.DaoException;

public class TestDao {
    public static void main(String[] args) {
        NewsDaoImpl dao = new NewsDaoImpl();
        try {
            List<News> newsList = dao.getAllNews();
            for (News news : newsList) {
                System.out.println("Title: " + news.getTitle());
                System.out.println("Content: " + news.getContent());
                System.out.println();
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }
}

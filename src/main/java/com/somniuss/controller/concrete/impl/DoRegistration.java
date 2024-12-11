package com.somniuss.controller.concrete.impl;

import java.io.IOException;
import com.somniuss.web.controller.concrete.Command;
import com.somniuss.web.dao.impl.SQLUserDao;
import com.somniuss.web.dao.DaoException;
import com.somniuss.web.bean.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DoRegistration implements Command {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Получение данных из формы
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Проверка на пустые поля
        if (name == null || email == null || password == null || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Все поля должны быть заполнены.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/registration.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Вызов DAO для проверки уникальности имени и email
        SQLUserDao userDao = new SQLUserDao();
        try {
            if (userDao.isUserExistsByName(name)) {
                request.setAttribute("error", "Пользователь с таким именем уже зарегистрирован.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/registration.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (userDao.isUserExistsByEmail(email)) {
                request.setAttribute("error", "Пользователь с таким email уже зарегистрирован.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/registration.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Регистрация пользователя в базе данных
            User newUser = userDao.registration(name, email, password);
            if (newUser != null) {
                // Если регистрация успешна, перенаправляем на страницу входа
                response.sendRedirect("MyController?command=go_to_index_page");
            } else {
                request.setAttribute("error", "Ошибка регистрации. Попробуйте снова.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/registration.jsp");
                dispatcher.forward(request, response);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка базы данных. Попробуйте позже.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/registration.jsp");
            dispatcher.forward(request, response);
        }
    }
}

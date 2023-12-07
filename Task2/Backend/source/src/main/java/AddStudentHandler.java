import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AddStudentHandler {
    private static final Logger logger = Logger.getLogger(AddStudentHandler.class.getName());

    // Вложенный класс, реализующий интерфейс HttpHandler
    static class ApiHandler implements HttpHandler {
        private final DatabaseConfig databaseConfig;

        // Конструктор класса ApiHandler, принимающий объект databaseConfig
        public ApiHandler(DatabaseConfig databaseConfig) {
            this.databaseConfig = databaseConfig;
        }

        // Метод обработки HTTP-запроса
        @Override
        public void handle(HttpExchange exchange) {
            // Обработка запросов OPTIONS (используется для предварительной проверки CORS)
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Api.addCorsHeaders(exchange);
                try {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Использую OutputStream для отправки JSON
            try {
                // Вызов метода AddStudentRequest для обработки запроса
                AddStudentRequest(exchange, databaseConfig);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Не удалось обработать запрос.", e);
            }
        }
    }

    // Метод для обработки запроса на добавление студента
    private static void AddStudentRequest(HttpExchange exchange, DatabaseConfig databaseConfig) throws IOException {
        // Добавление заголовков CORS к ответу
        Api.addCorsHeaders(exchange);

        // Попытка получить данные запроса
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            // Чтение тела запроса и формирование строки requestBody
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }

            // Использование библиотеки Gson для преобразования JSON в объект Student
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            Student newStudent = gson.fromJson(requestBody.toString(), Student.class);

            // Добавление нового студента в базу данных
            if (addStudentToDatabase(newStudent, databaseConfig)) {
                exchange.sendResponseHeaders(201, 0); // Created
            } else {
                exchange.sendResponseHeaders(500, 0); // Internal Server Error
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка в процессе обработки запроса.", e);
        } finally {
            exchange.close();
        }
    }

    // Метод для добавления студента в базу данных
    private static boolean addStudentToDatabase(Student student, DatabaseConfig databaseConfig) {
        // Проверка наличия данных о студенте
        if (student == null ) {
            // Обработка случаев, когда student являются null
            return false;
        }

        // Попытка установить соединение с базой данных и выполнить запрос на добавление студента
        try (Connection connection = DriverManager.getConnection(
                databaseConfig.getJdbcUrl(),
                databaseConfig.getDbUser(),
                databaseConfig.getDbPassword()
        )) {
            String query = "INSERT INTO students (name, surname, patronymic, bd_date, study_group) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, student.getName());
                statement.setString(2, student.getSurname());
                statement.setString(3, student.getPatronymic());
                statement.setDate(4, student.getBd_date());
                statement.setString(5, student.getStudy_group());

                // Выполнение запроса и проверка успешности операции
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // Обработка ошибок, возникших при выполнении запроса к базе данных
            logger.log(Level.SEVERE, "Произошла ошибка в процессе добавления студента в базу данных.", e);
            return false;
        }
    }
}

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteStudentHandler {
    private static final Logger logger = Logger.getLogger(DeleteStudentHandler.class.getName());

    // Класс вложен в DeleteStudentHandler и реализует интерфейс HttpHandler
    static class ApiHandler implements HttpHandler {
        private final DatabaseConfig databaseConfig;

        // Конструктор, принимающий конфигурацию базы данных
        public ApiHandler(DatabaseConfig databaseConfig) {
            this.databaseConfig = databaseConfig;
        }

        // Метод, обрабатывающий HTTP-запрос
        @Override
        public void handle(HttpExchange exchange) {
            // Проверка, является ли запрос методом OPTIONS (используется для предварительной проверки запроса)
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Api.addCorsHeaders(exchange);
                try {
                    exchange.sendResponseHeaders(204, -1); // Ответ 204 - No Content
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                deleteStudentRequest(exchange, databaseConfig);
            } catch (IOException e) {
                // Логгирование ошибки
                logger.log(Level.SEVERE, "Не удалось обработать запрос.", e);
            }
        }
    }

    // Метод для обработки запроса на удаление студента
    private static void deleteStudentRequest(HttpExchange exchange, DatabaseConfig databaseConfig) throws IOException {
        Api.addCorsHeaders(exchange); // Добавление заголовков для поддержки CORS

        // Чтение тела запроса
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }

            // Получение ID студента из тела запроса
            int studentId = Integer.parseInt(requestBody.toString().trim());

            // Удаление студента из базы данных
            if (deleteStudentFromDatabase(studentId, databaseConfig)) {
                exchange.sendResponseHeaders(200, 0); // Ответ 200 - OK
            } else {
                exchange.sendResponseHeaders(500, 0); // Ответ 500 - Internal Server Error
            }
        } catch (Exception e) {
            // Логгирование ошибки
            logger.log(Level.SEVERE, "Произошла ошибка в процессе обработки запроса.", e);
        } finally {
            exchange.close(); // Закрытие соединения
        }
    }

    // Метод для удаления студента из базы данных
    private static boolean deleteStudentFromDatabase(int studentId, DatabaseConfig databaseConfig) {
        // Использование try-with-resources для автоматического закрытия ресурсов
        try (Connection connection = DriverManager.getConnection(
                databaseConfig.getJdbcUrl(),
                databaseConfig.getDbUser(),
                databaseConfig.getDbPassword()
        )) {
            String query = "DELETE FROM students WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);

                // Выполнение SQL-запроса на удаление
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // Логгирование ошибки
            logger.log(Level.SEVERE, "Произошла ошибка в процессе удаления студента из базы данных.", e);
            return false;
        }
    }
}

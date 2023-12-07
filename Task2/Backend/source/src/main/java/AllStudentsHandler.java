import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllStudentsHandler {
    private static final Logger logger = Logger.getLogger(AllStudentsHandler.class.getName());

    // Вложенный класс, реализующий HttpHandler для обработки HTTP-запросов
    static class ApiHandler implements HttpHandler {
        private final DatabaseConfig databaseConfig;

        // Конструктор, принимающий конфигурацию базы данных
        public ApiHandler(DatabaseConfig databaseConfig) {
            this.databaseConfig = databaseConfig;
        }

        // Метод обработки HTTP-запроса
        @Override
        public void handle(HttpExchange exchange) {
            // Добавление заголовков CORS для разрешения кросс-доменных запросов
            Api.addCorsHeaders(exchange);

            // Используйте OutputStream для отправки JSON
            try (OutputStream os = exchange.getResponseBody()) {
                // Получение данных о всех студентах из базы данных в виде строки JSON
                byte[] jsonBytes = getAllStudentsInfoFromDatabase(databaseConfig).getBytes(StandardCharsets.UTF_8);
                // Установка кода ответа и длины ответа
                exchange.sendResponseHeaders(200, jsonBytes.length);
                // Отправка данных в ответ
                os.write(jsonBytes);
            } catch (IOException e) {
                // Логгирование ошибки в случае проблем с формированием ответа
                logger.log(Level.SEVERE, "Не удалось сформировать ответ.", e);
            }
        }
    }

    // Метод для получения информации о всех студентах из базы данных
    private static String getAllStudentsInfoFromDatabase(DatabaseConfig databaseConfig) {
        // Инициализация списка для хранения информации о студентах
        List<Student> allStudents = new ArrayList<>();

        // Обработка исключений связанных с работой с базой данных
        try (Connection connection = DriverManager.getConnection(
                databaseConfig.getJdbcUrl(),
                databaseConfig.getDbUser(),
                databaseConfig.getDbPassword()
        )) {
            // SQL-запрос для выборки всех данных из таблицы students
            String query = "SELECT * FROM students";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обработка результатов запроса
                    while (resultSet.next()) {
                        // Создание объекта Student на основе данных из результата запроса
                        Student student = new Student(
                                resultSet.getString("id"),
                                resultSet.getString("name"),
                                resultSet.getString("surname"),
                                resultSet.getString("patronymic"),
                                resultSet.getDate("bd_date"),
                                resultSet.getString("study_group")
                        );
                        // Добавление объекта Student в список
                        allStudents.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            // Логгирование ошибки в случае проблем с выполнением запроса к базе данных
            logger.log(Level.SEVERE, "Произошла ошибка во время попытки получения данных о студентах.", e);
        }

        // Преобразование списка студентов в формат JSON
        Gson gson = new Gson();
        return gson.toJson(allStudents);
    }
}

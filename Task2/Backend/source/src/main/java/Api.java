import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;

// Класс, представляющий студента
class Student {
    private final String id;
    private final String name;
    private final String surname;
    private final String patronymic;
    private final Date bd_date;
    private final String study_group;


    // Конструктор для создания объекта Student
    public Student(String id, String name, String surname, String patronymic, Date bd_date, String study_group) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.bd_date = bd_date;
        this.study_group = study_group;
    }

    // Геттеры для получения значений полей
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public Date getBd_date() {
        return bd_date;
    }

    public String getStudy_group() {
        return study_group;
    }
}

// Главный класс приложения для создания и запуска HTTP-сервера
public class Api {
    public static void main(String[] args) throws IOException {
        // Создание сетевого адреса и HTTP-сервера
        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);

        // Конфигурация базы данных
        DatabaseConfig databaseConfig = new DatabaseConfig();

        // Установка обработчиков контекстов для различных API-эндпоинтов
        server.createContext("/api/students/delete", new DeleteStudentHandler.ApiHandler(databaseConfig));
        server.createContext("/api/students/all", new AllStudentsHandler.ApiHandler(databaseConfig));
        server.createContext("/api/students/add", new AddStudentHandler.ApiHandler(databaseConfig));

        // Установка исполнителя (Executor) в null, чтобы использовать дефолтный
        server.setExecutor(null);

        // Запуск HTTP-сервера
        server.start();
        System.out.println("Сервер запущен на порту 8080");
    }

    // Метод для добавления HTTP-заголовков CORS
    public static void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
        headers.set("Access-Control-Allow-Origin", exchange.getRequestHeaders().getFirst("Origin"));
    }
}

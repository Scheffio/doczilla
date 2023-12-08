import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/todos", new TodoHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Запущено на порту 8080");
    }

    public static String extractUrlParameters(String urlString) {
        // Создаем регулярное выражение для поиска строки после "?url="
        Pattern pattern = Pattern.compile("\\?url=(.*)");

        // Создаем объект Matcher для выполнения поиска
        Matcher matcher = pattern.matcher(urlString);

        // Если находит совпадение, возвращаем найденную строку
        if (matcher.find()) {
            return matcher.group(1);
        }

        // Если не находит совпадение, возвращаем null
        return null;
    }

    static class TodoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Получаем URL API из запроса клиента
            String url = exchange.getRequestURI().toString();
            String apiUrl = extractUrlParameters(url);

            // Устанавливаем CORS-заголовки для разрешения запросов с фронтенда
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type,Authorization");

            // Если это OPTIONS запрос, отправляем только заголовки
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // Создаем HTTP-соединение
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            // Получаем входной поток от удаленного сервера
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 OutputStream localOutputStream = exchange.getResponseBody()) {

                // Читаем весь ответ от удаленного сервера в строку
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Преобразуем строку в массив байт и отправляем на клиент
                byte[] responseBytes = result.toString().getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                localOutputStream.write(responseBytes);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0); // Internal Server Error
            } finally {
                // Закрываем соединение и завершаем обработку запроса
                connection.disconnect();
            }
        }
    }
}

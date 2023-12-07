// Класс DatabaseConfig представляет конфигурацию базы данных
public class DatabaseConfig {
    // Задаем начальные значения для URL, пользователя и пароля
    private String jdbcUrl = "jdbc:mysql://localhost:3306/test";
    private String dbUser = "root";
    private String dbPassword = "";

    // Геттеры для получения значений полей

    // Получение JDBC URL
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    // Получение имени пользователя базы данных
    public String getDbUser() {
        return dbUser;
    }

    // Получение пароля пользователя базы данных
    public String getDbPassword() {
        return dbPassword;
    }

    // Сеттеры для установки новых значений полей

    // Установка нового JDBC URL
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    // Установка нового имени пользователя базы данных
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    // Установка нового пароля пользователя базы данных
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}

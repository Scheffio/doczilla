import java.io.*;
import java.util.*;

class FileNode {
    File file;
    List<FileNode> dependencies;

    FileNode(File file) {
        this.file = file;
        this.dependencies = new ArrayList<>();
    }
}

public class FileHandler {

    public static void main(String[] args) {
        // Указываем стартовую директорию
        File current_dir = new File("src");
        // Создаем список для хранения информации о файлах
        List<FileNode> order_to_read = new ArrayList<>();
        // Получаем информацию о файлах
        getContent(current_dir, order_to_read);

        // Получаем отсортированный список файлов
        List<FileNode> sortedOrder = topologicalSort(order_to_read);

        // Проверяем, есть ли циклические зависимости
        if (sortedOrder == null) {
            System.out.println("Произошла циклическая ошибка. Подробности записаны в файл output_error.txt");
            // Записываем информацию о циклических зависимостях в файл
            writeErrorToFile(order_to_read);
        } else {
            // Объединяем содержимое отсортированных файлов в один файл
            concatenateFiles(sortedOrder);
        }
    }

    public static void getContent(File dir, List<FileNode> orderToRead) {
        // Получаем содержимое директории
        File[] content = dir.listFiles();
        // Проходим по содержимому
        for (File elem : content) {
            // Если элемент - директория, рекурсивно вызываем getContent
            if (elem.isDirectory()) {
                getContent(elem, orderToRead);
            } else {
                // Если элемент - текстовый файл, читаем его содержимое
                if (elem.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(elem))) {
                        // Создаем узел для текущего файла или получаем существующий
                        FileNode currentNode = findOrCreateFileNode(orderToRead, elem);

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Проверяем строки на наличие зависимостей
                            if (line.contains("require")) {
                                String dependencyPath = line.split("'")[1];
                                // Формируем путь к зависимому файлу
                                File dependencyFile = new File(elem.getParentFile(), dependencyPath);
                                // Создаем узел для зависимого файла или получаем существующий
                                FileNode dependencyNode = findOrCreateFileNode(orderToRead, dependencyFile);
                                // Добавляем зависимость
                                currentNode.dependencies.add(dependencyNode);
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
    }

    private static FileNode findOrCreateFileNode(List<FileNode> orderToRead, File file) {
        // Поиск или создание узла для файла
        for (FileNode node : orderToRead) {
            if (node.file.equals(file)) {
                return node;
            }
        }

        FileNode newNode = new FileNode(file);
        orderToRead.add(newNode);
        return newNode;
    }

    public static List<FileNode> topologicalSort(List<FileNode> orderToRead) {
        // Список для хранения отсортированного порядка файлов
        List<FileNode> sortedOrder = new ArrayList<>();
        // Множество для отслеживания посещенных узлов
        Set<FileNode> visited = new HashSet<>();
        // Множество для отслеживания узлов в процессе обхода
        Set<FileNode> inProgress = new HashSet<>();

        // Проходим по всем узлам
        for (FileNode node : orderToRead) {
            // Проверяем, не посещен ли узел, и не содержит ли циклических зависимостей
            if (!visited.contains(node) && !topologicalSortDFS(node, visited, inProgress, sortedOrder)) {
                return null; // Цикл обнаружен
            }
        }

        return sortedOrder;
    }

    private static Boolean topologicalSortDFS(FileNode node, Set<FileNode> visited, Set<FileNode> inProgress, List<FileNode> sortedOrder) {
        // Проверка наличия циклических зависимостей
        Object[] result = hasSelfDependency(node);
        if ((Boolean) result[0]) {
            return false;
        }

        // Если узел не посещен, добавляем его в список sortedOrder
        if (!visited.contains(node)) {
            inProgress.add(node);

            visited.add(node);
            inProgress.remove(node);

            // Добавляем узел в начало списка, если у него нет зависимостей, иначе в конец
            if (node.dependencies.isEmpty()) {
                sortedOrder.add(0, node);
            } else {
                sortedOrder.add(node);
            }
        }
        return true;
    }

    private static Object[] hasSelfDependency(FileNode node) {
        // Проверка циклических зависимостей
        String file_name = node.file.getName().replace(".txt", "");

        for (int i = 0; i < node.dependencies.size(); i++) {
            String dependency_name = node.dependencies.get(i).file.getName();
            if (file_name.equals(dependency_name)) {
                return new Object[]{true, node.file.getName(), dependency_name};
            }
        }

        return new Object[]{false};
    }

    private static void writeErrorToFile(List<FileNode> orderToRead) {
        // Запись информации о циклических зависимостях в файл
        for (FileNode node : orderToRead) {
            Object[] result = hasSelfDependency(node);

            if ((Boolean) result[0]) {
                try (PrintWriter writer = new PrintWriter("output_error.txt")) {
                    writer.println("Файл " + result[1] + " имеет циклическую зависимость с " + result[2] + ".txt");
                } catch (IOException e) {
                    System.out.println("Ошибка при записи в файл: " + e.getMessage());
                }
            }
        }
    }

    private static void concatenateFiles(List<FileNode> sortedOrder) {
        // Объединение содержимого файлов в один файл
        try (PrintWriter writer = new PrintWriter("output_success.txt")) {
            for (FileNode node : sortedOrder) {
                if (node.file.getName().endsWith(".txt")) {
                    String fileContent = readFileContent(node.file);
                    writer.println(fileContent);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private static String readFileContent(File file) throws IOException {
        // Чтение содержимого текстового файла
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}

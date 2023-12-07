# doczilla

Инструкция к заданиям:

# Task2

Запустите XAMPP. Включите Apache и MySQL. **Важно, порт у MySQL должен быть равен 3306!**
![image](https://github.com/Scheffio/doczilla/assets/45427871/9a2df947-d499-4e7c-9867-cfae857c34b9)

Откройте PhpMyAdmin нажав на кноку **admin** у MySQL. Создайте таблицу test и импортируйте туда шаблон sql, который лежит в папке Task2 -> Backend.
Должно получиться вот так:

![image](https://github.com/Scheffio/doczilla/assets/45427871/47697fdc-b224-4bf0-9da5-bba7681c2e7f)
![image](https://github.com/Scheffio/doczilla/assets/45427871/909b9016-0713-476f-83a1-9fed23075b7f)


После того, как всё запустили и импортировали, в этом репозитории, в папке Task2, в папке Backend лежит .jar файл с сервером и всем бэкэндом. ***В этой же папке, лежит папка source с исходным кодом бэкэнда***. Скачайте этот файл и запустите. Либо двойным кликом, тогда проверьте что сервер запустился, в диспетчере задач будет Java процесс, либо (так эффективнее) откройте папку, в которой лежит ваш файл server.jar:

![image](https://github.com/Scheffio/doczilla/assets/45427871/8c85160d-4be3-488d-b502-21dd123fd16f)

Щёлкните по строке с путем сверху и напишите туда **cmd**.

![image](https://github.com/Scheffio/doczilla/assets/45427871/cdc2e6be-f14d-4272-8416-b78977b9e9d6)

Появится одна строчка, нажмите на неё. Запустится консоль, нужно будет вписать следующий код:
```
java -jar server.jar
```
Получите вот такое сообщение с успехом запуска:

![image](https://github.com/Scheffio/doczilla/assets/45427871/2c0dc9b0-b276-41da-8fd7-156d4ae87975)

Всё, сервер запущен, можно тестировать. Клиент доступен на [https://scheffio.github.io/doczilla/Task2](https://scheffio.github.io/doczilla/Task2)
Если всё сделали правильно, то увидите это:

![image](https://github.com/Scheffio/doczilla/assets/45427871/0168811a-be16-47e6-8858-235f6c1cb374)

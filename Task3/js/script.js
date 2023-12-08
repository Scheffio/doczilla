// Определение класса ToDoApp
class ToDoApp {
    // Конструктор класса, инициализирующий элементы страницы и переменные
    constructor() {
        this.currentMonthElement = document.querySelector('.currentMonth');
        this.daysContainer = document.querySelector('.days');
        this.prevMonthButton = document.querySelector('.prevMonth');
        this.nextMonthButton = document.querySelector('.nextMonth');
        this.taskList = document.querySelector('.tasks_list');
        this.noTasks = document.querySelector('.no_tasks');
        this.currentStatus = 'any';
        this.currentMonth = new Date().getMonth() - 1;
        this.currentYear = '2021';

        // Привязываем методы к текущему экземпляру класса
        this.renderCalendar = this.renderCalendar.bind(this);
        this.clearCalendar = this.clearCalendar.bind(this);
        this.noFoundTasks = this.noFoundTasks.bind(this);
        this.showAll = this.showAll.bind(this);
        this.handlePrevMonthClick = this.handlePrevMonthClick.bind(this);
        this.handleNextMonthClick = this.handleNextMonthClick.bind(this);
        this.clearList = this.clearList.bind(this);
        this.getTodos = this.getTodos.bind(this);
        this.formatDateTime = this.formatDateTime.bind(this);
        this.renderTask = this.renderTask.bind(this);
        this.expandTask = this.expandTask.bind(this);
        this.getIncompleteTasks = this.getIncompleteTasks.bind(this);
        this.getTodayTasks = this.getTodayTasks.bind(this);
        this.getWeekTasks = this.getWeekTasks.bind(this);
        this.handleSearchInput = this.handleSearchInput.bind(this);

        // Устанавливаем обработчики событий
        this.prevMonthButton.addEventListener('click', this.handlePrevMonthClick);
        this.nextMonthButton.addEventListener('click', this.handleNextMonthClick);
        const searchbar = document.querySelector('.search');
        searchbar.addEventListener('input', this.handleSearchInput);

        // Ожидаем полной загрузки страницы и затем вызываем метод renderCalendar
        window.onload = () => {
            this.renderCalendar();
            this.getTodos();
        };
    }

    // Метод для очистки выбранной даты в календаре
    clearCalendar() {
        document.querySelectorAll('.day').forEach(day => day.classList.remove('selected'));
    }

    // Метод для отрисовки календаря
    renderCalendar() {
        // Получаем первый и последний день текущего месяца
        const firstDayOfMonth = new Date(this.currentYear, this.currentMonth, 1)
        const lastDayOfMonth = new Date(this.currentYear, this.currentMonth + 1, 0)
        // Получаем количество дней в текущем месяце
        const daysInMonth = lastDayOfMonth.getDate()
        // Получаем день недели, с которого начинается текущий месяц
        const firstDayOfWeek = firstDayOfMonth.getDay()

        // Устанавливаем текст заголовка с помощью объекта Intl.DateTimeFormat
        this.currentMonthElement.textContent = new Intl.DateTimeFormat('ru-RU', {
            month: 'long',
            year: 'numeric'
        }).format(firstDayOfMonth)

        // Очищаем контейнер для дней календаря
        this.daysContainer.innerHTML = ''

        // Добавляем пустые ячейки для дней предыдущего месяца
        for (let i = 0; i < firstDayOfWeek; i++) {
            const dayElement = document.createElement('div')
            dayElement.className = 'day'
            this.daysContainer.appendChild(dayElement)
        }

        // Добавляем дни текущего месяца
        for (let i = 1; i <= daysInMonth; i++) {
            const dayElement = document.createElement('div')
            dayElement.className = 'day'
            dayElement.textContent = i

            // Проверяем, является ли текущий день сегодняшним
            const currentDate = new Date()
            if (
                this.currentYear === currentDate.getFullYear() &&
                this.currentMonth === currentDate.getMonth() &&
                i === currentDate.getDate()
            ) {
                dayElement.classList.add('today')
            }

            dayElement.addEventListener('click', () => {
                // Удаляем класс 'selected' у всех дней
                this.clearCalendar()
                
                // Помечаем выбранный день
                dayElement.classList.add('selected');
                // Получаем выбранную дату
                const selectedDate = new Date(this.currentYear, this.currentMonth, i);
                // Форматируем дату в строку "DD.MM.YYYY"
                const formattedDate = `${selectedDate.getDate()}.${selectedDate.getMonth() + 1}.${selectedDate.getFullYear()}`;
                // Выводим отформатированную дату в консоль (вы можете использовать её по-вашему усмотрению)
                this.getTodos(formattedDate)
            });

            this.daysContainer.appendChild(dayElement)
        }
    }

    // Метод для отображения сообщения о отсутствии задач
    noFoundTasks() {
        const tasks = document.querySelectorAll('.task');

        // Проверяем, есть ли хотя бы одна задача
        const noTasksFound = tasks.length === 0;

        if (noTasksFound && !this.noTasks.classList.contains('show')) {
            this.noTasks.classList.add('show');
        } else if (!noTasksFound && this.noTasks.classList.contains('show')) {
            this.noTasks.classList.remove('show');
        }
    }

    // Метод для отображения всех задач
    showAll() {
        this.clearCalendar()
        this.getTodos()
        this.noFoundTasks()
    }

    // Обработчик события для кнопки "Предыдущий месяц"
    handlePrevMonthClick() {
        // Уменьшаем текущий месяц и обновляем год при необходимости
        this.currentMonth = (this.currentMonth - 1 + 12) % 12
        if (this.currentMonth === 11) {
            this.currentYear--
        }
        // Перерисовываем календарь с новыми значениями
        this.renderCalendar()
    }

    // Обработчик события для кнопки "Следующий месяц"
    handleNextMonthClick() {
        // Увеличиваем текущий месяц и обновляем год при необходимости
        this.currentMonth = (this.currentMonth + 1) % 12
        if (this.currentMonth === 0) {
            this.currentYear++
        }
        // Перерисовываем календарь с новыми значениями
        this.renderCalendar()
    }

    // Метод для очистки списка задач
    clearList() {
        this.taskList.innerHTML = ''
    }

    // Метод для получения списка задач
    getTodos(date) {
        const url = "http://localhost:8080/api/todos?url=https://todo.doczilla.pro/api/todos?limit=10"

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Ошибка HTTP! Статус: ${response.status}`)
                }
                return response.json()
            }).then(data => {
                this.clearList()
                let found = false;
                data.forEach(elem => {
                    if (date) {
                        if (date === this.formatDateTime(elem.date).split(' ')[0]) {
                            this.renderTask(elem);
                            found = true
                        }
                    } else {
                        if (this.currentStatus === 'false') {
                            if (!elem.status) {
                                this.renderTask(elem);
                            }
                        } else if (this.currentStatus === 'true') {
                            if (elem.status) {
                                this.renderTask(elem);
                            }
                        } else {
                            this.renderTask(elem);
                        }
                    }

                })
                if (!found) {
                    this.noFoundTasks();
                } else {
                    this.noFoundTasks(); // Вызываем для обработки случая, когда найдена хотя бы одна задача
                }
            }).catch(error => {
                console.error('Ошибка: ', error.message, error.stack);
            })
    }

    // Метод для форматирования даты и времени
    formatDateTime(dateTimeString) {
        const options = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false,
            timeZone: 'UTC'
        };

        const formattedDateTime = new Date(dateTimeString).toLocaleString('ru-RU', options);
        return formattedDateTime.replace(',', '');
    }

    // Метод для отрисовки задачи
    renderTask(task) {
        // <section class="task">
        const element_0 = document.createElement('SECTION')
        element_0.className = 'task'
        // <section class="title">
        const element_1 = document.createElement('SECTION')
        element_1.className = 'title'
        // <div>
        const element_2 = document.createElement('DIV')
        // <h1>
        const element_3 = document.createElement('H1')
        const element_4 = document.createTextNode(task.name)
        element_3.appendChild(element_4)
        element_2.appendChild(element_3)
        // <span>
        const element_5 = document.createElement('SPAN')
        const element_6 = document.createTextNode(task.status ? 'Выполнено' : 'Не выполнено')
        element_5.appendChild(element_6)
        element_2.appendChild(element_5)
        element_1.appendChild(element_2)
        // <p>
        const element_7 = document.createElement('P')
        const element_8 = document.createTextNode(this.formatDateTime(task.date))
        element_7.appendChild(element_8)
        element_1.appendChild(element_7)
        element_0.appendChild(element_1)
        // <section class="text">
        const element_9 = document.createElement('SECTION')
        element_9.className = 'text'
        element_9.id = task.id
        // <p>
        const element_10 = document.createElement('P')
        const element_11 = document.createTextNode(task.fullDesc)
        element_10.appendChild(element_11)
        element_9.appendChild(element_10)
        // <span class="expand" onclick="expandTask()">
        const element_12 = document.createElement('SPAN')
        element_12.className = 'expand'
        element_12.setAttribute('onclick', `todoApp.expandTask('${task.id}')`)
        const element_13 = document.createTextNode('Читать полностью')
        element_12.appendChild(element_13)
        element_9.appendChild(element_12)
        element_0.appendChild(element_9)

        this.taskList.appendChild(element_0)
    }

    // Метод для раскрытия задачи
    expandTask(id) {
        const tasks = document.querySelectorAll('.text')

        tasks.forEach((elem) => {
            if (elem.id === id) {
                const text = elem.childNodes[0]
                const expand_button = elem.childNodes[1]

                if (expand_button.textContent === "Читать полностью") {
                    expand_button.textContent = 'Закрыть'
                    text.classList.add('expanded')
                } else {
                    expand_button.textContent = 'Читать полностью'
                    text.classList.remove('expanded')
                }
            }
        })
    }

    // Метод для получения невыполненных задач
    getIncompleteTasks() {
        if (this.currentStatus === 'true' || this.currentStatus === 'any') {
            this.currentStatus = 'false'
            this.getTodos()
        } else {
            this.currentStatus = 'any'
            this.getTodos()
        }
    }

    // Метод для получения задач на текущую дату
    getTodayTasks() {
        // Получаем текущую дату
        const currentDate = new Date();

        // Форматируем текущую дату в строку "DD.MM.YYYY"
        const formattedDate = currentDate.toLocaleDateString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
        // Выводим отформатированную дату в консоль (вы можете использовать её по-вашему усмотрению)
        this.getTodos(formattedDate)
    }

    // Метод для получения задач на следующую неделю
    getWeekTasks() {
        const currentDate = new Date();
        const endDate = new Date(currentDate);
        endDate.setDate(currentDate.getDate() + 7);

        const formattedStartDate = currentDate.toISOString();
        const formattedEndDate = endDate.toISOString();

        const startDateMillis = new Date(formattedStartDate).getTime();
        const endDateMillis = new Date(formattedEndDate).getTime();

        const url = `http://localhost:8080/api/todos?url=https://todo.doczilla.pro/api/todos/date?from=${startDateMillis}&to=${endDateMillis}`

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Ошибка HTTP! Статус: ${response.status}`)
                }
                return response.json()
            }).then(data => {
                if (data.length > 0) {
                    data.forEach((elem) => {
                        this.renderTask(elem)
                    })
                } else {
                    this.clearList()
                    this.noFoundTasks()
                }
            }).catch(error => {
                console.error('Ошибка: ', error.message, error.stack);
            })
    }

    // Обработчик события для ввода текста в поле поиска
    handleSearchInput(e) {
        const url = `http://localhost:8080/api/todos?url=https://todo.doczilla.pro/api/todos/find?q=${e.target.value}&limit=10`

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Ошибка HTTP! Статус: ${response.status}`)
                }
                return response.json()
            }).then(data => {
                this.clearList()
                data.forEach(elem => {
                    this.renderTask(elem)
                })
            }).catch(error => {
                console.error('Ошибка: ', error.message, error.stack);
            })
    }
}

// Создаем экземпляр класса ToDoApp
const todoApp = new ToDoApp();
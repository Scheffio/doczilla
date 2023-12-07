function getStudents() {
    const apiUrl = `http://localhost:8080/api/students/all`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Ошибка HTTP! Статус: ${response.status}`);
            }
            return response.json();
        }).then(data => {
            data.forEach(elem => {
                renderStudentCard(elem)
            });
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
}

const students_list = document.querySelector('.students')

function renderStudentCard(student) {
    // <section class="card">
    const element_0 = document.createElement('SECTION')
    element_0.className = 'card'
    // <h1>
    const element_1 = document.createElement('H1')
    const element_2 = document.createTextNode(student.surname)
    element_1.appendChild(element_2)
    element_0.appendChild(element_1)
    // <div>
    const element_3 = document.createElement('DIV')
    // <p>
    const element_4 = document.createElement('P')
    const element_5 = document.createTextNode(`${student.name} ${student.patronymic}`)
    element_4.appendChild(element_5)
    element_3.appendChild(element_4)
    // <p>
    const element_6 = document.createElement('P')
    const element_7 = document.createTextNode(student.bd_date)
    element_6.appendChild(element_7)
    element_3.appendChild(element_6)
    element_0.appendChild(element_3)
    // <span>
    const element_8 = document.createElement('SPAN')
    const element_9 = document.createTextNode(student.study_group)
    element_8.appendChild(element_9)
    element_0.appendChild(element_8)
    // <section class="overlay">
    const element_10 = document.createElement('SECTION')
    element_10.className = 'overlay'
    // <p>
    const element_11 = document.createElement('P')
    element_11.setAttribute('onclick', `deleteStudent(${student.id})`)
    const element_12 = document.createTextNode('Удалить')
    element_11.appendChild(element_12)
    element_10.appendChild(element_11)
    element_0.appendChild(element_10)

    students_list.appendChild(element_0)
}

let isShowModal = false

function modalHandler() {
    const modal = document.querySelector('.modal')
    if (isShowModal === false) {
        modal.classList.add('show')
    } else {
        modal.classList.remove('show')
    }
    isShowModal = !isShowModal
}

function deleteStudent(id) {
    fetch('http://localhost:8080/api/students/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(id),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Ошибка HTTP! Статус: ${response.status}`);
            }
            return response.text();
        })
        .then(data => {
            clear_list()
            getStudents()
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
}

function clear_list() {
    students_list.innerHTML = ''
}

document.querySelector('form').addEventListener('submit', function (event) {
    event.preventDefault();

    let name = document.getElementById('name').value;
    let surname = document.getElementById('surname').value;
    let patronymic = document.getElementById('patronymic').value;
    let bd_date = document.getElementById('bd_date').value;
    let study_group = document.getElementById('study_group').value;

    let data = {
        name: name,
        surname: surname,
        patronymic: patronymic,
        bd_date: bd_date,
        study_group: study_group
    };

    fetch('http://localhost:8080/api/students/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось получить ответ.');
            }
            return response.text();
        })
        .then(data => {
            clear_list()
            getStudents()
            modalHandler()
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });
});

window.onload = () => {
    getStudents()
}
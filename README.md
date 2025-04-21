# 📋 Attendance Service

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)
![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)

📌 Attendance Service — это микросервис для учёта рабочего времени сотрудников по MAC-адресу, с поддержкой:
- отметки прихода/ухода через QR-код
- учёта командировок
- генерации табеля учёта рабочего времени
- отображения текущего статуса всех сотрудников

---

## ✨ Возможности

- 🏢 Регистрация компаний и сотрудников
- 📱 Учёт посещаемости по MAC-адресу (через QR или ссылку)
- 🛡️ Привязка MAC к браузеру для защиты от подмены
- ✈️ Отметка командировки с автоматическим начислением 8 часов
- 📅 Генерация табеля (Excel, JSON)
- 👀 Визуальный просмотр присутствующих

---

## 📦 Технологии

| Технология       | Версия       |
|------------------|--------------|
| Java             | 17           |
| Spring Boot      | 3.4.4        |
| PostgreSQL       | 15+          |
| Spring Data JPA  | ✅           |
| Thymeleaf        | ✅           |
| Apache POI       | 5.2.3        |
| Lombok           | 1.18.36      |

---

## 🚀 Сборка и запуск

```bash
git clone https://github.com/PollyBreak/Employees-Work-Time-Log.git
cd Employees-Work-Time-Log

# Сборка
mvn clean install

# Запуск
mvn spring-boot:run
```

📍 После запуска: [http://localhost:8080](http://localhost:8080)

---

## 📚 API и интерфейсы

---

### 👥 Регистрация

| Метод | URL             | Описание               |
|-------|------------------|------------------------|
| POST  | `/api/company`   | Создать компанию       |
| POST  | `/api/employee`  | Добавить сотрудника    |

#### 📥 Пример запроса `/api/company`

```json
{
  "name": "Smart lab"
}
```

#### 📤 Пример ответа

```json
{
  "id": 1,
  "name": "Smart lab"
}
```

---

#### 📥 Пример запроса `/api/employee`

```json
{
  "name": "Alice",
  "surname": "Johnson",
  "macAddress": "AA:BB:CC:11:22:33",
  "position": "Backend Developer",
  "room": "1.1.133",
  "phone": "+77771234567",
  "companyId": 1
}
```

#### 📤 Пример ответа

```json
{
  "id": 2,
  "name": "Alice",
  "surname": "Johnson",
  "position": "Backend Developer",
  "room": "1.1.133",
  "phone": "+77771234567"
}
```

---

### 🕘 Отметка присутствия

| Метод | URL                        | Описание                            |
|-------|----------------------------|-------------------------------------|
| GET   | `/company/{id}/attendance` | HTML-форма для отметки MAC          |
| POST  | `/company/scan`            | Зафиксировать приход или уход       |

#### 🧍 Пример HTML-интерфейса

```html
<form action="/company/scan" method="post">
  <input type="hidden" name="companyId" value="1"/>
  <input type="text" name="macAddress"/>
  <button type="submit">Submit</button>
</form>
```
![image](https://github.com/user-attachments/assets/d664a594-3a9d-4c97-819a-8694784a05b8)


#### 📥 Пример запроса `/company/scan`

```
companyId=1
macAddress=AA:BB:CC:11:22:33
```

#### 📤 Пример ответа (HTML)

- 🟢 `Вы успешно отметились!`
- 🔴 `Вы ушли с работы!`

#### 🖼️ Внешний вид

![image](https://github.com/user-attachments/assets/955ab8b5-c06d-43da-be4d-a251d4f4f727)


---

### ✈️ Командировка

| Метод | URL                                | Описание                         |
|-------|-------------------------------------|----------------------------------|
| GET   | `/company/{id}/business-trip`      | HTML-форма для командировки      |
| POST  | `/company/{id}/business-trip`      | Отметка командировки (раз в день)|

#### 📥 Пример запроса `/company/{id}/business-trip`

```
macAddress=AA:BB:CC:11:22:33
```

#### 📤 Пример ответа (HTML)

- 🟢 `Командировка учтена!`
- 🔴 `Сотрудник не найден`

#### 🖼️ Внешний вид

По бизнес-логике, во время командировки достаточно отметиться 1 раз в день, и тогда за этот день засчитается 8 рабочих часов и в табеле будет указано "К".

![image](https://github.com/user-attachments/assets/72fd498f-1c49-4eb1-b2ad-f74e7a5fd036)


---

### 📅 Табель учёта

| Метод | URL                                                       | Формат     |
|-------|------------------------------------------------------------|------------|
| GET   | `/api/company/{id}/timesheet?year=2025&month=4`           | Excel-файл |
| GET   | `/api/company/{id}/timesheet/json?year=2025&month=4`      | JSON       |

#### 📤 Пример ответа (JSON)

```json
[
  {
    "employeeId": 1,
    "name": "Polina",
    "surname": "Batova",
    "position": "QA",
    "room": "1.1.105",
    "workedHoursPerDay": {
      "2025-04-17": "PT7H58M0S"
    },
    "totalWorked": "PT7H58M0S"
  }
]
```

#### 🖼️ Excel-табель

![image](https://github.com/user-attachments/assets/457b12db-435a-4e4d-baef-7b6376b73553)


---

### 📆 История посещений

| Метод | URL                                              | Описание                          |
|-------|--------------------------------------------------|-----------------------------------|
| GET   | `/api/employee/{id}/attendance?range=week`       | История одного сотрудника         |
| GET   | `/api/company/{id}/attendance?range=month`       | История по всем сотрудникам       |

#### 📥 Пример запроса

```
GET /api/employee/1/attendance?range=week
```

#### 📤 Пример ответа

```json
[
  {
    "employeeId": 1,
    "employeeName": "Polina Batova",
    "atWork": true,
    "timestamp": "2025-04-17T09:00:00"
  },
  {
    "employeeId": 1,
    "employeeName": "Polina Batova",
    "atWork": false,
    "timestamp": "2025-04-17T17:45:00"
  }
]
```

---

### 👀 Статус сотрудников

| Метод | URL                                | Описание                                 |
|-------|------------------------------------|------------------------------------------|
| GET   | `/api/company/{id}/employees/view` | HTML-таблица статуса сотрудников         |

#### 🖼️ Внешний вид

![image](https://github.com/user-attachments/assets/467642cc-975a-4cc0-b77b-6d0ae28e74ef)

---



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
cd attendance-service

# Сборка
mvn clean install

# Запуск
mvn spring-boot:run
```

📍 После запуска: [http://localhost:8080](http://localhost:8080)

---

## 📚 API и интерфейсы

### 👥 Регистрация

| Метод | URL             | Описание               |
|-------|------------------|------------------------|
| POST  | `/api/company`   | Создать компанию       |
| POST  | `/api/employee`  | Добавить сотрудника    |

#### Пример: POST `/api/employee`

```json
{
  "name": "Alice",
  "surname": "Johnson",
  "macAddress": "AA:BB:CC:11:22:33",
  "position": "Backend Developer",
  "room": "1.1.133",
  "phone": "+7 (777) 123-45-67",
  "companyId": 1
}
```

---

### 🕘 Отметка присутствия

| Метод | URL                        | Описание                            |
|-------|----------------------------|-------------------------------------|
| GET   | `/company/{id}/attendance` | HTML-форма для отметки MAC          |
| POST  | `/company/scan`            | Зафиксировать приход или уход       |

📌 MAC сохраняется в `localStorage` и становится нередактируемым.

---

### ✈️ Командировка

| Метод | URL                                | Описание                         |
|-------|-------------------------------------|----------------------------------|
| GET   | `/company/{id}/business-trip`      | HTML-форма для командировки      |
| POST  | `/company/{id}/business-trip`      | Отметка командировки (раз в день)|

---

### 📅 Табель учёта

| Метод | URL                                                       | Формат     |
|-------|------------------------------------------------------------|------------|
| GET   | `/api/company/{id}/timesheet?year=2025&month=4`           | Excel-файл |
| GET   | `/api/company/{id}/timesheet/json?year=2025&month=4`      | JSON       |

---

### 📆 История посещений

| Метод | URL                                              | Описание                          |
|-------|--------------------------------------------------|-----------------------------------|
| GET   | `/api/employee/{id}/attendance?range=week`       | История одного сотрудника         |
| GET   | `/api/company/{id}/attendance?range=month`       | История по всем сотрудникам       |

---

### 👀 Статус сотрудников

| Метод | URL                                | Описание                                 |
|-------|------------------------------------|------------------------------------------|
| GET   | `/api/company/{id}/employees/view` | HTML-таблица статуса сотрудников         |

---

## 🖼️ Скриншоты

> Загрузите картинки в папку `img/` вашего репозитория и замените `PLACEHOLDER` на имя файла.

### 🧍 Отметка прихода/ухода

![attendance-form](img/attendance-form.png) <!-- PLACEHOLDER -->

---

### ✈️ Отметка командировки

![business-trip-form](img/business-trip-form.png) <!-- PLACEHOLDER -->

---

### 📄 Табель Excel

![timesheet-excel](img/timesheet.png) <!-- PLACEHOLDER -->

---

### 👀 Онлайн статус сотрудников

![employee-status-view](img/status-view.png) <!-- PLACEHOLDER -->

---

## 📁 Структура проекта

```
src/
├── controller/      # Контроллеры API и страниц
├── service/         # Бизнес-логика
├── repository/      # Репозитории JPA
├── entity/          # JPA-сущности
├── dto/             # Запросы и ответы
├── templates/       # HTML-страницы (Thymeleaf)
└── resources/       # Настройки, стили и шаблоны
```

---

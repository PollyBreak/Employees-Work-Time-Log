# 📋 Attendance Service

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)
![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)
![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)

📌 **Attendance Service** — это микросервис для учёта рабочего времени сотрудников по MAC-адресу 📱, с поддержкой:
- отметки прихода/ухода через QR
- учёта командировок ✈️
- генерации табеля учёта рабочего времени 📊
- отображения текущего статуса всех сотрудников 👀

---

## ✨ Возможности

- 🏢 Регистрация компаний и сотрудников
- 📱 Отметка о присутствии через MAC-адрес (через QR или ссылку)
- 🛡️ Защита от подделки: привязка MAC к браузеру
- ✈️ Учёт командировок с отметкой "К"
- 📅 Генерация табеля за месяц (Excel и JSON)
- 🔍 Просмотр статуса всех сотрудников
- 📜 Чистый REST API + HTML-интерфейс

---

## 🧩 Стек технологий

| Технология       | Версия       |
|------------------|--------------|
| Java             | 17           |
| Spring Boot      | 3.4.4        |
| Spring Data JPA  | ✅           |
| PostgreSQL       | 15+          |
| Thymeleaf        | ✅           |
| Apache POI       | 5.2.3 (Excel)|
| Lombok           | 1.18.36      |

---

📚 API и интерфейсы
👥 Регистрация

Метод	URL	Описание
POST	/api/company	Создать компанию
POST	/api/employee	Добавить сотрудника
Пример: POST /api/employee
json
Копировать
Редактировать
{
  "name": "Alice",
  "surname": "Johnson",
  "macAddress": "AA:BB:CC:11:22:33",
  "position": "Backend Developer",
  "room": "1.1.133",
  "phone": "+7 (777) 123-45-67",
  "companyId": 1
}
🕘 Отметка присутствия

Метод	URL	Описание
GET	/company/{id}/attendance	HTML-форма для отметки MAC
POST	/company/scan	Зафиксировать приход или уход
📌 MAC сохраняется в localStorage и становится нередактируемым.

✈️ Командировка

Метод	URL	Описание
GET	/company/{id}/business-trip	HTML-форма для командировки
POST	/company/{id}/business-trip	Отметка командировки (раз в день)
📅 Табель учёта

Метод	URL	Формат
GET	/api/company/{id}/timesheet?year=2025&month=4	Excel-файл
GET	/api/company/{id}/timesheet/json?year=2025&month=4	JSON
📆 История посещений

Метод	URL	Описание
GET	/api/employee/{id}/attendance?range=week	История одного сотрудника
GET	/api/company/{id}/attendance?range=month	История по всем сотрудникам
👀 Статус сотрудников

Метод	URL	Описание
GET	/api/company/{id}/employees/view	HTML-таблица статуса сотрудников

🖼️ Скриншоты

🧍 Отметка прихода/ухода


✈️ Отметка командировки


📄 Табель Excel


👀 Онлайн статус сотрудников


📁 Структура проекта
src/
├── controller/      # Контроллеры API и страниц
├── service/         # Бизнес-логика
├── repository/      # Репозитории JPA
├── entity/          # JPA-сущности
├── dto/             # Запросы и ответы
├── templates/       # HTML-страницы (Thymeleaf)
└── resources/       # Настройки, стили и шаблоны

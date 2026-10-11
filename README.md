# Osteria Verde Reservation

Учебное full-stack приложение для онлайн-бронирования столиков в итальянском ресторане. Пользователь выбирает дату, время и количество гостей, а сервер находит подходящий свободный столик и защищает его от пересекающихся броней.

## Возможности

- проверка свободных столиков до отправки контактных данных;
- автоматический выбор столика по вместимости и желаемому расположению;
- бронирование на два часа без пересечения с действующими бронями;
- подтверждение с уникальным номером вида **OV-XXXXXX**;
- поиск брони по номеру;
- отмена бронирования;
- валидация даты, времени, телефона, email и количества гостей;
- адаптивный интерфейс для компьютера и телефона.

## Технологии

- frontend: React 18, TypeScript, Vite, обычный CSS;
- backend: Java 21, Spring Boot, Spring Web, Spring Data JPA;
- база данных: PostgreSQL 17;
- тесты: JUnit 5, Mockito, Vitest.

## Запуск

Понадобятся Java 21, Maven, Node.js с npm, Docker и Docker Compose.

1. Запустите Docker Desktop.
2. В корне проекта поднимите PostgreSQL:

   ~~~bash
   docker compose up -d
   ~~~

3. В отдельном терминале запустите backend:

   ~~~bash
   cd backend
   mvn spring-boot:run
   ~~~

   Сервер будет доступен на **http://localhost:8081**.

4. В другом терминале запустите frontend:

   ~~~bash
   cd frontend
   npm install
   npm run dev
   ~~~

5. Откройте **http://localhost:5173**.

При первом запуске сервер автоматически добавит девять столиков разной вместимости и расположения. Данные PostgreSQL сохраняются в Docker volume **tabletime-data**.

## REST API

| Метод | Адрес | Назначение |
| --- | --- | --- |
| GET | /api/reservations/availability | Проверить наличие столика |
| POST | /api/reservations | Создать бронь |
| GET | /api/reservations/{bookingCode} | Найти бронь по номеру |
| PATCH | /api/reservations/{bookingCode}/cancel | Отменить бронь |

Пример параметров проверки:

~~~text
/api/reservations/availability?date=2030-05-20&time=18:30&guests=4&location=У окна
~~~

## Тесты и сборка

~~~bash
cd backend
mvn test
~~~

~~~bash
cd frontend
npm test
npm run build
~~~

Если Maven не может записывать в пользовательский кэш, можно использовать кэш внутри проекта:

~~~bash
mvn -Dmaven.repo.local=.maven-repo test
~~~

Подробные пояснения для курсовой работы находятся в [COURSEWORK_NOTES.md](COURSEWORK_NOTES.md).

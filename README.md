# Master–Detail — система управления документами

Веб-приложение для работы с документами (**Master**) и их строками спецификации (**Detail**). Позволяет вести документы, автоматически рассчитывать итоговые суммы и контролировать уникальность номеров.

## Возможности

- Создание, просмотр, изменение и удаление документов.
- Создание, просмотр, изменение и удаление строк спецификации выбранного документа.
- Автоматический пересчёт `master.total` при добавлении, изменении и удалении строк.
- Проверка уникальности номера документа при создании и редактировании.
- Дополнительная защита от дублирования через ограничение `UNIQUE` в PostgreSQL.
- Сохранение обнаруженных приложением ошибок дублирования в таблицу `error_log` **в отдельной транзакции** (`Propagation.REQUIRES_NEW`).
- Интерфейс на Angular с формами создания и редактирования.

## Стек и версии

| Компонент | Версия / технология |
|---|---|
| Java | **25** (`pom.xml`) |
| Spring Boot | **4.1.2** (`pom.xml`) |
| Spring Data JPA / Hibernate | Через зависимости Spring Boot |
| PostgreSQL | **18.6** (версия, использованная при разработке) |
| Angular | Версия определяется файлом `frontend/package.json` |
| TypeScript | Версия определяется файлом `frontend/package.json` |
| Node.js / npm | Совместимые с установленной версией Angular; проверить `node -v`, `npm -v` |
| Maven | Используется Maven Wrapper (`backend/mvnw.cmd`) |
| ОС для приведённых команд | Windows / PowerShell |

## Структура проекта

```text
masterdetail/
├── backend/
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/masterdetail/
│       │   │   ├── controller/    # REST-контроллеры Master и Detail
│       │   │   ├── dto/           # Объекты запросов и ответов
│       │   │   ├── entity/        # Master, Detail, ErrorLog
│       │   │   ├── repository/    # Spring Data JPA
│       │   │   └── service/       # Бизнес-логика и транзакции
│       │   └── resources/         # Конфигурация Spring Boot
│       └── test/
├── frontend/
│   ├── package.json
│   ├── angular.json
│   └── src/app/                # Компоненты, модели, HTTP-сервисы
├── .gitignore
└── README.md
```

## Требования для локального запуска

1. **JDK 25**.
2. **PostgreSQL 18.6** (для воспроизведения исходного окружения) и утилита `psql`.
3. **Node.js и npm**, совместимые с версией Angular из `frontend/package.json`.


## 1. Настройка PostgreSQL

Убедитесь, что сервер PostgreSQL запущен. Откройте `psql` под пользователем, имеющим право создавать базы данных:

```powershell
psql -U postgres
```

Создайте базу данных:

```sql
CREATE DATABASE master_detail_db;
\c master_detail_db
```

Создайте таблицы в указанном порядке:

```sql
CREATE TABLE master (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    number INTEGER NOT NULL UNIQUE,
    date DATE NOT NULL,
    total NUMERIC(15,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    note TEXT
);

CREATE TABLE detail (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_det NUMERIC(15,2) NOT NULL CHECK (total_det >= 0),
    document_id BIGINT NOT NULL REFERENCES master(id) ON DELETE CASCADE
);

CREATE TABLE error_log (
    id BIGSERIAL PRIMARY KEY,
    error_type VARCHAR(100) NOT NULL,
    error_message TEXT NOT NULL,
    document_number INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

> Это SQL-скрипт для **новой пустой базы**. Он воспроизводит известную структуру `master` и `error_log`; для `detail` приведена согласованная с Java-моделью схема. Если таблицы уже существуют, **не запускайте `CREATE TABLE` повторно**. Проверьте реальную схему через `\d master`, `\d detail`, `\d error_log`. Для переноса существующей БД лучше использовать проверенный SQL-дамп или миграции.

### Проверка таблиц

```sql
\dt
\d master
\d detail
\d error_log
```

Связь: один документ `master` может содержать несколько записей `detail`. Поле `detail.document_id` ссылается на `master.id`; при удалении документа связанные строки удаляются по `ON DELETE CASCADE`.

## 2. Настройка backend

Создайте локальный файл `backend/src/main/resources/application.properties` (если его нет) и укажите свои данные подключения:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/master_detail_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_LOCAL_PASSWORD

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
server.port=8080
```

Замените `YOUR_LOCAL_PASSWORD` паролем своего пользователя PostgreSQL. Параметр `ddl-auto=validate` проверяет соответствие таблиц JPA-моделям, но не создаёт их. Если Hibernate сообщает о несовпадении схемы, сверьте таблицы с фактическими Entity и конфигурацией проекта.


### Запуск Spring Boot

Из корня проекта:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend по умолчанию доступен по адресу `http://localhost:8080`.

> Если используется уже настроенный `application.properties`, сохраните его рабочие настройки; приведённый выше блок — пример конфигурации, а не требование переписать существующий файл.

## 3. Настройка и запуск frontend

Откройте **второй терминал** в корне проекта:

```powershell
cd frontend
npm install
npx ng serve
```

Откройте в браузере **http://localhost:4200**.

Frontend обращается к REST API по адресу `http://localhost:8080/api/masters`. Для локального запуска должны одновременно работать PostgreSQL, backend и frontend. Если измените порт backend, обновите адрес API в Angular HTTP-сервисах и настройки CORS в Spring Boot.

## REST API

### Документы (Master)

| Метод | URL | Назначение |
|---|---|---|
| `GET` | `/api/masters` | Список документов |
| `GET` | `/api/masters/{id}` | Документ по ID |
| `POST` | `/api/masters` | Создание документа |
| `PUT` | `/api/masters/{id}` | Редактирование документа |
| `DELETE` | `/api/masters/{id}` | Удаление документа |

Пример тела запроса `POST /api/masters`:

```json
{
  "number": 101,
  "date": "2026-11-10",
  "note": "Тестовый документ"
}
```

### Строки спецификации (Detail)

| Метод | URL | Назначение |
|---|---|---|
| `GET` | `/api/masters/{masterId}/details` | Строки документа |
| `POST` | `/api/masters/{masterId}/details` | Добавление строки |
| `PUT` | `/api/masters/{masterId}/details/{detailId}` | Изменение строки |
| `DELETE` | `/api/masters/{masterId}/details/{detailId}` | Удаление строки |

Пример тела запроса для создания строки:

```json
{
  "name": "Товар A",
  "totalDet": 250.00
}
```

Поле `total` документа рассчитывается на backend и не передаётся в `MasterRequest`.

## Логика и транзакции

- В `MasterService` проверяется, существует ли документ с заданным номером.
- Если дубликат найден, приложение записывает ошибку в `error_log` через `ErrorLogService`.
- Метод логирования использует `@Transactional(propagation = Propagation.REQUIRES_NEW)`: запись журнала сохраняется в отдельной транзакции и не откатывается вместе с основной операцией.
- Пользователю возвращается **HTTP 409 Conflict** при дубликате, обнаруженном предварительной проверкой.
- Уникальность номера дополнительно защищена ограничением `UNIQUE` в PostgreSQL.

**Известное ограничение:** при одновременных запросах предварительная проверка может не обнаружить конфликт. PostgreSQL всё равно не позволит создать два документа с одинаковым номером, но возникшее на уровне БД исключение в текущей версии отдельно не логируется и может вернуть не HTTP 409, а серверную ошибку.

## Ручная проверка функциональности

1. Создайте документ с уникальным номером.
2. Измените его номер, дату и примечание.
3. Добавьте две строки с суммами `100` и `250`: итог документа должен быть `350`.
4. Измените сумму первой строки на `200`: итог должен стать `450`.
5. Удалите строку с суммой `250`: итог должен стать `200`.
6. Попробуйте создать документ с уже существующим номером: приложение должно сообщить об ошибке.
7. Проверьте, что в `error_log` появилась запись.
8. Удалите документ и проверьте удаление связанных строк.

SQL-запросы для проверки:

```sql
SELECT * FROM master ORDER BY id;
SELECT * FROM detail ORDER BY id;
SELECT * FROM error_log ORDER BY created_at DESC, id DESC;
```

## Статус проекта

Основные CRUD-операции, пересчёт сумм, проверка уникальности и запись обнаруженных дубликатов в отдельной транзакции проверены вручную.

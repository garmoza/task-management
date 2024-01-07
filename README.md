# Task Management System

## Run

```bash
cp ./build/libs/task-management-0.0.1-SNAPSHOT.jar ./docker
cd ./docker
docker-compose down
docker rmi docker-spring-boot-postgres:latest
docker-compose up
```

Swagger UI
http://localhost:8080/swagger-ui/index.html

Аутентификация `/auth`
```
admin@mail.com:admin
user@mail.com:user
```

|            | GET /users | POST /users | PUT /users | GET /users/{id} | DELETE /users/{id} | PATCH /users/{id}                     |
|------------|------------|-------------|------------|-----------------|--------------------|---------------------------------------|
| ROLE_ADMIN | All        | All         | All        | All             | All                | All (email, rawPassword, authorities) |
| ROLE_USER  | All        | -           | -          | All             | -                  | Current user only (email,rawPassword) |


|            | GET /tasks | POST /tasks | PUT /tasks | GET /tasks/{id} | DELETE /tasks/{id} | PATCH /tasks/{id}                                                                                                  |
|------------|------------|-------------|------------|-----------------|--------------------|--------------------------------------------------------------------------------------------------------------------|
| ROLE_ADMIN | All        | All         | All        | All             | All                | All (title, description, status, priority, authorId, performerId)                                                  |
| ROLE_USER  | All        | All         | -          | All             | -                  | Current user is author (title, description, status, priority, performerId)<br/> Current user is performer (status) |


|            | POST /users                 | GET /users/{id} | DELETE /users/{id} |
|------------|-----------------------------|-----------------|--------------------|
| ROLE_ADMIN | All (author - current user) | All             | All                |
| ROLE_USER  | All (author - current user) | All             | -                  |

## Task

- Сервис должен поддерживать аутентификацию и авторизацию пользователей по email и паролю.
- Доступ к API должен быть аутентифицирован с помощью JWT токена.

Запрос JWT токена по адресу `/auth` - требуется отправить `email` и `password`.
В ответ приходит токен для аутентификации с доступными для пользователя ролями внутри.
__Все другие ручки требуют__ `Bearer Token`.

Доступны две роли:
`ROLE_USER` - обычный пользователь по задаче,
`ROLE_ADMIN` - пользователь с привилегиями.

- Пользователи могут управлять своими задачами: создавать новые,
  редактировать существующие, просматривать и удалять, менять статус и
  назначать исполнителей задачи.

PATCH `/tasks/{id}`

- Пользователи могут просматривать задачи других пользователей,
  а исполнители задачи могут менять статус своих задач.

GET `/tasks`, PATCH `/tasks/{id}`

- К задачам можно оставлять комментарии.

POST `/comments`

- API должно позволять получать задачи конкретного автора или исполнителя,
  а также все комментарии к ним. Необходимо обеспечить фильтрацию и пагинацию вывода.

GET `/tasks?page=0&size=20`, GET `/tasks?authorId=1&performerId=2&page=0&size=20`

- Сервис должен корректно обрабатывать ошибки и возвращать понятные сообщения,
  а также валидировать входящие данные.

```json
{
  "code": "400 BAD_REQUEST",
  "description": "не должно равняться null",
  "message": "Invalid Data",
  "authorId": "не должно равняться null",
  "priority": "не должно равняться null",
  "title": "не должно быть пустым",
  "status": "не должно равняться null"
}
```

```json
{
  "code": "404 NOT_FOUND",
  "message": "Task with id=%d not found"
}
```

- Сервис должен быть хорошо задокументирован. API должен быть описан
  с помощью Open API и Swagger. В сервисе должен быть настроен Swagger UI.
  Необходимо написать README с инструкциями для локального запуска проекта.
  Дев среду нужно поднимать с помощью docker compose.

- Напишите несколько базовых тестов для проверки основных функций вашей системы.

Запуск PostgreSQL для локальной разработки
```bash
docker compose -f .\docker-compose-postgresql.yaml up -d
```

<img src="ER.drawio.png" alt="" />
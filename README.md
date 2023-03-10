## Explore With Me
***
#### Основной функционал

Pull request: https://github.com/SZFO/java-explore-with-me/pull/2
***
#### Дополнительный функционал (реакции)
Pull request: https://github.com/SZFO/java-explore-with-me/pull/3
***
### Описание
Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком планировании поиск информации и переговоры. Какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться. Приложение предоставляет возможность делиться информацией об интересных событиях и помогать найти компанию для участия в них.

### Используемые технологии
Java 17, Spring Boot, Maven, Lombok, REST API, PostgreSql, JPA, Hibernate, Jackson, Docker.

### Архитектура
Приложение разбито на 2 микросервиса:
* Основной сервис - ewm-main
* Сервис для сбора статистики по посещениям - ewm-stats

Считается, что все запросы приходят от авторизованных пользователей (за авторизацию отвечает сторонний микросервис). Для тел запросов и ответов используется формат JSON.

### Функционал
* Сервис пользователей: создание, изменение и получение пользователей;
* Сервис событий: базовые CRUD-операции + расширенный функционал с подключением к БД, включая получение настраиваемых отфильтрованных списков, модерирование событий, возможность лайкать/дизлайкать событие с формированием рейтинга мероприятий и рейтинга их авторов, сортировка событий в зависимости от рейтинга. 
* Сервис запросов: подача заявок на участие в событиях, отмена заявок и получение информации о своих заявках;
* Сервис категорий: базовые CRUD-операции;
* Сервис подборок: возможность добавлять несколько событий в одну подборку с возможностью добавлять или удалять события в любой момент и закреплять (или откреплять) разные подборки;
* Сервис статистики: отдельный сервис со своей БД для сохранения и получения статистики просмотров событий.

### Сборка и запуск

Сборка проекта:

>mvn clean install

Развертывание проекта:

> docker-compose up

Остановка проекта:

> docker-compose stop

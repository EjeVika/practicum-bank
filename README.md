# Practicum Bank

Проект выполнен в рамках курса "Микросервисы на Java"

## Основные архитектурные решения

### Стек

- **Язык**: Java 21
- **Сборка**: Apache Maven (Maven Wrapper)
- **Базы данных**: PostgreSQL 17.5
- **Сервер авторизации**: Keycloak 24.0.5
- **Service Discovery**: Apache Zookeeper 3.8.8

### Сервисы (каждый в отдельном докер контейнере)

- `bank-ui` - фронтенд (взят из шаблона)
- `keycloak` - сервер авторизации
- `bank-gateway` - реализация паттерна API Gateway
- `bank-config-server` - сервер конфигураций реализованный с помощью Spring Cloud Config
- `zookeeper` - используется в качестве Service Discovery
- `accounts-service` - управление счетами и данными пользователей
- `accounts-db` - база данных Постгрес для сервиса accounts-service
- `cash-service` - операции с наличными (снятие/пополнение)
- `transfer-service` - перевод денег между счетами
- `notifications-service`- сервис уведомлений

## Сборка и запуск

1. Клонируем репозиторий и переходим в ветку `develop`:
  ```bash
  git clone https://github.com/EjeVika/practicum-bank.git
  cd practicum-bank
  git checkout develop
  ```
2. Запускаем сборку локально
  ```bash
  ./mvnw clean install
  ```  
3. Создаем файл с переменными окружения
  ```bash
  cp .env.example .env
  ``` 
4. Запускаем Keycloak для настройки
  ```bash
  sudo docker compose up --no-deps -d keycloak
  ```  
5. Настраиваем Keycloak http://localhost:8080 (admin/admin)
    - Идем в пространство bank-realm
    - Создаем пользователя Users -> Create new user
	    - задаем Username (логин) и сохраняем
	    - задаем пароль на вкладке credentials
	    - на вкладке Role mapping назначаем роли из предложенных:
		    - ACCOUNTS_ACCESS
		    - CASH_ACCESS
		    - TRANSFER_ACCESS
		    - USER
    - Для сервисных клиентов генерируем секреты в Clients
	    - выбираем по очереди accounts-service, cash-service, notification-service, генерируем секреты и вставляем в файл .env в переменные ACCOUNTS_SERVICE_CLIENT_SECRET, CASH_SERVICE_CLIENT_SECRET, TRANSFER_SERVICE_CLIENT_SECRET соответсвенно 
6. Добавляем строчку в /etc/hosts
  Нужно, чтобы и UI, и сервисы в докере могли обращаться к keycloak по одному имени: keycloak
    ```bash
    echo "127.0.0.1 keycloak" | sudo tee -a /etc/hosts
    ``` 
7. Запускаем приложение 
    ```bash
    sudo docker compose up
    ```
8. UI доступен по адресу http://localhost:8081

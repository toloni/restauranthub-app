# RestaurantHub App

## Overview

**RestaurantHub App is a backend application** — a shared restaurant management system that allows restaurant owners to
manage their establishments
while customers can browse restaurants and their menus.

### Features

- **User Type Management** — structure to distinguish between `Restaurant Owner`, `Customer` and `Admin`, with full CRUD
- **User Management** — user registration and management associated with a user type
- **Restaurant Management** — full CRUD with ownership validation and user type enforcement
- **Menu Item Management** — full CRUD for menu items linked to a restaurant

### Business Rules

- Only users with the `RESTAURANT_OWNER` role can create restaurants
- Only the restaurant owner can update or delete their restaurant
- Only the restaurant owner can manage menu items of their restaurant
- Users with the `ADMIN` role have full access to all resources regardless of ownership
- User types in use cannot be deleted
- Users associated with a restaurant cannot be deleted
- A restaurant and all its associated MenuItem instances are deleted together

## Technologies

| Technology        | Version |
|:------------------|:--------|
| Java              | 25      |
| Spring Boot       | 4.1.0   |
| MySQL             | 8.0     |
| Docker            | latest  |
| Docker Compose    | latest  |
| JUnit 5           | 5.11    |
| Mockito           | 5.x     |
| Swagger / OpenAPI | 3.0     |

## Package Structure

```
src/main/java/br/com/postechfiap/toloni/restauranthub
│
├── domain                                 # Enterprise business rules
│   ├── shared
│   │   └── exception
│   │       ├── AlreadyExistsException     # Resource already exists (409)
│   │       ├── NotFoundException          # Resource not found (404)
│   │       ├── DomainException            # Base domain exception (422)
│   │       ├── EntityInUseException       # Resource in use (409)
│   │       └── UnauthorizedException      # Access denied (403)
│   │
│   ├── usertype                           # UserType aggregate
│   │   ├── valueobject
│   │   │   ├── UserTypeDescription
│   │   │   ├── UserTypeName
│   │   │   └── UserTypeId
│   │   ├── UserRole                       # Enum: RESTAURANT_OWNER, CUSTOMER, ADMIN
│   │   └── UserType                       # UserType entity
│   │
│   ├── user                               # User aggregate
│   │   ├── valueobject
│   │   │   ├── UserId
│   │   │   ├── UserName
│   │   │   ├── UserEmail
│   │   │   └── UserPassword
│   │   ├── User                           # User entity
│   │   └── UserWithTypeName               # Read model enriched with UserType name
│   │
│   ├── restaurant                         # Restaurant aggregate
│   │   ├── valueobject
│   │   │   ├── RestaurantId
│   │   │   ├── RestaurantName
│   │   │   ├── RestaurantAddress
│   │   │   ├── RestaurantCuisineType
│   │   │   └── RestaurantOpeningHours
│   │   ├── Restaurant                     # Restaurant entity
│   │   └── RestaurantWithOwnerName        # Read model enriched with owner name
│   │
│   └── menuitem                           # MenuItem aggregate
│       ├── valueobject
│       │   ├── MenuItemId
│       │   ├── MenuItemName
│       │   ├── MenuItemDescription
│       │   ├── MenuItemPrice
│       │   └── MenuItemImagePath
│       ├── MenuItem                       # MenuItem entity
│       └── MenuItemWithRestaurantName     # Read model enriched with Restaurant name
│
├── application                            # Application business rules
│   ├── authorization
│   │   └── AuthorizationService           # Role-based access control
│   ├── gateways                           # Persistence contracts (ports)
│   │   ├── UserTypeGateway
│   │   ├── UserGateway
│   │   ├── RestaurantGateway
│   │   └── MenuItemGateway
│   ├── pagination
│   │   ├── Page                           # Paginated result set
│   │   ├── PageRequest                    # Pagination parameters
│   │   ├── PageFilter                     # Filter parameters
│   │   ├── PageSort                       # Sort parameters
│   │   └── SortDirection                  # ASC / DESC enum
│   └── usecases
│       ├── usertype
│       │   ├── CreateUserTypeUseCase
│       │   ├── UpdateUserTypeUseCase
│       │   ├── DeleteUserTypeUseCase
│       │   ├── FindUserTypeByIdUseCase
│       │   └── FindAllUserTypesUseCase
│       ├── user
│       │   ├── CreateUserUseCase
│       │   ├── UpdateUserUseCase
│       │   ├── DeleteUserUseCase
│       │   ├── FindUserByIdUseCase
│       │   └── FindAllUsersUseCase
│       ├── restaurant
│       │   ├── CreateRestaurantUseCase
│       │   ├── UpdateRestaurantUseCase
│       │   ├── DeleteRestaurantUseCase
│       │   ├── FindRestaurantByIdUseCase
│       │   ├── FindAllRestaurantsUseCase
│       │   └── TransferRestaurantOwnershipUseCase
│       └── menuitem
│           ├── CreateMenuItemUseCase
│           ├── UpdateMenuItemUseCase
│           ├── DeleteMenuItemUseCase
│           ├── FindMenuItemByIdUseCase
│           └── FindAllMenuItemsUseCase
│
├── adapters                               # Interface adapters
│   ├── controllers
│   │   ├── UserTypeController
│   │   ├── UserController
│   │   ├── RestaurantController
│   │   └── MenuItemController
│   └── presenters
│       ├── usertype
│       │   ├── UserTypePresenter
│       │   └── UserTypeViewModel
│       ├── user
│       │   ├── UserPresenter
│       │   └── UserViewModel
│       ├── restaurant
│       │   ├── RestaurantPresenter
│       │   ├── RestaurantViewModel
│       │   └── TransferOwnershipViewModel
│       └── menuitem
│           ├── MenuItemPresenter
│           └── MenuItemViewModel
│
└── infrastructure                         # Frameworks and drivers
    ├── config                             # Spring Bean configuration
    │   ├── UserTypeConfig
    │   ├── UserConfig
    │   ├── OpenApiConfig
    │   ├── SharedConfig
    │   ├── RestaurantConfig
    │   └── MenuItemConfig
    ├── persistence
    │   ├── entities
    │   │   ├── UserTypeJpaEntity
    │   │   ├── UserJpaEntity
    │   │   ├── RestaurantJpaEntity
    │   │   └── MenuItemJpaEntity
    │   ├── repositories
    │   │   ├── UserTypeJpaRepository
    │   │   ├── UserJpaRepository
    │   │   ├── RestaurantJpaRepository
    │   │   └── MenuItemJpaRepository
    │   ├── gateways                       # Gateway implementations (adapters)
    │   │   ├── UserTypeJpaGateway
    │   │   ├── UserJpaGateway
    │   │   ├── RestaurantJpaGateway
    │   │   └── MenuItemJpaGateway
    │   └── shared
    │       ├── JpaSpecificationBuilder    # Dynamic filters via JPA Specification
    │       └── PageRequestMapper          # Domain PageRequest → Spring Pageable
    └── web
        ├── filter
        │   └── HttpLoggingFilter          # Request/response logging
        ├── exception
        │   └── GlobalExceptionHandler     # Centralized error handling
        └── rest
            ├── usertype
            │   ├── UserTypeApi            # Swagger contract
            │   ├── UserTypeRestController
            │   ├── UserTypeRequest
            │   └── UserTypeResponse
            ├── user
            │   ├── UserApi
            │   ├── UserRestController
            │   ├── UserRequest
            │   └── UserResponse
            ├── restaurant
            │   ├── RestaurantApi
            │   ├── RestaurantRestController
            │   ├── RestaurantRequest
            │   ├── RestaurantResponse
            │   ├── RestaurantTransferOwnershipRequest
            │   └── RestaurantTransferOwnershipResponse
            └── menuitem
                ├── MenuItemApi
                ├── MenuItemRestController
                ├── MenuItemRequest
                └── MenuItemResponse
```

## Domains

### UserType

Defines the classification of a user within the system.

**Fields**

| Field         | Type       | Description                        |
|---------------|------------|------------------------------------|
| `id`          | `UUID`     | Unique identifier                  |
| `name`        | `String`   | Name of the user type              |
| `description` | `String`   | Brief description of the user type |
| `role`        | `UserRole` | Role associated with the user type |

**Roles**

| Role               | Description                                              |
|--------------------|----------------------------------------------------------|
| `RESTAURANT_OWNER` | Can create and manage restaurants and their menu items   |
| `CUSTOMER`         | Can browse restaurants and their menus                   |
| `ADMIN`            | Has full access to all resources regardless of ownership |

**Business Rules**

- `name` and `description` must not be null or blank
- `role` must not be null and must be a valid `UserRole`
- Each `role` must be unique across all user types
- A `UserType` associated with any `User` cannot be deleted

---

### User

Represents a user in the system, associated with a `UserType`.

**Fields**

| Field        | Type     | Description                            |
|--------------|----------|----------------------------------------|
| `id`         | `UUID`   | Unique identifier                      |
| `name`       | `String` | Full name of the user                  |
| `email`      | `String` | Email address, used for identification |
| `password`   | `String` | Password, minimum 8 characters         |
| `userTypeId` | `UUID`   | Reference to the associated `UserType` |

**Business Rules**

- `name`, `email` and `password` must not be null or blank
- `email` must be a valid email format and is normalized to lowercase
- `password` must be at least 8 characters long
- `email` must be unique across all users
- `userTypeId` must reference an existing `UserType`
- A `User` associated with any `Restaurant` cannot be deleted

---

### Restaurant

Represents a restaurant managed by a `Restaurant Owner` user.

**Fields**

| Field          | Type     | Description                        |
|----------------|----------|------------------------------------|
| `id`           | `UUID`   | Unique identifier                  |
| `name`         | `String` | Name of the restaurant             |
| `address`      | `String` | Physical address of the restaurant |
| `cuisineType`  | `String` | Type of cuisine served             |
| `openingHours` | `String` | Operating hours of the restaurant  |
| `ownerId`      | `UUID`   | Reference to the owner `User`      |

**Business Rules**

- All fields must not be null or blank
- `name` must be unique across all restaurants
- `ownerId` must reference an existing `User` with the `RESTAURANT_OWNER` role
- Only the owner or an `ADMIN` can update or delete the restaurant
- A `Restaurant` and all its associated `MenuItem` instances are deleted together
- Only an `ADMIN` can transfer restaurant ownership to a new owner
- The new owner must have the `RESTAURANT_OWNER` role

**Owner Validation**

The `ownerId` is always provided via the `X-User-Id` request header — never in the request body. This ensures the
ownership is tied to the authenticated user making the request.

**Ownership Transfer**

Restaurant ownership can be transferred by an `ADMIN` via a dedicated endpoint. The new owner must be an existing user
with the `RESTAURANT_OWNER` role. The transfer is performed by providing the new owner identifier in the request body
and the admin identifier via the `X-User-Id` header.

---

### MenuItem

Represents an item available in a restaurant's menu.

**Fields**

| Field          | Type         | Description                                    |
|----------------|--------------|------------------------------------------------|
| `id`           | `UUID`       | Unique identifier                              |
| `name`         | `String`     | Name of the menu item                          |
| `description`  | `String`     | Description of the menu item                   |
| `price`        | `BigDecimal` | Price of the menu item                         |
| `currency`     | `String`     | Currency code (e.g. `BRL`, `USD`)              |
| `dineInOnly`   | `Boolean`    | Whether the item is available for dine-in only |
| `imagePath`    | `String`     | Path to the item image (optional)              |
| `restaurantId` | `UUID`       | Reference to the owning `Restaurant`           |

**Business Rules**

- `name`, `description`, `price` and `currency` must not be null or blank
- `price` must not be negative
- `currency` must be a valid ISO 4217 currency code
- `price` is automatically scaled to the correct number of decimal places for the given currency
- `name` must be unique within the same restaurant (the same name can exist in different restaurants)
- `imagePath` is optional — a `MenuItem` can exist without an image
- `restaurantId` is immutable — a `MenuItem` cannot be moved to a different restaurant
- Only the restaurant owner or an `ADMIN` can create, update or delete menu items

**Owner Validation**

The `ownerId` is always provided via the `X-User-Id` request header — never in the request body. This ensures the
ownership is tied to the authenticated user making the request.

## API Endpoints

Base URL: `http://localhost:8080/api/v1`

> All endpoints that require ownership validation expect the authenticated user identifier via the `X-User-Id` header.

---

### UserType

| Method   | Route              | Description                     | Headers | Response Codes             |
|----------|--------------------|---------------------------------|---------|----------------------------|
| `POST`   | `/user-types`      | Create a new user type          | —       | `201`, `409`, `422`        |
| `PATCH`  | `/user-types/{id}` | Update an existing user type    | —       | `200`, `404`, `409`, `422` |
| `DELETE` | `/user-types/{id}` | Delete a user type              | —       | `204`, `409`               |
| `GET`    | `/user-types/{id}` | Find a user type by id          | —       | `200`, `404`               |
| `GET`    | `/user-types`      | List all user types (paginated) | —       | `200`                      |

---

### User

| Method   | Route         | Description                | Headers | Response Codes             |
|----------|---------------|----------------------------|---------|----------------------------|
| `POST`   | `/users`      | Create a new user          | —       | `201`, `404`, `409`, `422` |
| `PATCH`  | `/users/{id}` | Update an existing user    | —       | `200`, `404`, `409`, `422` |
| `DELETE` | `/users/{id}` | Delete a user              | —       | `204`, `409`               |
| `GET`    | `/users/{id}` | Find a user by id          | —       | `200`, `404`               |
| `GET`    | `/users`      | List all users (paginated) | —       | `200`                      |

---

### Restaurant

| Method   | Route                                  | Description                                | Headers     | Response Codes                    |
|----------|----------------------------------------|--------------------------------------------|-------------|-----------------------------------|
| `POST`   | `/restaurants`                         | Create a new restaurant                    | `X-User-Id` | `201`, `403`, `404`, `409`, `422` |
| `PATCH`  | `/restaurants/{id}`                    | Update an existing restaurant              | `X-User-Id` | `200`, `403`, `404`, `409`, `422` |
| `PATCH`  | `/restaurants/{id}/transfer-ownership` | Update restaurant ownership                | `X-User-Id` | `200`, `403`, `404`, `409`, `422` |
| `DELETE` | `/restaurants/{id}`                    | Delete a restaurant and all its menu items | `X-User-Id` | `204`, `403`, `404`               |
| `GET`    | `/restaurants/{id}`                    | Find a restaurant by id                    | —           | `200`, `404`                      |
| `GET`    | `/restaurants`                         | List all restaurants (paginated)           | —           | `200`                             |

---

### MenuItem

| Method   | Route              | Description                     | Headers     | Response Codes                    |
|----------|--------------------|---------------------------------|-------------|-----------------------------------|
| `POST`   | `/menu-items`      | Create a new menu item          | `X-User-Id` | `201`, `403`, `404`, `409`, `422` |
| `PUT`    | `/menu-items/{id}` | Update an existing menu item    | `X-User-Id` | `200`, `403`, `404`, `409`, `422` |
| `DELETE` | `/menu-items/{id}` | Delete a menu item              | `X-User-Id` | `204`, `403`, `404`               |
| `GET`    | `/menu-items/{id}` | Find a menu item by id          | —           | `200`, `404`                      |
| `GET`    | `/menu-items`      | List all menu items (paginated) | —           | `200`                             |

---

### Pagination Query Parameters

All `GET` list endpoints support the following query parameters:

| Parameter     | Type      | Default | Description                         |
|---------------|-----------|---------|-------------------------------------|
| `page`        | `integer` | `0`     | Page number (zero-based)            |
| `size`        | `integer` | `10`    | Number of elements per page         |
| `sort`        | `string`  | —       | Field name to sort by (e.g. `name`) |
| `direction`   | `string`  | `ASC`   | Sort direction (`ASC` or `DESC`)    |
| `filter`      | `string`  | —       | Field name to filter by             |
| `filterValue` | `string`  | —       | Value to filter with                |

---

### Response Codes

| Code  | Description                                           |
|-------|-------------------------------------------------------|
| `200` | OK — request succeeded                                |
| `201` | Created — resource created successfully               |
| `204` | No Content — resource deleted successfully            |
| `400` | Bad Request — malformed request or invalid field type |
| `403` | Forbidden — requester does not have permission        |
| `404` | Not Found — resource not found                        |
| `405` | Method Not Allowed — HTTP method not supported        |
| `409` | Conflict — resource already exists or is in use       |
| `422` | Unprocessable Entity — domain rule violated           |
| `500` | Internal Server Error — unexpected error              |

---

## Setup and Execution

### Prerequisites

- Java 25
- Maven 3.9+
- Docker and Docker Compose
- MySQL 8.0 (if running locally without Docker)

---

### Environment Variables

| Variable      | Description       | Default            |
|---------------|-------------------|--------------------|
| `DB_HOST`     | Database host     | `localhost`        |
| `DB_PORT`     | Database port     | `3306`             |
| `DB_NAME`     | Database name     | `restauranthub_db` |
| `DB_USERNAME` | Database username | `root`             |
| `DB_PASSWORD` | Database password | `root`             |
| `SERVER_PORT` | Application port  | `8080`             |

---

### Running with Docker Compose

```bash
# Clone the repository
git clone https://github.com/toloni/restauranthub-app.git
cd restauranthub

# Build and start all services
docker compose up --build

# Stop all services
docker compose down
```

The application will be available at `http://localhost:8080`.

---

### Running Locally without Docker

```bash
# Start MySQL locally and create the database
mysql -u root -p -e "CREATE DATABASE restauranthub_db;"

# Set environment variables
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=restauranthub_db
export DB_USERNAME=root
export DB_PASSWORD=root

# Build and run the application
./mvnw spring-boot:run
```

---

## Docker File

```
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Docker Compose

The `docker-compose.yml` configures two services:

**`db`** — MySQL 8.0 database

- Exposes port `3306`
- Creates the `restauranthub_db` database on startup
- Data is persisted via a named volume

**`app`** — RestaurantHub App Spring Boot application

- Exposes port `8080`
- Depends on the `db` service
- Connects to the database using environment variables

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_HOST: db
      DB_PORT: 3306
      DB_NAME: restauranthub_db
      DB_USERNAME: root
      DB_PASSWORD: root
    depends_on:
      db:
        condition: service_healthy
  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: restauranthub_db
      TZ: America/Sao_Paulo
    volumes:
      - mysql_data:/var/lib/mysql
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: [ "CMD", "mysqladmin", "ping", "-h", "localhost", "-proot" ]
      interval: 10s
      timeout: 5s
      retries: 5
volumes:
  mysql_data:
```

---

## API Documentation

Swagger UI is available after starting the application:

http://localhost:8080/swagger-ui.html

OpenAPI JSON specification:

http://localhost:8080/api-docs

---

### Usage

1. Start the application
2. Open Swagger UI in your browser
3. Use the `POST /api/v1/user-types` endpoint to create the initial user types
4. Use the `POST /api/v1/users` endpoint to create users
5. For endpoints requiring `X-User-Id`, click **Authorize** in Swagger UI and provide the user UUID

---

## Test Collections

Collections for Postman and Insomnia are available in the `/collections` directory of the repository.

### Importing

**Postman**

1. Open Postman
2. Click **Import**
3. Select the file `collections/restauranthub.postman_collection.json`

### Suggested Execution Order

1. Create user types (`POST /user-types`)
2. Create users (`POST /users`)
3. Create restaurants (`POST /restaurants`) — requires `X-User-Id` of a `RESTAURANT_OWNER`
4. Create menu items (`POST /menu-items`) — requires `X-User-Id` of the restaurant owner
5. Test read endpoints (`GET`)
6. Test update endpoints (`PATCH`)
7. Test delete endpoints (`DELETE`)

---

## Tests

### Strategy

**Unit Tests** — test each class in isolation using mocks for dependencies:

- Domain entities (`UserType`, `User`, `Restaurant`, `MenuItem`)
- Use cases (all CRUD operations for each domain)
- `AuthorizationService`

**Integration Tests** — test the full application stack:

- REST endpoints via `MockMvc`
- Database persistence via `@DataJpaTest`
- Full application context via `@SpringBootTest`

### Running Tests

```bash
# Run all tests
./mvnw test

# Run only unit tests
./mvnw test -Dgroups="unit"

# Run only integration tests
./mvnw test -Dgroups="integration"

# Generate coverage report
./mvnw verify
```

Coverage report will be available at `target/site/jacoco/index.html`.

### Coverage

| Layer                 | Coverage |
|-----------------------|----------|
| Domain Entities       | > 90%    |
| Use Cases             | > 90%    |
| Authorization Service | > 90%    |
| Overall               | > 80%    |

---

## Author

**Tiago Toloni**  
[GitHub](https://github.com/toloni) · [LinkedIn](https://linkedin.com/in/toloni)

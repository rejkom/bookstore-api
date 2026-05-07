# Book Store – REST API + RestAssured (Warsztaty QA)

## Ćwiczenia – część 7 - RequestSpecification-szablon żądań oraz filtrowanie w RestAssured

### RequestSpecification w testach-dziedziczenie po klasie

```java
//Przykład: wspólna specyfikacja w klasie bazowej
public abstract class BaseApiTest {
    protected static RequestSpecification requestSpec;

    @BeforeAll
    static void configureRestAssured() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setBasePath("/api")
                .addFilter(new RequestLoggingFilter(LogDetail.METHOD))
                .addFilter(new RequestLoggingFilter(LogDetail.URI))
                .addFilter(new ResponseLoggingFilter(LogDetail.STATUS))
                .build();

        RestAssured.requestSpecification = requestSpec;
    }
}
```

- `given()` korzysta automatycznie z `RestAssured.requestSpecification`
- nie musimy podawać baseUri, portu, basePath w każdym teście (dzięki rozszerzaniu klasy `BaseApiTest`)

```java
// Test z domyślną specyfikacją
class BookCrudApiTest extends BaseApiTest {

    @Test
    void shouldGetBooks() {
        given()
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }
}
```

### RequestSpecification w testach-rozszerzenie o adminSpec

- requestSpec to baza (adres, logowanie, basePath).
- adminSpec dodaje tylko to, czego potrzeba w tym teście (Basic Auth).
- Wzorzec: wspólna specyfikacja + lokalne rozszerzenia zamiast kopiowania parametrów w kilkunastu miejscach.

```java
// rozszerzenie specyfikacji o auth
@Test
void shouldCreateBookAsAdmin() {
    RequestSpecification adminSpec = requestSpec
            .given()
            .auth().basic("admin", "admin123");

    given()
            .spec(adminSpec)
            .body(newBookJson)
            .contentType("application/json")
            .when()
            .post("/books")
            .then()
            .statusCode(201);
}
```

---

### Logowanie w testach tylko w przypadku błędów-Rozwiązanie 1

```java

@BeforeAll
static void configureRestAssured() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8080;
    RestAssured.basePath = "/api";

// Loguj request + response tylko, gdy asercje nie przejdą
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
}
```

### Logowanie w testach tylko w przypadku błędów-Rozwiązanie 2

- `LogConfig` z konkretnym poziomem logowania
- Logujemy tylko body requestu i response’a, gdy walidacja zawiedzie (zamiast ALL)
- Można użyć też `LogDetail.HEADERS`, `LogDetail.STATUS`, itd., w zależności co jest dla nas przydatniejsze

```java
import static io.restassured.config.LogConfig.logConfig;

@BeforeAll
static void configureRestAssured() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.config = RestAssured.config()
            .logConfig(LogConfig.logConfig()
                    .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.BODY));
}
```

### Logowanie w testach tylko w przypadku błędów-Rozwiązanie 3

- ErrorLoggingFilter loguje body odpowiedzi tylko wtedy, gdy status jest błędny – z zakresu
- To dobry kompromis w dużych projektach:
  nie spamujemy logów przy 200/201, a gdy API faktycznie zwraca błąd, mamy od razu treść odpowiedzi do analizy.

```java
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.builder.RequestSpecBuilder;

public class BaseApiTest {
    protected static RequestSpecification requestSpec;

@BeforeAll
static void configureRestAssured() {
    requestSpec = new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8080).setBasePath("/api")
            .addFilter(new ErrorLoggingFilter()) // loguj body tylko dla 4xx/5xx 
            .build();
}
}

```
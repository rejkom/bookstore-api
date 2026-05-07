# Book Store – REST API + RestAssured (Warsztaty QA)

## Ćwiczenia – część 3

Uruchom aplikację BookstoreApplication.

### Ćwiczenie 1: RestAssured-Sprawdzanie statusu

Napisz test wykorzystując bibliotekę RestAssured i sprawdź, czy Book Store zwraca listę książek.

- Test weryfikuje kod statusu

```java

@Test
void shouldGetAllBooks() {
    given()
            .baseUri("http://localhost:8080")
            .when()
            .get("/api/books")
            .then()
            .statusCode(200);
}
```

### Ćwiczenie 2: RestAssured-Sprawdzanie statusu i nagłówków

Napisz test wykorzystując bibliotekę RestAssured i sprawdź, czy Book Store zwraca listę książek.

- Test weryfikuje kod statusu oraz nagłówki (header("Content-Type", ...))

```java

@Test
void shouldReturnJsonForAllBooks() {
    given()
            .baseUri("http://localhost:8080")
            .when()
            .get("/api/books")
            .then()
            .statusCode(200)
            .header("Content-Type", containsString("application/json"));
}
```

### Ćwiczenie 3: RestAssured-sprawdzanie zwracanej treści

RestAssured pozwala asercjonować body JSON przy użyciu JsonPath i matcherów Hamcrest
(anagram „matchers” – definiowanie reguł do pasowania w asercjach).

Dla endpointu GET /api/books spodziewamy się listy książek (tablica JSON).

- Przykład asercji na rozmiar listy:

```java

@Test
void shouldReturnJsonForAllBooks() {
    given()
            .baseUri("http://localhost:8080")
            .when()
            .get("/api/books")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(1));
}
```

- "size()" – liczba elementów w tablicy JSON na poziomie root.
- Sprawdź co się stanie gdy: greaterThan(3)

### Ćwiczenie 4: RestAssured-path parameters

Chcemy, przetestować pobranie konkretnej książki po id.

- Pobieramy książkę Clean Code o id = 1
- pathParam("id", 1L) – bezpiecznie wstawia id do ścieżki URL.

```java

@Test
void shouldReturnJsonForAllBooks() {
    given()
            .baseUri("http://localhost:8080")
            .pathParam("id", 1L)
            .when()
            .get("/api/books{id}")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", notNullValue())
            .body("author", notNullValue());
}
```

### Ćwiczenie 5: RestAssured-test negatywny: brak książki 404

Testujemy, co się stanie, gdy książka nie istnieje.

- Spodziewamy się: 404 Not Found,
- Dzięki temu w testach weryfikujemy przewidywalne zachowanie API w sytuacji błędnej.

```java

@Test
void shouldReturn404ForMissingBook() {
    given()
            .baseUri("http://localhost:8080")
            .pathParam("id", 999999L)
            .when()
            .get("/api/books/{id}")
            .then()
            .statusCode(404)
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"));
}
```

### Ćwiczenie 6: RestAssured-Query params – filtrowanie

Sprawdzamy filtrowanie po parametrze query author.

- RestAssured sam zakoduje spacje w wartości (queryParam).

```java

@Test
void shouldFilterBooksByAuthor() {
    given()
            .baseUri("http://localhost:8080")
            .queryParam("author", "Robert C. Martin")
            .when()
            .get("/api/books")
            .then()
            .statusCode(404)
            .body("status", equalTo(200))
            .body("size()", greaterThan(200))
            .body("author", everyItem(equalTo("Robert C. Martin")));
}
```
"author" w everyItem(...) – odnosi się do listy wartości pola author dla wszystkich elementów listy.
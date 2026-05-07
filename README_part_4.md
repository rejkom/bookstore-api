# Book Store – REST API + RestAssured (Warsztaty QA)

## Ćwiczenia – część 4 - testy POST/PUT/PATCH/DELETE

- Testy nie powinny się wzajemnie psuć – każdy test ma swoje dane wejściowe, najlepiej tworzone w danym teście (POST na
  początku scenariusza).
- Po operacjach modyfikujących warto sprawdzić końcowy efekt (GET po POST/PUT/PATCH/DELETE, a nie tylko sam status
  kodu).
- Uważaj na wielokrotne uruchomienie testów – unikaj „twardych” id, których istnienie może się zmieniać; w testach
  lepiej
  tworzyć dane ad‑hoc.
- Trzymaj logikę pomocniczą (np. tworzenie książki do testu) w helpers methods, żeby nie powielać kodu w każdej
  metodzie.

Przed wykonaniem testów pamiętaj, aby uruchomić aplikację `BookstoreApplication`.

### Ćwiczenie 1: Poznaj Book Store API w Swagger UI

Uruchom Swagger UI: http://localhost:8080/swagger-ui.html :
– zobacz listę endpointów: (/api/books, /api/books/{id}, /api/auth, /api/secure/books)

Znajdź `GET /api/books` i:

- odczytaj status odpowiedzi sukcesu (np. 200),
- sprawdź typ odpowiedzi (lista książek w JSON),
- użyj Try it out i zobacz przykładowe body.

Znajdź `GET /api/books/{id}` i:

- sprawdź typ i wymaganie parametru {id},
- wykonaj request z id = 1 i wypisz pola, które widzisz w odpowiedzi (id, title, author, price, isbn).

Znajdź `POST /api/books` i:

- obejrzyj model request body,
- wykonaj POST z własnymi danymi książki,
- sprawdź status (201) i nagłówek Location,
- wykonaj GET na ten Location i upewnij się, że książka została utworzona.

### Ćwiczenie 2: RestAssured-dodawanie nowej książki (POST)

Stwórz mapę zawierającą nową książkę:

```java
Map<String, Object> newBook = Map.of(
        "title", "Clean Architecture",
        "author", "Robert C. Martin",
        "price", 149.90,
        "isbn", "978-0134494166"
);
```

Utwórz test, w którym użyjesz metody POST i przekażesz książkę:

```java

@Test
void shouldCreateNewBook() {
    given()
            .baseUri("http://localhost:8080")
            .contentType("application/json")
            .body(newBook)
            .when()
            .post("/api/books")
            .then()
            .statusCode(201);
}
```

### Ćwiczenie 3: RestAssured-dodawanie nowej książki (POST) z weryfikacją (GET)

Sugerowany scenariusz tworzenia zasobu:

- POST → 201 Created,
- nagłówek Location z adresem nowej książki,
- znając `location` wykonujemy GET celem weryfikacji czy otrzymamy nową książkę.

```java

@Test
void shouldCreateBookAndGetItBack() {
    Map<String, Object> newBook = Map.of(
            "title", "Clean Architecture",
            "author", "Robert C. Martin",
            "price", 149.90,
            "isbn", "978-0134494166"
    );

    // 1. POST – tworzenie
    String location =
            given()
                    .baseUri("http://localhost:8080")
                    .contentType("application/json")
                    .body(newBook)
                    .when()
                    .post("/api/books")
                    .then()
                    .statusCode(201)
                    .header("Location", notNullValue())
                    .extract()
                    .header("Location");

    // 2. GET – weryfikacja
    given()
            .baseUri("http://localhost:8080")
            .when()
            .get(location)
            .then()
            .statusCode(200)
            .body("title", equalTo("Clean Architecture"))
            .body("author", equalTo("Robert C. Martin"));
}

```

### Ćwiczenie 4: RestAssured-pełna aktualizacja istniejącego zasobu (PUT)

```java

@Test
void shouldUpdateAllBookFieldsWithPut() {
    Map<String, Object> updatedBook = Map.of(
            "title", "Clean Code (2nd Edition)",
            "author", "Robert C. Martin",
            "price", 159.90,
            "isbn", "978-0132350884"
    );

    given()
            .baseUri("http://localhost:8080")
            .contentType("application/json")
            .pathParam("id", 1L)
            .body(updatedBook)
            .when()
            .put("/api/books/{id}")
            .then()
            .statusCode(200)
            .body("title", equalTo("Clean Code (2nd Edition)"))
            .body("price", equalTo(159.90f));
}

```

### Ćwiczenie 5: RestAssured-częściowa aktualizacja istniejącego zasobu (PATCH)

```java

@Test
void shouldUpdateOnlyPriceWithPatch() {
    long id = 1L;

    // 1. GET przed zmianą
    var before =
            given()
                    .baseUri("http://localhost:8080")
                    .pathParam("id", id)
                    .when()
                    .get("/api/books/{id}")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();

    double oldPrice = before.getDouble("price");
    String titleBefore = before.getString("title");

// 2. PATCH – zmiana ceny
    Map<String, Object> priceUpdate = Map.of("price", oldPrice + 10.0);

    given()
            .baseUri("http://localhost:8080")
            .contentType("application/json")
            .pathParam("id", id)
            .body(priceUpdate)
            .when()
            .patch("/api/books/{id}/price")
            .then()
            .statusCode(200);

    // 3. GET po zmianie
    given()
            .baseUri("http://localhost:8080")
            .pathParam("id", id)
            .when()
            .get("/api/books/{id}")
            .then()
            .statusCode(200)
            .body("price", equalTo((float) (oldPrice + 10.0)))
            .body("title", equalTo(titleBefore));
}
```

### Ćwiczenie 6: RestAssured-usunięcie istniejącej książki (DELETE)

```java

@Test
void shouldDeleteBookAndReturn404Afterwards() {
    // 1. POST – tworzymy książkę
    Map<String, Object> newBook = Map.of(
            "title", "To Delete",
            "author", "Anonymous",
            "price", 99.99,
            "isbn", "111-1111111111"
    );

    String location =
            given()
                    .baseUri("http://localhost:8080")
                    .contentType("application/json")
                    .body(newBook)
                    .when()
                    .post("/api/books")
                    .then()
                    .statusCode(201)
                    .extract()
                    .header("Location");

    // 2. DELETE – usuwamy
    given()
            .baseUri("http://localhost:8080")
            .when()
            .delete(location)
            .then()
            .statusCode(204);

    // 3. GET – sprawdzamy 404
    given()
            .baseUri("http://localhost:8080")
            .when()
            .get(location)
            .then()
            .statusCode(404);
}
```
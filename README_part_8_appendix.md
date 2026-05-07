# Book Store – REST API + RestAssured (Warsztaty QA)

## Ćwiczenia – część 8 - Praca z jsonPath oraz biblioteką Jackson

JSON (JavaScript Object Notation) to lekki tekstowy format wymiany danych – łatwy do czytania dla ludzi i maszyn.

Składa się z:

- obiektów `{ ... }` – para klucz : wartość
- tablic `[ ... ]` – uporządkowana lista wartości

- Typowe wartości: string, number, boolean, null, obiekt, tablica.
- W nowoczesnych API JSON jest de facto standardem odpowiedzi HTTP (REST, mikroserwisy)

### Test dla /books (lista niepusta + wartości pól)
- `"$"` – cała tablica książek (korzeń JSON).
- `"[0].title" / "[0].price" / "[0].isbn"` – pola pierwszej książki.
- Jednym testem sprawdzamy: status, niepustą listę i sensowne wartości biznesowe (tytuł, cena, ISBN).

```java
@Test 
void shouldReturnNonEmptyBooksList() { 
   given() 
      .basePath("/api/books") 
   .when() 
      .get() 
   .then() 
      .statusCode(200) // lista nie jest pusta 
      .body("$", not(empty())) // pierwszy element ma tytuł 
      .body("[0].title", notNullValue()) // cena > 0 
      .body("[0].price", greaterThan(0.0f)) // isbn nie jest pusty          
      .body("[0].isbn", not(isEmptyOrNullString())); 
}
```
### Test dla /books (liczba elementów)
- `jsonPath().getList("$")` – pobiera tablicę `[ {book1}, {book2}, ... ]` jako listę Javy.
- W danych startowych mamy 3 książki – w testach wystarczy warunek „co najmniej jedna książka”.
```java
@Test
void shouldReturnAtLeastOneBook() { 
    var response = 
      given()
         .basePath("/api/books") 
      .when() 
         .get() 
      .then() 
         .statusCode(200) 
         .extract() 
         .response(); 
int size = response.jsonPath().getList("$").size(); assertThat(size).isGreaterThan(1); 
}
```

## Serializacja i deserializacja JSON do obiektów Java
#### Biblioteka Jackson
Jackson to popularna biblioteka Javy do serializacji i deserializacji JSON:
- zamienia obiekty Javy na JSON i JSON na obiekty Javy.
- Jest domyślnie używana w Spring Boot, dlatego bez dodatkowej konfiguracji kontrolery REST mogą automatycznie zwracać JSON i przyjmować JSON jako DTO 
- Data Transfer Object – zawiera publiczne dane i nie posiada logiki biznesowej

### Mapowanie JSON na obiekt BookDto w teście
```java
public record BookDto(Long id, String title, String author, BigDecimal price, String isbn, Instant createdAt,
                      Instant updatedAt) {
}

@Test 
void shouldMapSingleBookToBookDto() { 
   BookDto book = 
      given() 
         .basePath("/api/books") 
         .pathParam("id", 1) 
      .when() 
         .get("/{id}") 
      .then() 
         .statusCode(200) 
         .extract() 
         .as(BookDto.class); 

assertThat(book.title()).isEqualTo("Clean Code"); 
assertThat(book.price()).isGreaterThan(BigDecimal.ZERO); 
assertThat(book.isbn()).isEqualTo("978-0132350884"); 
assertThat(book.createdAt()).isNotNull(); 
assertThat(book.updatedAt()).isNotNull(); 
}
```

### Mapowanie BookDto na JSON
```java
public record BookDto(Long id, String title, String author, BigDecimal price, String isbn, Instant createdAt,
                      Instant updatedAt) {

    public BookDto(String title, String author, BigDecimal price, String isbn) {
        this(null, title, author, price, isbn, null, null);
    }
}

@Test 
void shouldCreateBookFromDto() {
    BookDto newBook =
            new BookDto("Clean Code", "Robert C. Martin", new BigDecimal("99.90"), "978-0132350884");

   given()
      .contentType(ContentType.JSON) 
      .body(newBook) // BookDto -> JSON (Jackson) 
   .when() 
      .post("/books") 
   .then() 
      .statusCode(201) 
      .body("title", equalTo("Clean Code")) 
      .body("author", equalTo("Robert C. Martin")); 
}
```
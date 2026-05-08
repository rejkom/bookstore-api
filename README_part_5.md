# Book Store – REST API + RestAssured (Warsztaty QA)

## Ćwiczenia – część 5 - Autentykacja i Autoryzacja w testach API

1. Otwórz klasę `SecuirtyConfig`
2. Zakomentuj istniejącą metodę `apiSecurityFilterChain` oraz odkomentuj poniższą, aby włączyć autentykację

```java

@Bean
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    // publiczne GET-y
                    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                    // tokenowe endpointy obsługujemy ręcznie
                    .requestMatchers("/api/auth/**", "/api/secure/**").permitAll()
                    // modyfikacje książek tylko dla ADMIN
                    .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/books/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                    // reszta – domyślnie wymaga uwierzytelnienia
                    .anyRequest()
                    .authenticated()
            )
            .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

3. Uruchom ponownie aplikację `BookstoreApplication`.
4. Od momentu włączenia autentykacji (klasa `SecurityConfig`) modyfikacje książek (POST/PUT/PATCH/DELETE) wymagają
   `auth().basic(...)`

### Ćwiczenie 1: RestAssured-Basic Auth w testach

- Uruchom istniejący test tworzący nową książkę przy wykorzystaniu metody `POST`
- Sprawdź kod błędu (40?)
- Dodaj Basic Auth w teście i uruchom ponownie test
  (RestAssured ma wbudowaną obsługę Basic Auth – nie musimy ręcznie kodować w base64)

```java

@Test
void shouldCreateNewBookForAuthenticatedUser() {
    given()
            .baseUri("http://localhost:8080")
            .contentType("application/json")
            .auth().basic("admin", "admin123")
            .contentType("application/json")
            .body(newBook)
            .when()
            .post("/api/books")
            .then()
            .statusCode(201);
}
```

### Ćwiczenie 2: OAuth2 w Book Store (token)

- Najpierw pobierz token

```java
String token =
        given()
                .baseUri("http://localhost:8080")
                .contentType(ContentType.JSON)
                .auth().basic("admin", "admin123")
                .body(""" 
                        { "username": "student", "password": "student" } 
                        """)
                .when()
                .post("/api/auth/token")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
```

- w nagłówku dodaj `Authorization: Bearer <twój-token>`
- wykonaj `GET http://localhost:8080/api/secure/books`
```java
@Test
 void secureBooksWithTokenShouldReturnBooks() {
    given()
            .baseUri("http://localhost:8080")
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            //.auth().oauth2(token) - sprawdź czy zamiast header ta opcja również zadziała
            .when()
            .get("/api/secure/books")
            .then()
            .statusCode(200)
            .body("$", not(empty())); //$ sprawdza cały root odpowiedzi
}
```
### Ćwiczenie 3: Brak nagłówka Authorization
 ```java
@Test
void secureBooksWithoutTokenShouldReturn401() {
    given()
            .baseUri("http://localhost:8080")
            .contentType("application/json")
            .accept(ContentType.JSON)
            .when()
            .get("/api/secure/books")
            .then()
            .statusCode(401);
}
 ```
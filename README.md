# Book Store – REST API + RestAssured (Warsztaty QA)

Ten projekt został przygotowany na potrzeby zajęć ze **zautomatyzowanego testowania RESTful API w Javie z użyciem
biblioteki RestAssured**.

Repozytorium zawiera:

- aplikację Book Store napisaną w Spring Boot (Java 21),
- zestaw przykładowych testów API,
- opis ćwiczeń, które realizujemy krok po kroku na zajęciach.

---

## 1. Wymagania wstępne

- Java 25 (lub nowsza),
- Maven 3.9+,
- dostęp do Internetu (tylko dla ćwiczeń z zewnętrznymi API),
- dowolne IDE (IntelliJ / Eclipse / VS Code) lub edytor + terminal.

---

## 2. Uruchomienie aplikacji Book Store

1. Sklonuj repozytorium:

   ```bash
   git clone https://github.com/rejkom/bookstore-api.git
   cd bookstore-api
   ```

2. Uruchom aplikację:

   ```bash
   mvn spring-boot:run
   ```

   Domyślnie aplikacja wystartuje pod adresem:

    - `http://localhost:8080`

3. Sprawdź, czy działa:

    - `GET http://localhost:8080/api/books` powinien zwrócić listę książek (np. w przeglądarce lub w Hoppscotch).

---

## 3. Narzędzia, których używamy

- **RestAssured** – biblioteka do testów REST API w Javie (statyczne DSL)
- **JUnit 5** – framework testowy.
- **Hoppscotch** – webowy klient HTTP (alternatywa dla Postmana, działa w przeglądarce)

---

## 4. Struktura projektu

Najważniejsze pakiety:

- `src/main/java/pl/pg/qa/bookstore` – kod aplikacji (Spring Boot),
- `src/test/java/pl/pg/qa/bookstore/api` – testy RestAssured.

W katalogu `pl.pg.qa.bookstore.api` znajdziesz m.in.:

- `BaseApiTest` – konfiguracja RestAssured (adres, port, logowanie),

---

## 5. Jak uruchamiać testy

Z poziomu terminala:

```bash
mvn test
```

Z poziomu IDE:

- otwórz dowolną klasę testową,
- uruchom pojedynczy test lub całą klasę, korzystając z runnera JUnit.

Upewnij się, że aplikacja Book Store jest uruchomiona (**`mvn spring-boot:run`**) przed odpaleniem testów.

---

## 6. Dalsza lektura

Oficjalna dokumentacja RestAssured:

- [https://rest-assured.io](https://rest-assured.io)
  „A Guide to REST-assured” – Baeldung (tutorial z przykładami i konfiguracją):
- https://www.baeldung.com/rest-assured-tutorial
  Polski artykuł: „Jak testować REST API w Javie korzystając z biblioteki RestAssured”:
- https://tujestbug.pl/blog/jak-testowac-rest-api-w-javie-korzystajac-z-biblioteki-restassured/
# Book Store – REST API + RestAssured (Warsztaty QA)


## Ćwiczenia – część 1

Uruchom aplikację BookstoreApplication.

### Ćwiczenie 1: Hoppscotch-Odczyt listy książek (GET)
- Upewnij się, że aplikacja Book Store jest uruchomiona: `mvn spring-boot:run`
- Otwórz Hoppscotch w przeglądarce (https://hoppscotch.io/)
- Gdy pojawi się Extenstion error wybierz `Browser extenstion`

Wykonaj:
- `GET http://localhost:8080/api/books`
Sprawdź:
- status (200),
- nagłówek Content-Type (powinien wskazywać JSON),
- czy w body jest lista książek (tablica obiektów JSON)


### Ćwiczenie 2: Hoppscotch-Path params i 404
W Hoppscotch wykonaj:
    - `GET http://localhost:8080/api/books/1` – istniejąca książka, status odpowiedzi 200
    - `GET http://localhost:8080/api/books/999999` – książka nie istnieje, powinno być 404.


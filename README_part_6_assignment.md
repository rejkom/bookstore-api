# Book Store – REST API + RestAssured (Warsztaty QA)

## Ćwiczenia – część 6 - Zaliczenie przedmiotu (Zewnętrzne API)

### Warunki zaliczenia

Wybrane zadanie:

1. API pogodowe
2. Poke API
3. lub wybrane z listy

- Testy napisane przy wykorzystaniu RestAssured
- Wyjaśnienie napisanych testów

### 1. API pogodowe

Dokumentacja: https://open-meteo.com/en/docs

#### Test 1 – status 200 + podstawowe pola:

Napisz test `shouldReturnCurrentWeatherForGivenLocation`, który:

Wysyła `GET /v1/forecast` z parametrami:

- latitude, longitude (np. dla Gdańska),
- current=temperature_2m,wind_speed_10m,

Sprawdza, czy:

- statusCode(200),
- w odpowiedzi istnieje sekcja current,
- np. current.temperature_2m nie jest null,
- np. current.wind_speed_10m nie jest null.

#### Test 2 – inna lokalizacja

- Skopiuj test i zmień współrzędne na inne miasto (np. Warszawa / Kraków).
- Sprawdź, że nazwa parametrów i struktura odpowiedzi są takie same, status też 200.

#### Test 3 – niepoprawne parametry

- Spróbuj wywołać endpoint z brakującymi lub „dziwnymi” parametrami (np. bez latitude).
- Zobacz, jaki status i błąd zwraca API – opisz to w komentarzu/przy prezentacji (nawet jeśli nie ma asercji na błąd).

---

### 2. Poke API

Dokumentacja: https://pokeapi.co/docs/v2

#### Test 1 – szczegóły konkretnego Pokémona (pikachu)

Napisz test który:

Wysyła `GET /api/v2/pokemon/pikachu`

Sprawdza, czy:

- statusCode(200),
- body("name", equalTo("pikachu")),
- body("id", equalTo(25)) (oficjalne ID Pikachu),
- czy tablica types zawiera element z type.name == "electric"

#### Test 2 – Pokemon po ID i po nazwie (spójność)

Wykonaj:
`GET /api/v2/pokemon/25`
`GET /api/v2/pokemon/pikachu`

Pobierz z obu odpowiedzi:

- name
- id

Sprawdź, czy:

- id jest taki sam w obu odpowiedziach
- name jest taki sam ("pikachu")

#### Test 3 – Filtrowanie po typie (np. electric)

Wykonaj:
`GET /api/v2/type/electric`

- W odpowiedzi powinna być lista Pokémonów typu electric.

Wybierz kilka pierwszych i dla każdego z nich wykonaj:

- GET /api/v2/pokemon/{name} (może być w pętli lub parametrized test).
- Dla każdego sprawdź, że w tablicy types występuje type.name == "electric".

---

### 3. Dowolne publiczne API
Wybierz dowolne publiczne API z list:
https://apipheny.io/free-api/
https://mixedanalytics.com/blog/list-actually-free-open-no-auth-needed-apis/

- Napisz 3 Testy GET w RestAssured wykorzystując wybrane API (status + logika biznesowa)
- Wyjaśnij grupie, jakie API zostało wybrane oraz omówić napisane testy


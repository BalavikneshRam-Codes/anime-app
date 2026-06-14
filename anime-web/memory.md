# Chat Context & Memory

This file serves as a memory of the features implemented, bugs fixed, and architectural decisions made during our current pair-programming session on the `anime-web` project.

## Features Implemented

### 1. `fetchRecentAnimeList` API
- **Goal:** Fetch a paginated list of Animes sorted by the latest updated episode.
- **Implementation:** Added a custom JPQL query in `AnimeRepository.java`. 
- **Key Detail:** Because the `updateAt` field in the database is a `String`, we used `CAST(e.updateAt AS timestamp)` inside the `MAX()` function to ensure it sorts correctly chronologically, rather than alphabetically.

### 2. Global Exception Handling
- **Goal:** Provide a clean, consistent JSON error response when exceptions occur.
- **Implementation:** Created a new `com.bu.anime_web.exception` package containing:
  - `ErrorResponse.java` (status, message, timestamp)
  - `GlobalExceptionHandler.java` (using `@RestControllerAdvice`)
- **Key Detail:** We explicitly catch `IllegalArgumentException` (HTTP 400) and generic `Exception` (HTTP 500).
- **Service Update:** Updated the pagination parsing logic in `AnimeService` to throw an `IllegalArgumentException` instead of swallowing `NumberFormatException`s, ensuring invalid requests are caught by the handler.

### 3. Dynamic Filtering via Custom Repository (`loadAnime` API)
- **Goal:** Allow searching for animes dynamically based on various optional fields.
- **Implementation:** 
  - Updated `LoadAnimeRequestVO.java` to accept `title`, `type`, `genres`, `season`, `pageNum`, and `pageSize`.
  - Created `AnimeCustomRepository.java` and `AnimeCustomRepositoryImpl.java`.
  - Used `EntityManager` and `CriteriaBuilder` to dynamically append `WHERE` clauses (e.g. `LIKE %genre%`) depending on which fields the user provides.
- **Key Detail:** The `title` search matches both the `title` and alternative `titles` columns using an `OR` condition. This explains why searching for "one piece" returned "Toriko" (due to the "Toriko x One Piece Collabo Special" alternative title).

### 4. `fetchAnime` API
- **Goal:** Fetch a single anime along with its full list of episodes.
- **Implementation:** 
  - Created a new `EpisodeVO.java` in the common package to format episode data cleanly for the frontend.
  - Updated `AnimeVO.java` to include a `List<EpisodeVO> episodesList`.
  - Implemented the mapping logic in `AnimeService.fetchAnime` to map internal `Anime` and `Episode` entities to these VOs.

## Bugs Fixed & Investigations

### 1. Spring Context `NullPointerException`
- **Issue:** Calling `new AnimeSeriesService()` in `AnimeWebApplication.main` caused dependencies (like `animeRepository`) to be null.
- **Fix:** Retrieved the fully managed service bean directly from the Spring `ApplicationContext` using `context.getBean(AnimeSeriesService.class)`.

### 2. Missing Episodes in Database
- **Issue:** You noticed that `fetchAndSaveEpisodes` resulted in only 1 episode being saved to the database.
- **Investigation:** We discovered that the API deduplication logic filters out any episodes where `episodeEmbedId == null`. If the external API doesn't provide embed IDs for most episodes, they are completely ignored and not saved. Furthermore, the database query `anime_episode_id` refers to the internal DB primary key, not the external API ID.

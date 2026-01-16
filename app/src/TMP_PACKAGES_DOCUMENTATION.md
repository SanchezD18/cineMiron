# Documentation for tmp_ Packages

This document provides a comprehensive explanation of all packages beginning with `tmp_` in the CineMiron application.

## Table of Contents

1. [tmp_common](#tmp_common)
2. [tmp_di](#tmp_di)
3. [tmp_movie](#tmp_movie)
4. [tmp_ui](#tmp_ui)
5. [tmp_utils](#tmp_utils)

---

## tmp_common

### Overview
The `tmp_common` package contains shared interfaces and utilities used across multiple modules in the application.

### Components

#### `data/ApiMapper.kt`
**Purpose**: Generic interface for mapping API data transfer objects (DTOs) to domain models.

**Functionality**:
- Defines a contract for converting API response entities to domain models
- Uses generics to support different types of domain models and DTOs
- Single method: `mapToDomain(apiDto: Entity): Domain`

**Usage**: Implemented by mapper classes to transform API responses into domain models that the application can use.

---

## tmp_di

### Overview
The `tmp_di` package handles dependency injection configuration using Dagger Hilt.

### Components

#### `MovieModule.kt`
**Purpose**: Dagger Hilt module that provides dependencies for the movie feature.

**Functionality**:
- **JSON Configuration**: Sets up Kotlinx Serialization JSON parser with:
  - `coerceInputValues = true`: Allows default values when input is missing
  - `ignoreUnknownKeys = true`: Ignores unknown JSON keys during deserialization

- **Dependency Providers**:
  1. **`provideMovieRepository()`**: Creates and provides a singleton `MovieRepository` instance
     - Requires `MovieApiService` and `ApiMapper` as dependencies
     - Returns `MovieRepositoryImpl` implementation

  2. **`providoMovieMapper()`**: Provides a singleton `ApiMapper` instance
     - Returns `MovieApiMapperImpl` for mapping `MovieDto` to `List<Movie>`

  3. **`provideMovieApiService()`**: Creates and provides a singleton Retrofit API service
     - Configures Retrofit with base URL from `K.BASE_URL`
     - Uses Kotlinx Serialization converter factory
     - Returns `MovieApiService` interface implementation

**Architecture**: All dependencies are scoped as singletons, ensuring single instances throughout the application lifecycle.

---

## tmp_movie

### Overview
The `tmp_movie` package implements the movie feature following Clean Architecture principles with separate data and domain layers.

### Domain Layer

#### `domain/models/Movie.kt`
**Purpose**: Domain model representing a movie entity.

**Properties**:
- `backdropPath`: URL path for the movie backdrop image
- `genreIds`: List of genre names (converted from IDs)
- `id`: Unique movie identifier
- `originalLanguage`: Original language of the movie
- `originalTitle`: Original title of the movie
- `overview`: Movie description/synopsis
- `popularity`: Popularity score
- `posterPath`: URL path for the movie poster
- `releaseDate`: Release date string
- `title`: Movie title
- `video`: Boolean indicating if movie has video content
- `voteAverage`: Average vote rating
- `voteCount`: Total number of votes

**Note**: This is a pure domain model with no external dependencies.

#### `domain/repository/MovieRepository.kt`
**Purpose**: Repository interface defining data access contracts for movies.

**Methods**:
- `fetchDiscoverMovie()`: Returns a Flow of Response containing a list of discoverable movies
- `fetchTrendingMovie()`: Returns a Flow of Response containing a list of trending movies

**Architecture**: Uses Kotlin Flow for reactive data streams and `Response` sealed class for state management.

### Data Layer

#### `data/remote/api/MovieApiService.kt`
**Purpose**: Retrofit interface defining API endpoints for The Movie Database (TMDB) API.

**Endpoints**:
1. **`fetchDiscoverMovie()`**:
   - Endpoint: `discover/movie`
   - Query parameters:
     - `api_key`: API key from BuildConfig (default)
     - `include_adult`: Boolean flag (default: false)
   - Returns: `MovieDto`

2. **`fetchTrendingMovie()`**:
   - Endpoint: `trending/movie/week`
   - Query parameters: Same as above
   - Returns: `MovieDto`

**Implementation**: Uses Retrofit annotations (`@GET`, `@Query`) for HTTP requests.

#### `data/remote/models/MovieDto.kt`
**Purpose**: Data Transfer Object (DTO) representing the API response structure.

**Properties**:
- `page`: Current page number
- `results`: List of movie results (nullable)
- `totalPages`: Total number of pages
- `totalResults`: Total number of results

**Serialization**: Uses Kotlinx Serialization with `@Serializable` and `@SerialName` annotations for JSON mapping.

#### `data/remote/models/Result.kt`
**Purpose**: DTO representing individual movie result from API response.

**Properties**: All properties are nullable to handle missing data:
- `adult`: Adult content flag
- `backdropPath`: Backdrop image path
- `genreIds`: List of genre IDs
- `id`: Movie ID
- `originalLanguage`: Original language code
- `originalTitle`: Original title
- `overview`: Movie description
- `popularity`: Popularity score
- `posterPath`: Poster image path
- `releaseDate`: Release date
- `title`: Movie title
- `video`: Video availability flag
- `voteAverage`: Average vote
- `voteCount`: Vote count

**Serialization**: Uses Kotlinx Serialization with snake_case to camelCase mapping.

#### `data/mapper_impl/MovieApiMapperImpl.kt`
**Purpose**: Implementation of `ApiMapper` that converts `MovieDto` to domain `Movie` objects.

**Functionality**:
- **`mapToDomain()`**: Main mapping function
  - Extracts `results` from `MovieDto`
  - Maps each `Result` to a `Movie` domain model
  - Handles null values with default fallbacks
  - Converts genre IDs to genre names using `GenreConstants`

- **`formatEmptyValue()`**: Helper function
  - Handles null or empty string values
  - Returns default placeholder strings (e.g., "Unknown title", "Unknown date")
  - Prevents null pointer exceptions

- **`formatGenre()`**: Genre conversion function
  - Converts list of genre IDs to list of genre names
  - Uses `GenreConstants.getGenreNameById()` for ID-to-name mapping
  - Returns empty list if genre IDs are null

**Data Transformation**: Transforms nullable API data into non-nullable domain models with safe defaults.

#### `data/repository_impl/MovieRepositoryImpl.kt`
**Purpose**: Implementation of `MovieRepository` interface that handles data fetching and transformation.

**Functionality**:
- **`fetchDiscoverMovie()`**: 
  - Emits `Response.Loading()` state
  - Calls `MovieApiService.fetchDiscoverMovie()`
  - Maps `MovieDto` to domain models using `ApiMapper`
  - Emits `Response.Success()` with mapped data
  - Catches exceptions and emits `Response.Error()`

- **`fetchTrendingMovie()`**: 
  - Same flow as `fetchDiscoverMovie()` but for trending movies
  - Uses `MovieApiService.fetchTrendingMovie()`

**Error Handling**: Uses Kotlin Flow's `catch` operator to handle exceptions and wrap them in `Response.Error`.

**Reactive Pattern**: Returns Kotlin Flow for reactive data streams, allowing UI to observe state changes.

---

## tmp_ui

### Overview
The `tmp_ui` package contains UI-related components, specifically ViewModels that manage UI state.

### Components

#### `home/HomeViewModel.kt`
**Purpose**: ViewModel for the home screen that manages movie data state.

**Dependencies**: 
- Injected `MovieRepository` via Hilt

**State Management**:
- Uses `MutableStateFlow` for reactive state management
- Exposes `homeState` as read-only `StateFlow`
- `HomeState` data class contains:
  - `discoverMovies`: List of discoverable movies
  - `trendingMovies`: List of trending movies
  - `error`: Error message string (nullable)
  - `isLoading`: Loading state boolean

**Initialization**:
- Two `init` blocks that trigger data fetching:
  1. Fetches discover movies on ViewModel creation
  2. Fetches trending movies on ViewModel creation

**Methods**:
- **`fetchDiscoverMovie()`**: 
  - Launches coroutine in `viewModelScope`
  - Collects from repository Flow using `collectAndHandle` extension
  - Updates state based on response:
    - Loading: Sets `isLoading = true`, clears error
    - Success: Sets `isLoading = false`, updates `discoverMovies`, clears error
    - Error: Sets `isLoading = false`, sets error message

- **`fetchTrendingMovie()`**: 
  - Same pattern as `fetchDiscoverMovie()` but updates `trendingMovies`

**Architecture**: Follows MVVM pattern with reactive state management using Kotlin Flow and StateFlow.

---

## tmp_utils

### Overview
The `tmp_utils` package contains utility classes and extension functions used throughout the application.

### Components

#### `K.kt`
**Purpose**: Constants object containing API endpoints and base URLs.

**Constants**:
- `BASE_URL`: The Movie Database API base URL (`https://api.themoviedb.org/3/`)
- `BASE_IMAGE_URL`: TMDB image base URL (`https://image.tmdb.org/t/p/w500`)
- `MOVIE_ENDPOINT`: Discover movie endpoint (`discover/movie`)
- `MOVIE_DETAIL_ENDPOINT`: Movie detail endpoint (`movie`)
- `MOVIE_ACTOR_ENDPOINT`: Actor endpoint (`person`)
- `TRENDING_MOVIE_ENDPOINT`: Trending movies endpoint (`trending/movie/week`)
- `MOVIE_ID`: Movie ID parameter name (`id`)
- `ACTOR_ID`: Actor ID parameter name (`id`)

**Usage**: Centralized location for API-related constants to avoid magic strings and enable easy configuration changes.

#### `Response.kt`
**Purpose**: Sealed class representing the state of an asynchronous operation.

**States**:
1. **`Success<T>`**: Operation completed successfully
   - Contains `data: T` with the result

2. **`Error<T>`**: Operation failed
   - Contains `error: Throwable?` with exception details
   - Optional `data: T?` for partial data on error

3. **`Loading<T>`**: Operation in progress
   - No data, indicates loading state

**Usage**: Used throughout the application for handling async operations (API calls, data fetching) in a type-safe manner.

**Benefits**: Enables exhaustive when expressions and clear state handling in UI.

#### `Extension.kt`
**Purpose**: Extension function for handling Flow of Response states.

**Function**: `Flow<Response<T>>.collectAndHandle()`

**Parameters**:
- `onError: (Throwable?) -> Unit`: Callback for error state (default: logs error)
- `onLoading: () -> Unit`: Callback for loading state (default: no-op)
- `stateReducer: (T) -> Unit`: Callback for success state with data

**Functionality**:
- Collects from Flow of `Response<T>`
- Uses `when` expression to handle each response state:
  - `Response.Error`: Calls `onError` callback
  - `Response.Success`: Calls `stateReducer` with data
  - `Response.Loading`: Calls `onLoading` callback

**Usage**: Simplifies Flow collection and state handling in ViewModels, reducing boilerplate code.

**Example**: Used in `HomeViewModel` to handle repository responses and update UI state.

#### `GenreConstants.kt`
**Purpose**: Utility object for mapping genre IDs to genre names.

**Functionality**:
- **`genreMap`**: Private map containing TMDB genre ID to name mappings:
  - 28 → "Action"
  - 12 → "Adventure"
  - 16 → "Animation"
  - 35 → "Comedy"
  - 80 → "Crime"
  - 99 → "Documentary"
  - 18 → "Drama"
  - 10751 → "Family"
  - 14 → "Fantasy"
  - 36 → "History"
  - 27 → "Horror"
  - 10402 → "Music"
  - 9648 → "Mystery"
  - 10749 → "Romance"
  - 878 → "Science Fiction"
  - 10770 → "TV Movie"
  - 53 → "Thriller"
  - 10752 → "War"
  - 37 → "Western"

- **`getGenreNameById(id: Int): String`**: 
  - Looks up genre name by ID
  - Returns "Unknown" if ID not found in map

**Usage**: Used in `MovieApiMapperImpl` to convert numeric genre IDs from API to human-readable genre names.

---

## Architecture Overview

The `tmp_` packages follow **Clean Architecture** principles:

1. **Domain Layer** (`tmp_movie.domain`): Pure business logic and models
2. **Data Layer** (`tmp_movie.data`): API integration, DTOs, and repository implementations
3. **UI Layer** (`tmp_ui`): ViewModels managing UI state
4. **DI Layer** (`tmp_di`): Dependency injection configuration
5. **Common/Utils** (`tmp_common`, `tmp_utils`): Shared utilities and interfaces

### Data Flow

```
UI (HomeViewModel) 
  → Repository Interface (MovieRepository)
    → Repository Implementation (MovieRepositoryImpl)
      → API Service (MovieApiService)
        → API Response (MovieDto)
          → Mapper (MovieApiMapperImpl)
            → Domain Model (Movie)
              → UI State (HomeState)
```

### Key Patterns

- **Reactive Programming**: Kotlin Flow for asynchronous data streams
- **State Management**: Sealed class `Response` for operation states
- **Dependency Injection**: Dagger Hilt for dependency management
- **Clean Architecture**: Separation of concerns across layers
- **Type Safety**: Generics and sealed classes for compile-time safety

---

## Summary

The `tmp_` packages form a complete movie discovery feature with:
- **API Integration**: TMDB API for movie data
- **Reactive Data Flow**: Kotlin Flow for async operations
- **State Management**: Sealed classes for type-safe state handling
- **Clean Architecture**: Separation between domain, data, and UI layers
- **Dependency Injection**: Hilt for managing dependencies
- **Utility Functions**: Reusable extensions and constants

All packages work together to provide a robust, maintainable, and testable movie discovery feature.

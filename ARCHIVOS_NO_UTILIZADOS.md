# Archivos y Funciones No Utilizados

## Archivos .kt No Utilizados

### 1. `ui/screens/Home.kt`
**Estado**: ❌ NO UTILIZADO
- **Funciones no utilizadas**:
  - `HomeScreen()` - Reemplazada por `HomeScreenAPI()`
  - `HomeAppBar()` - Reemplazada por `HomeAppBarAPI()`
  - `ScrollContent()` - Reemplazada por `ScrollContentAPI()`
  - `ImageRow()` - Función auxiliar no utilizada

**Razón**: Se reemplazó completamente por `HomeAPI.kt` que usa la API de TMDB.

---

### 2. `ui/screens/Search.kt`
**Estado**: ❌ NO UTILIZADO
- **Funciones no utilizadas**:
  - `SearchScreen()` - Reemplazada por `SearchScreenAPI()`
  - `SearchApp()` - Reemplazada por `SearchAPIApp()`
  - `DockedSearchBarConFiltro()` - Reemplazada por `DockedSearchBarAPI()`
  - `searchUsers()` - Reemplazada por `searchUsersAPI()`
  - `loadNearbyUsers()` - Reemplazada por `loadNearbyUsersAPI()`
  - `loadPublicUsers()` - Reemplazada por `loadPublicUsersAPI()`

**Razón**: Se reemplazó completamente por `SearchAPI.kt` que usa la API de TMDB.

---

### 3. `ui/screens/FilmInfo.kt`
**Estado**: ❌ NO UTILIZADO
- **Funciones no utilizadas**:
  - `FilmInfo()` - Reemplazada por `FilmInfoAPI()`
  - `TopFilmColumn()` - Reemplazada por `TopFilmColumnAPI()`
  - `TopFilmInfo()` - Reemplazada por `TopFilmInfoAPI()`
  - `TrailerButton()` - Reemplazada por `TrailerButtonAPI()`
  - `DescriptionRow()` - Reemplazada por `DescriptionRowAPI()`
  - `DescriptionText()` - Función auxiliar no utilizada
  - `RatingRow()` - Reemplazada por `RatingRowAPI()`
  - `RatingColumn()` - Reemplazada por `RatingColumnAPI()`
  - `StarRatingBar()` - Reemplazada por `StarRatingBarAPI()`
  - `StarIcon()` - Reemplazada por `StarIconAPI()`

**Razón**: Se reemplazó completamente por `FilmInfoAPI.kt` que usa la API de TMDB.

---

### 4. `ui/components/CardPelis.kt`
**Estado**: ❌ NO UTILIZADO
- **Función no utilizada**:
  - `CardPelis()` - Solo se usaba en `Search.kt` que ya no se utiliza

**Razón**: Dependía de `Search.kt` que fue reemplazado.

---

### 5. `data/Pelis.kt`
**Estado**: ❌ NO UTILIZADO
- **Clase no utilizada**: `Pelis` (data class)

**Razón**: Solo se usaba en `CardPelis.kt` y `PelisRepository.kt`, ambos no utilizados.

---

### 6. `data/PelisRepository.kt`
**Estado**: ❌ NO UTILIZADO
- **Función no utilizada**:
  - `getContactInfo()` - Solo se usaba en `Search.kt` que ya no se utiliza

**Razón**: Solo se usaba en `Search.kt` que fue reemplazado.

---

## Archivos Utilizados (Mantener)

### ✅ Archivos Activos
- `ui/screens/Login.kt` - `LoginScreen()` ✅
- `ui/screens/Register.kt` - `RegisterScreen()` ✅
- `ui/screens/Profile.kt` - `ProfileScreen()` ✅
- `ui/screens/Review.kt` - `ReviewScreen()` ✅
- `ui/screens/ResetPassword.kt` - `ResetPassword()` ✅
- `ui/screens/HomeAPI.kt` - `HomeScreenAPI()` ✅
- `ui/screens/SearchAPI.kt` - `SearchScreenAPI()` ✅
- `ui/screens/FilmInfoAPI.kt` - `FilmInfoAPI()` ✅
- `data/Resenas.kt` - Usado en `Review.kt` ✅
- `data/ResenasRepository.kt` - Usado en `Review.kt` ✅

---

## Resumen

- **Total de archivos no utilizados**: 6
- **Total de funciones no utilizadas**: ~20
- **Archivos a eliminar**: 6
- **Archivos a mantener**: Todos los demás



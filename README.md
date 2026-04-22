#  Meal Prep Android App

## Overview
This is a native Android application developed as part of a university coursework. The app assists users with meal preparation by allowing them to search for recipes via a live REST API and save their favorite meals to a local database for offline viewing. 

A core architectural constraint of this project was the strict prohibition of third-party networking or image-loading libraries (such as Retrofit, Volley, Glide, or Coil). All networking, JSON parsing, and image rendering were built manually using fundamental Android APIs and Kotlin Coroutines.

## ✨ Key Features
* **Live API Integration:** Fetches meal data dynamically from `TheMealDB` REST API using `HttpURLConnection` and manual JSON parsing.
* **Ingredient Search:** Users can query the web service by specific ingredients (e.g., "chicken_breast") to find relevant recipes.
* **Local Database Caching:** Users can save downloaded web results directly to the device using the **Room Database** library (SQLite) for instant, offline retrieval.
* **Advanced Local Search:** Features a custom, case-insensitive SQL query that allows users to search their local database by either meal name or ingredient substring.
* **Custom Image Downloader:** Thumbnail images are streamed and decoded manually from the web using `BitmapFactory` on a background `Dispatchers.IO` thread, bypassing the need for external libraries.
* **Robust State Management:** Built 100% with **Jetpack Compose**. The UI safely handles configuration changes (like screen rotations) without losing data or search states.

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material Design 3)
* **Local Storage:** Room Database (SQLite)
* **Asynchronous Programming:** Kotlin Coroutines (`viewModelScope`, `Dispatchers.IO`)
* **Networking:** Standard Android `java.net.URL` and `HttpURLConnection`

## 🚀 Demonstration
(https://drive.google.com/file/d/1uAg6gAAeVObvT4TFacAc7m8zHJ67selc/view?usp=drive_link)

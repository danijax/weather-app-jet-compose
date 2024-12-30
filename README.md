# Weather Tracker

A modern Android weather application built with Kotlin and Jetpack Compose that allows users to track weather conditions for their selected city.

![Weather Tracker Banner](weather_tracker_banner.png)

## Features

- Real-time weather data from WeatherAPI.com
- Clean and intuitive UI built with Jetpack Compose
- City search functionality
- Persistent city storage
- Detailed weather metrics including:
  - Current temperature
  - "Feels like" temperature
  - Weather conditions with icons
  - Humidity levels
  - UV index

## Screenshots

<div align="center">
  <img src="screenshots/home_screen.png" alt="Home Screen" width="250"/>
  <img src="screenshots/search_screen.png" alt="Search Screen" width="250"/>
  <img src="screenshots/weather_details.png" alt="Weather Details" width="250"/>
</div>

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** Clean Architecture with MVVM
- **Dependencies:**
  - Jetpack Compose UI toolkit
  - Kotlin Coroutines
  - Retrofit for API calls
  - Dagger Hilt for dependency injection
  - DataStore for persistent storage
  - Material Design 3 components

## Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or newer
- Minimum SDK: Android 26 (Android 8.1)
- Target SDK: Android 34
- JDK 11

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/danijax/weather-app-jet-compose
   ```

2. Open the project in Android Studio

3. Create a `local.properties` file in the root directory and add your WeatherAPI.com API key:
   ```properties
   weather.api.key=your_api_key_here
   ```

4. Build and run the project

## Architecture

The app follows Clean Architecture principles with three main layers:

- **Presentation Layer:** Contains UI components and ViewModels
- **Domain Layer:** Contains business logic and use cases
- **Data Layer:** Handles data operations and external services

```
app/
├── data/
│   ├── repository/
│   ├── remote/
│   └── local/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/
    ├── ui/
    ├── viewmodel/
    └── theme/
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

- Weather data provided by [WeatherAPI.com](https://www.weatherapi.com/)
- Icons from [Material Design Icons](https://materialdesignicons.com/)
- UI/UX design inspiration from the provided Figma designs

## Contact

Your Name - [@yourusername](https://twitter.com/yourusername)

Project Link: [https://github.com/yourusername/weather-tracker](https://github.com/yourusername/weather-tracker)

# Smart Expense Tracker App - Project Report

## 1. Introduction

This report details the development of the Smart Expense Tracker mobile application, designed to assist users in managing personal finances, categorizing spending, and analyzing monthly budgets. The application leverages Kotlin for Android development, with Firebase for authentication and cloud data storage, and Room Database for local data persistence. Material Design principles are applied for a modern and intuitive user interface.

## 2. Business Problem

Many individuals encounter difficulties in effectively managing their personal finances and monitoring spending habits. The Smart Expense Tracker aims to address this by providing a robust and user-friendly platform for financial oversight.

## 3. Key Features

The application incorporates the following essential features:

*   **User Registration & Login:** Secure user authentication powered by Firebase Authentication.
*   **Add Income & Expenses:** Functionality to record both income and expense transactions.
*   **Expense Categories:** Customizable categories for detailed spending classification.
*   **Monthly Budget Management:** Tools to set and track monthly budgets, with alerts for overspending.
*   **Expense History & Search:** A comprehensive view of past transactions with search capabilities.
*   **Dashboard with Spending Summary:** An overview of financial status, including current balance, total income, and total expenses.
*   **Dark Mode Support:** User interface adapts to system-wide dark mode preferences.
*   **Push Notifications for Budget Alerts:** Timely notifications to inform users about their budget status.

## 4. Technologies Used

| Technology            | Purpose                                                               |
| :-------------------- | :-------------------------------------------------------------------- |
| **Kotlin**            | Primary programming language for Android application development.     |
| **Android Studio**    | Integrated Development Environment (IDE) for Android projects.        |
| **XML**               | Used for defining the user interface layouts.                         |
| **Firebase Authentication** | User registration, login, and session management.                     |
| **Firestore**         | NoSQL cloud database for storing and synchronizing user data.         |
| **Room Database**     | Local persistence layer for offline data access and caching.          |
| **Material Design**   | UI/UX guidelines and components for a modern Android experience.      |

## 5. Expected Outcome

The Smart Expense Tracker App is expected to facilitate better financial planning and management by providing users with clear insights into their spending habits and budget adherence.

## 6. Architecture Design and Project Structure

### 6.1. Architectural Principles

Based on Android's recommended app architecture [1], the following principles guided the application's design:

*   **Separation of Concerns:** Each component of the application has a clear and distinct responsibility, improving maintainability and testability. UI components (Activities, Fragments) primarily focus on displaying data and handling user interaction, delegating business logic and data operations to other layers.
*   **Drive UI from Data Models:** The UI is driven by data models, ensuring data persistence and a stable user experience independent of UI component lifecycles.
*   **Single Source of Truth (SSOT):** For each data type, a single source is designated as the owner, responsible for all modifications to that data. This centralizes changes, protects data integrity, and simplifies debugging.
*   **Unidirectional Data Flow (UDF):** Data flows in one direction (e.g., from data sources to UI), while events that modify data flow in the opposite direction (e.g., from UI to SSOT). This pattern enhances data consistency and reduces errors.
*   **Dependency Injection:** The Hilt library is used to manage dependencies between classes, promoting modularity and testability.

### 6.2. Layered Architecture

The application adopts a layered architecture consisting of the following layers:

#### 6.2.1. UI Layer (Presentation Layer)

*   **Responsibility:** Displays application data on the screen and handles user interactions. It observes changes in data and updates the UI accordingly.
*   **Components:**
    *   **Activities/Fragments:** Host the UI elements and act as entry points for user interaction.
    *   **ViewModels:** Act as state holders, exposing UI state to the UI and handling UI-related logic. They interact with the Domain or Data layer to retrieve and process data.
*   **Technologies:** Kotlin, XML, Material Design.

#### 6.2.2. Domain Layer (Optional)

*   **Responsibility:** Encapsulates complex business logic or simpler business logic that is reused by multiple ViewModels. This layer is optional and is introduced when needed to manage complexity or promote reusability.
*   **Components:**
    *   **Use Cases (Interactors):** Each use case represents a single, specific piece of business functionality (e.g., `GetMonthlyExpensesUseCase`, `AddTransactionUseCase`).
*   **Technologies:** Kotlin.

#### 6.2.3. Data Layer

*   **Responsibility:** Contains the business logic related to data operations, including creating, storing, and changing data. It exposes application data to the UI or Domain layer.
*   **Components:**
    *   **Repositories:** Provide a clean API for data access to the rest of the application. Each repository is responsible for a single type of data (e.g., `ExpenseRepository`, `UserRepository`). They abstract the data sources from the rest of the app and resolve conflicts between multiple data sources.
    *   **Data Sources:** Handle operations with a single source of data. These can be local (Room Database) or remote (Firebase Firestore).
        *   **Local Data Source:** Room Database for persistent local storage of expenses, categories, and budget information.
        *   **Remote Data Source:** Firebase Firestore for cloud storage and synchronization of user data. Firebase Authentication handles user registration and login.
*   **Technologies:** Kotlin, Room Database, Firebase Firestore, Firebase Authentication.

### 6.3. Project Structure

The project follows a modular structure, organizing code by feature or layer to enhance scalability and maintainability. The directory structure is as follows:

```
SmartExpenseTracker/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/smartexpensetracker/
│   │   │   │   ├── ui/                 # UI Layer (Activities, Fragments, ViewModels)
│   │   │   │   │   ├── auth/
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   ├── expenses/
│   │   │   │   │   ├── budget/
│   │   │   │   │   └── common/
│   │   │   │   ├── domain/             # Optional Domain Layer (Use Cases)
│   │   │   │   ├── data/               # Data Layer (Repositories, Data Sources)
│   │   │   │   │   ├── local/          # Room Database related (Entities, DAOs, Database)
│   │   │   │   │   ├── remote/         # Firebase related (Firestore, Auth)
│   │   │   │   │   └── models/         # Data models (shared across layers)
│   │   │   │   ├── di/                 # Dependency Injection modules (Hilt)
│   │   │   │   ├── utils/              # Utility classes
│   │   │   │   └── SmartExpenseTrackerApp.kt # Application class
│   │   │   └── res/                # Android resources (layouts, drawables, values)
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

### 6.4. Architecture Diagram

Below is a visual representation of the application's architecture:

![Architecture Diagram](architecture_diagram.png)

### 6.5. Application Flow Diagram

This diagram illustrates the main user flows within the application:

![Application Flow Diagram](app_flow_diagram.png)

## 7. Code Overview

### 7.1. Data Models (`data/models`)

*   `User.kt`: Data class representing a user with `uid`, `email`, and `displayName`.
*   `Category.kt`: Data class for expense/income categories with `id`, `name`, and `type`.
*   `Transaction.kt`: Data class for individual transactions, including `id`, `userId`, `amount`, `type`, `categoryId`, `date`, and `description`.

### 7.2. Remote Data Source (`data/remote`)

*   `AuthRepository.kt`: Interface for authentication operations (login, register, logout).
*   `AuthRepositoryImpl.kt`: Implementation of `AuthRepository` using Firebase Authentication.
*   `TransactionRepository.kt`: Interface for managing transactions.
*   `TransactionRepositoryImpl.kt`: Implementation of `TransactionRepository` using Firebase Firestore.

### 7.3. Local Data Source (`data/local`)

*   `UserEntity.kt`, `CategoryEntity.kt`, `TransactionEntity.kt`: Room entities corresponding to the data models for local persistence.
*   `UserDao.kt`, `CategoryDao.kt`, `TransactionDao.kt`: Data Access Objects (DAOs) for interacting with the Room database.
*   `AppDatabase.kt`: The main Room Database class, defining entities and DAOs.

### 7.4. ViewModels (`ui/auth`, `ui/expenses`)

*   `AuthViewModel.kt`: Handles authentication logic, interacting with `AuthRepository`.
*   `TransactionViewModel.kt`: Manages transaction-related logic, interacting with `TransactionRepository`.

### 7.5. Dependency Injection (`di`)

*   `AppModule.kt`: Hilt module providing singletons for `FirebaseAuth`, `FirebaseFirestore`, `AuthRepository`, `TransactionRepository`, `AppDatabase`, and DAOs.

### 7.6. Application Class (`SmartExpenseTrackerApp.kt`)

*   `SmartExpenseTrackerApp.kt`: The application class annotated with `@HiltAndroidApp` for Hilt setup.

## 8. UI Layouts (XML)

*   `activity_login.xml`: Layout for the user login screen.
*   `activity_register.xml`: Layout for the user registration screen.
*   `activity_dashboard.xml`: Main dashboard layout displaying summaries and recent transactions.
*   `activity_add_transaction.xml`: Layout for adding new income or expense transactions.
*   `activity_history.xml`: Layout for viewing and searching transaction history.
*   `activity_budget.xml`: Layout for managing monthly budgets and category-wise spending.
*   `item_transaction.xml`: Layout for individual transaction items in lists.
*   `item_budget_category.xml`: Layout for individual budget category items in lists.

## 9. Configuration Files

*   `google-services.json`: Placeholder for Firebase project configuration. **Users must replace this with their actual Firebase project's `google-services.json` file.**
*   `build.gradle` (Project-level): Configures project-wide dependencies and plugins, including Google Services and Hilt.
*   `build.gradle` (App-level): Configures app-specific dependencies for Firebase, Room, Hilt, Material Design, and Kotlin Coroutines.
*   `AndroidManifest.xml`: Declares application components, permissions, and integrates Firebase Messaging Service for push notifications.
*   `themes.xml` (Light & Dark): Defines the application's themes for both light and dark modes, utilizing Material Components.
*   `colors.xml`: Defines color resources used throughout the application.
*   `ic_notification.xml`: Placeholder drawable for the notification icon.

## 10. Push Notifications

*   `MyFirebaseMessagingService.kt`: A service extending `FirebaseMessagingService` to handle incoming FCM messages and display budget alert notifications.

## 11. Conclusion

This report details the architectural decisions, project structure, and implementation details for the Smart Expense Tracker App. The chosen architecture and technologies provide a scalable, maintainable, and feature-rich foundation for personal finance management. The application is designed to offer a seamless user experience with robust data handling and insightful financial tracking capabilities.

## 12. References

[1] Android Developers. (n.d.). *Guide to app architecture*. Retrieved from [https://developer.android.com/topic/architecture](https://developer.android.com/topic/architecture)

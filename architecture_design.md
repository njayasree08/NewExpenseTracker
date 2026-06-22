# Smart Expense Tracker App - Architecture Design and Project Structure

## 1. Introduction

This document outlines the proposed architecture and project structure for the Smart Expense Tracker mobile application. The application aims to help users manage personal finances by tracking daily expenses, categorizing spending, and analyzing monthly budgets. The core technologies for development include Kotlin, Android Studio, XML for UI, Firebase for authentication and cloud storage, and Room Database for local persistence.

## 2. Architectural Principles

Based on Android's recommended app architecture [1], the following principles will guide the application's design:

*   **Separation of Concerns:** Each component of the application will have a clear and distinct responsibility, improving maintainability and testability. UI components (Activities, Fragments) will primarily focus on displaying data and handling user interaction, delegating business logic and data operations to other layers.
*   **Drive UI from Data Models:** The UI will be driven by data models, ensuring data persistence and a stable user experience independent of UI component lifecycles.
*   **Single Source of Truth (SSOT):** For each data type, a single source will be designated as the owner, responsible for all modifications to that data. This centralizes changes, protects data integrity, and simplifies debugging.
*   **Unidirectional Data Flow (UDF):** Data will flow in one direction (e.g., from data sources to UI), while events that modify data flow in the opposite direction (e.g., from UI to SSOT). This pattern enhances data consistency and reduces errors.
*   **Dependency Injection:** The Hilt library will be used to manage dependencies between classes, promoting modularity and testability.

## 3. Layered Architecture

The application will adopt a layered architecture consisting of the following layers:

### 3.1. UI Layer (Presentation Layer)

*   **Responsibility:** Displays application data on the screen and handles user interactions. It observes changes in data and updates the UI accordingly.
*   **Components:**
    *   **Activities/Fragments:** Host the UI elements and act as entry points for user interaction.
    *   **ViewModels:** Act as state holders, exposing UI state to the UI and handling UI-related logic. They interact with the Domain or Data layer to retrieve and process data.
*   **Technologies:** Kotlin, XML, Material Design.

### 3.2. Domain Layer (Optional)

*   **Responsibility:** Encapsulates complex business logic or simpler business logic that is reused by multiple ViewModels. This layer is optional and will be introduced if needed to manage complexity or promote reusability.
*   **Components:**
    *   **Use Cases (Interactors):** Each use case will represent a single, specific piece of business functionality (e.g., `GetMonthlyExpensesUseCase`, `AddTransactionUseCase`).
*   **Technologies:** Kotlin.

### 3.3. Data Layer

*   **Responsibility:** Contains the business logic related to data operations, including creating, storing, and changing data. It exposes application data to the UI or Domain layer.
*   **Components:**
    *   **Repositories:** Provide a clean API for data access to the rest of the application. Each repository will be responsible for a single type of data (e.g., `ExpenseRepository`, `UserRepository`). They abstract the data sources from the rest of the app and resolve conflicts between multiple data sources.
    *   **Data Sources:** Handle operations with a single source of data. These can be local (Room Database) or remote (Firebase Firestore).
        *   **Local Data Source:** Room Database for persistent local storage of expenses, categories, and budget information.
        *   **Remote Data Source:** Firebase Firestore for cloud storage and synchronization of user data. Firebase Authentication will handle user registration and login.
*   **Technologies:** Kotlin, Room Database, Firebase Firestore, Firebase Authentication.

## 4. Project Structure

The project will follow a modular structure, organizing code by feature or layer to enhance scalability and maintainability. A possible structure could be:

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

## 5. Key Features Implementation Approach

*   **User Registration & Login:** Firebase Authentication will be integrated to handle user accounts securely. ViewModels will interact with a `UserRepository` in the Data Layer, which in turn will use a Firebase Authentication data source.
*   **Add Income & Expenses:** Data models for `Transaction` (or `Expense` and `Income`) will be defined. A `TransactionRepository` will manage persistence to both Room Database (for offline support) and Firebase Firestore (for cloud sync).
*   **Expense Categories:** Predefined and user-definable categories will be stored in Room and Firestore. A `CategoryRepository` will manage these.
*   **Monthly Budget Management:** Budget data will be stored and managed similarly to expenses. A `BudgetRepository` will handle budget creation, updates, and retrieval.
*   **Expense History & Search:** Data will be retrieved from the `TransactionRepository`, potentially with filtering and sorting logic implemented in the Domain Layer (Use Cases) for complex queries.
*   **Dashboard with Spending Summary:** ViewModels will aggregate data from various repositories to present a summary of spending, income, and budget status. This will involve calculations and data transformations.
*   **Dark Mode Support:** Material Design components will be utilized, and the application will be configured to support system-wide dark mode preferences.
*   **Push Notifications for Budget Alerts:** Firebase Cloud Messaging (FCM) will be used to send push notifications for budget-related alerts. This will involve setting up FCM in the Firebase project and handling notification logic in the app.

## 6. Next Steps

1.  Set up the basic project structure in Android Studio.
2.  Implement Firebase project setup and integrate Firebase Authentication.
3.  Define core data models (e.g., `User`, `Transaction`, `Category`, `Budget`).
4.  Implement Room Database entities, DAOs, and database class.
5.  Develop repositories and data sources for each data type.
6.  Create ViewModels and UI components for key features.

## 7. References

[1] Android Developers. (n.d.). *Guide to app architecture*. Retrieved from [https://developer.android.com/topic/architecture](https://developer.android.com/topic/architecture)

# Smart Expense Tracker App

## Project Overview

This is a mobile application developed for Android that helps users track daily expenses, categorize spending, and analyze monthly budgets. It aims to provide a comprehensive solution for personal finance management, offering insights into spending habits and promoting better financial planning.

## Key Features

*   **User Registration & Login:** Secure authentication using Firebase Authentication.
*   **Add Income & Expenses:** Record and manage all financial transactions.
*   **Expense Categories:** Organize spending with customizable categories.
*   **Monthly Budget Management:** Set budgets and receive alerts to stay within financial limits.
*   **Expense History & Search:** View and search through past transactions.
*   **Dashboard with Spending Summary:** Get a quick overview of your financial status.
*   **Dark Mode Support:** Enjoy a comfortable viewing experience in low-light conditions.
*   **Push Notifications:** Receive timely budget alerts.

## Technologies Used

*   **Kotlin:** Primary programming language.
*   **Android Studio:** Integrated Development Environment.
*   **XML:** For UI layouts.
*   **Firebase Authentication:** User authentication and management.
*   **Firestore:** Cloud NoSQL database for data storage.
*   **Room Database:** Local data persistence.
*   **Material Design:** Modern UI/UX components.
*   **Hilt:** Dependency Injection framework.
*   **Coroutines & Flow:** For asynchronous programming and reactive data streams.

## Architecture

The application follows a clean, layered architecture based on Android's recommended practices, incorporating MVVM (Model-View-ViewModel) with a Data Layer and an optional Domain Layer. Dependency Injection with Hilt is used for managing components.


## Project Structure

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

## Setup and Installation

To set up and run the Smart Expense Tracker App on your local machine, follow these steps:

### Prerequisites

*   Android Studio (Bumblebee 2021.1.1 or later)
*   Kotlin Plugin for Android Studio
*   A Firebase Project

### 1. Clone the Repository

```bash
git clone <repository_url>
cd SmartExpenseTracker
```

### 2. Firebase Project Setup

1.  **Create a Firebase Project:** Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2.  **Register your Android App:** Add an Android app to your Firebase project. Make sure to use `com.example.smartexpensetracker` as the package name.
3.  **Download `google-services.json`:** After registering your app, download the `google-services.json` file.
4.  **Replace Placeholder:** Place the downloaded `google-services.json` file into the `app/` directory of this project, replacing the existing placeholder file.
5.  **Enable Firebase Services:**
    *   **Authentication:** In the Firebase Console, navigate to "Authentication" and enable "Email/Password" sign-in method.
    *   **Firestore Database:** In the Firebase Console, navigate to "Firestore Database" and create a new database. Start in `test mode` for quick setup, or configure security rules as needed.
    *   **Cloud Messaging:** In the Firebase Console, navigate to "Cloud Messaging" to enable push notifications.

### 3. Open Project in Android Studio

1.  Open Android Studio.
2.  Select `File > Open` and navigate to the `SmartExpenseTracker` directory.

### 4. Sync Gradle Files

Android Studio will automatically prompt you to sync Gradle files. If not, click `File > Sync Project with Gradle Files`.

### 5. Run the Application

1.  Connect an Android device to your computer or start an Android Emulator.
2.  Click the `Run` button (green triangle) in Android Studio to install and launch the app on your device/emulator.

## Usage

1.  **Register/Login:** Upon first launch, register a new account or log in with existing credentials.
2.  **Dashboard:** View your current balance, total income, total expenses, and recent transactions.
3.  **Add Transaction:** Use the floating action button to add new income or expense records, specifying amount, type, category, date, and description.
4.  **History:** Browse through all your transactions and use the search bar to find specific entries.
5.  **Budget:** Set monthly budgets for different categories and monitor your spending against them.

## Contributing

Feel free to fork the repository, create feature branches, and submit pull requests. Any contributions are welcome!

## License

This project is licensed under the MIT License.

## Contact

For any questions or suggestions, please open an issue in the GitHub repository.

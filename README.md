# 💸 SpendTrack — Modern Android Spending Tracker

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://android.com) [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org) [![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.02.00-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose) [![Room](https://img.shields.io/badge/Room%20DB-2.6.1-F58220?logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room) [![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**SpendTrack** is an offline-first, modern Android expense tracker built specifically around monthly salary cycles. Unlike standard budget apps that operate from the 1st to the 31st of each calendar month, SpendTrack organizes each budget cycle starting the day following your salary credit (e.g. Day 1 = 11th through next month's 10th).

Designed with a sleek, vibrant dark aesthetic, interactive Jetpack Compose components, and a custom transparent Home Screen Arc Widget.

---

## 🌟 Key Features

### 📅 Salary-Cycle Day Tracking
* **Custom Period Cycle**: If your salary credits on the 10th, Day 1 is automatically the 11th, continuing through Day 30/31 (the 10th of next month).
* **Configurable Salary Date**: Easily customize your salary credit date (e.g. 1st, 5th, 10th, 25th) from the in-app Settings.
* **Auto Date Calculation**: Every day row automatically maps to its real-world calendar date (e.g., `Day 1 · Oct 11`).
* **Active Day Glow**: Today's row is highlighted with a subtle accent border and background tint.

### 📊 3-Column Expense Grid
* **Food 🍽️**: Dedicated tracking for daily meals, groceries, and dining.
* **Travelling 🚗**: Commute, cabs, fuel, and transport tracking.
* **Others 💰**: Miscellaneous purchases and bills.
* **Quick Amount Input**: Tap and type directly into the grid with zero clipping and centered typography.
* **Instant Auto-Save**: Entries persist automatically to local Room Database on entry.

### 🔍 Per-Cell Detail Popup
* Tap the arrow button (`>`) next to any amount to open a compact half-screen note modal.
* Add itemized breakdown notes for any day and category (e.g., *“Team lunch ₹150”*, *“Metro card recharge ₹200”*).
* Swipe or tap to delete notes with smooth item animations.

### 📉 Auto-Shifting Totals
* The Totals row stays **2 buffer rows below** your last filled day entry.
* As you enter expenses for Day 4, 5, etc., the Totals row shifts down smoothly, ensuring you always have clear visual breathing room.
* Displays dynamic column sums for Food, Travel, and Others.

### 💼 Bottom Expense Dashboard
* **Total Personal Expense**: Automatically computes `Food + Others` and shows in an elevated bottom card.
* **Total Travelling Expense**: Clear, dedicated display of all transport costs.

### 📱 Phone-Friendly WhatsApp / Social Sharing
* Generates a clean text summary formatted specifically for mobile chat bubbles:
  ```text
  📊 Spending Tracker - October 2026
  ──────────────────────
  Day | Food | Travel | Others
  ──────────────────────
  Day 1 | ₹22 | ₹55 | ₹22
  Day 2 | ₹22 | ₹22 | ₹58
  Day 3 | ₹888 | - | ₹8880
  Day 5 | ₹885 | - | -
  ──────────────────────
  Total | ₹1817 | ₹77 | ₹8960
  ──────────────────────
  💼 Total Personal Expense: ₹10777
  🚗 Total Travelling Expense: ₹77
  ```
* Dividers are engineered to never wrap onto double lines on narrow mobile screens.

### 📂 CSV Export
* Export the entire month's ledger into a structured `.csv` file saved to device Downloads.
* Includes all itemized notes joined cleanly per cell.

### 🎯 Transparent Home Screen Widget
* **Dual Circular Progress Arcs**:
  * **Personal Arc**: Outer boundary represents your custom target (default `₹2500`).
  * **Travel Arc**: Outer boundary represents your travel budget (default `₹1000`).
* **100% Transparent Background**: Sits on your phone wallpaper without solid black box borders.
* **Anti-Distortion Engine**: Image bounds and canvas aspect ratio maintain circular geometry on any launcher grid size (e.g. 2x2, 3x2, 4x2).
* **Live Sync**: Updates in real-time as you record expenses.

### ⚙️ Customizable Settings
* Custom budget limits for Personal and Travel widget circles.
* Custom salary credit day (adjusts Day 1 across all views).
* Currency symbol customization (`₹`, `$`, `€`, `£`, etc.).

---

## 🏗️ Architecture & Tech Stack

SpendTrack is developed following modern Android development best practices and official Android Jetpack architecture guidelines:

* **UI**: 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material3 components.
* **Architecture**: Modern MVVM (Model-View-ViewModel) with unidirectional data flow.
* **Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) with `Flow` reactive queries.
* **State Management**: Kotlin Coroutines & `StateFlow`.
* **JSON Serialization**: Google Gson for TypeConverters.
* **Widget Engine**: `AppWidgetProvider` with high-performance `android.graphics.Canvas` hardware rendering.
* **Scoped Storage**: Android `MediaStore` API for seamless CSV downloads without requiring legacy permissions on Android 10+.

---

## 📁 Project Directory Structure

```
SpendTrack/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/spendingtracker/
│   │   │   ├── MainActivity.kt               # Single activity hosting navigation
│   │   │   ├── data/
│   │   │   │   ├── AppDatabase.kt            # Room DB instance
│   │   │   │   ├── DayEntry.kt               # Main expense entity
│   │   │   │   ├── DayEntryDao.kt            # Reactive DB queries
│   │   │   │   ├── ExpenseDetail.kt          # Sub-item notes model
│   │   │   │   ├── Converters.kt             # Gson type converter
│   │   │   │   └── SettingsManager.kt        # User preferences & budget limits
│   │   │   ├── ui/
│   │   │   │   ├── MainScreen.kt             # Main interactive grid & sticky bottom cards
│   │   │   │   ├── ExpenseDetailDialog.kt    # Compact note modal
│   │   │   │   ├── SettingsScreen.kt         # Budget & date settings
│   │   │   │   ├── SummarySection.kt         # Clean category summary breakdown
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt              # Vibrant, balanced dark theme
│   │   │   │       ├── Theme.kt              # Material3 Theme wrapper
│   │   │   │       └── Type.kt               # Typography definition
│   │   │   ├── utils/
│   │   │   │   ├── CsvExporter.kt            # MediaStore CSV export
│   │   │   │   └── ShareUtils.kt             # Non-wrapping WhatsApp text share
│   │   │   ├── viewmodel/
│   │   │   │   └── MainViewModel.kt          # Period calculation & state holder
│   │   │   └── widget/
│   │   │       └── SpendingWidgetProvider.kt # Custom Canvas arc widget
│   │   └── res/
│   │       ├── layout/widget_spending.xml    # Transparent widget layout
│   │       ├── xml/widget_info.xml           # Widget metadata & resizing config
│   │       └── drawable/                     # App logo & vector graphics
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🚀 Building & Running

### Requirements
* **Android Studio Hedgehog** (2023.1.1) or newer
* **JDK 17**
* **Android SDK**: `compileSdk 34`, `minSdk 26` (Android 8.0 Oreo and above)

### Build Commands
```bash
# Clone the repository
git clone https://github.com/your-username/SpendTrack.git
cd SpendTrack

# Compile Debug APK
./gradlew assembleDebug

# Install directly on a connected device
./gradlew installDebug
```

The compiled APK will be located at:
`SpendTrack.apk`

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

# Quizly - Interactive Quiz Application 🎯

A modern, feature-rich Android quiz application built with Jetpack Compose and Material 3 design principles. Test your knowledge across multiple categories with an engaging, gamified experience!

## ✨ Features

### 🎮 Quiz Experience
- **8 Quiz Categories**: Science, History, Geography, Literature, Video Games, Technology, Sports, and Food & Cooking
- **Dynamic Question Loading**: Questions loaded from JSON files with Room Database caching
- **Smart Timer**: Category-based time limits with visual countdown
- **Instant Feedback**: Immediate correct/incorrect answer indicators with animations
- **Streak Tracking**: Maintain your quiz streak with fire animations
- **Score System**: Earn points for correct answers with persistent tracking

### 🎨 User Interface
- **Modern Material 3 Design**: Dynamic color support with smooth transitions
- **Smooth Swipe Animations**: Card-deck style transitions between questions
- **Confetti Effects**: Celebrate correct answers with particle animations
- **Responsive Feedback**: Visual, audio, and haptic feedback for all interactions
- **Adaptive Layouts**: Optimized for different screen sizes

### 📊 Stats & Progress
- **Interactive Charts**: Line graphs and pie charts showing progress
- **Trophy Cabinet**: 12 achievement badges with unlock animations
- **Streak Counter**: Animated fire effect for daily streaks
- **Personal Bests**: Track best scores per category
- **Category Performance**: Detailed statistics for each quiz category

### ⚙️ Settings & Customization
- **Theme Toggle**: Light, Dark, and Auto modes with smooth transitions
- **Sound Effects**: Pleasant tones for correct/incorrect answers
- **Haptic Feedback**: Vibration patterns for different interactions
- **Notification Control**: Manage daily reminders, streak alerts, and achievements
- **Data Management**: Clear history or reset progress options

### 🔔 Smart Notifications
- **Daily Reminders**: Scheduled notifications at 7:00 PM
- **Streak Alerts**: Warnings to maintain your streak (10:00 PM)
- **Achievement Notifications**: Instant alerts when badges unlock
- **Personal Best**: Celebrate new high scores
- **Three Priority Channels**: Customizable notification preferences

## 🏗️ Project Structure

### Architecture
The app follows clean architecture principles with MVVM pattern:

```
app/
├── data/
│   ├── model/                      # Data models
│   │   ├── QuizCategory.kt
│   │   ├── Question.kt
│   │   ├── QuizSession.kt
│   │   ├── Difficulty.kt
│   │   └── AnswerOption.kt
│   ├── local/                      # Local data storage
│   │   ├── ScoreDataStore.kt      # Jetpack DataStore for scores
│   │   └── database/              # Room Database
│   │       ├── QuizDatabase.kt
│   │       ├── QuestionDao.kt
│   │       └── QuestionEntity.kt
│   ├── json/                       # JSON parsing
│   │   ├── QuestionJsonModels.kt
│   │   └── QuestionJsonParser.kt
│   └── repository/                 # Data repositories
│       └── QuizRepository.kt
├── ui/
│   ├── home/                       # Home screen
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── quiz/                       # Quiz gameplay
│   │   ├── QuizScreen.kt
│   │   ├── QuizViewModel.kt
│   │   ├── QuizComponents.kt
│   │   └── Animations.kt
│   ├── results/                    # Results screen
│   │   ├── ResultsScreen.kt
│   │   ├── ResultsViewModel.kt
│   │   └── ResultsComponents.kt
│   ├── stats/                      # Statistics dashboard
│   │   ├── StatsScreen.kt
│   │   ├── StatsViewModel.kt
│   │   └── components/
│   │       ├── ChartsComponents.kt
│   │       └── GamificationComponents.kt
│   ├── settings/                   # Settings screen
│   │   ├── SettingsScreen.kt
│   │   ├── SettingsViewModel.kt
│   │   └── components/
│   │       └── SettingsComponents.kt
│   └── theme/                      # App theming
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/                           # Utilities
│   ├── SoundManager.kt            # Audio feedback
│   ├── NotificationManager.kt     # Push notifications
│   ├── ReminderScheduler.kt       # Alarm scheduling
│   └── DailyReminderReceiver.kt   # Broadcast receiver
├── navigation/                     # Navigation setup
│   ├── Screen.kt
│   └── QuizlyNavGraph.kt
├── di/                            # Dependency injection
│   └── AppModule.kt
├── MainActivity.kt
└── QuizlyApplication.kt
```

### Key Components

#### Home Screen
- **Welcome Card**: Displays app branding, user streak, and quick start button
- **Category Grid**: 2-column grid of category cards with:
  - Category-specific icons and colors
  - Dynamic question count from database
  - Smooth tap animations
  - Only shows categories with available questions

#### Quiz Screen
- **Question Display**: Card-based layout with smooth swipe transitions
- **Answer Options**: Four options with tap feedback
- **Timer**: Circular countdown with color changes
- **Progress Bar**: Shows question number and total
- **Feedback Cards**: Separate animations for correct/incorrect answers
- **Confetti Effect**: Particle celebration on correct answers

#### Results Screen
- **Score Display**: Large trophy with animated entrance
- **Statistics**: Correct answers, accuracy, time spent
- **Personal Best**: Gold flash animation for new records
- **Action Buttons**: Retry quiz or return home

#### Stats Dashboard
- **Line Chart**: Progress over time with interactive data points
- **Pie Chart**: Category performance breakdown
- **Trophy Cabinet**: 12 achievement badges in grid layout
- **Streak Counter**: Animated fire effect showing current streak
- **Overview Cards**: Total score, quizzes, average, badges

#### Settings Screen
- **Theme Toggle**: Light/Dark/Auto with visual preview
- **Notification Preferences**: Three toggles with descriptions
- **Sound & Haptics**: Independent toggles with previews
- **Data Management**: Clear history and reset progress
- **About Section**: Version, developer, rate/share options

#### Data Models
- **QuizCategory**: Enum with 8 quiz categories, each with icon and description
- **Question**: Quiz question with options, correct answer, difficulty, time limit
- **QuizSession**: Tracks current quiz state and score
- **QuizResult**: Final results with percentage and breakdown
- **Badge**: Achievement tracking with unlock status
- **CategoryScore**: Performance statistics per category

#### Theme
- **Primary Color**: Electric Blue (#00A8E8)
- **Secondary Colors**: Vibrant Purple and Teal accents
- **Success/Error**: Green and Red for feedback
- **Category Colors**: Each category has a unique color for visual distinction
- **Dark Mode**: Full support with proper contrast ratios

## 🎨 Design Features

### Visual Design
- Clean, card-based layout with rounded corners (24dp, 20dp, 16dp)
- Soft shadows and elevation for depth (2dp, 4dp, 8dp)
- Gradient overlays for visual interest
- Material 3 components throughout
- Dynamic color support (Android 12+)

### Animations
- **Smooth Transitions**: Spring-based animations with medium bouncy damping
- **Swipe Effects**: Edge-to-edge card transitions between questions
- **Scale Animations**: Bounce effects on button taps and selections
- **Fade Transitions**: Smooth opacity changes for state updates
- **Confetti Particles**: Physics-based celebration effects
- **Fire Effects**: Multi-layer flame animation for streaks
- **Chart Animations**: Progressive line drawing and pie rotation
- **Shake Effects**: Warning animations for destructive actions

### Micro-Interactions
- **Haptic Feedback**: Context-appropriate vibration patterns
- **Sound Effects**: Pleasant tones for correct/incorrect answers
- **Visual Feedback**: Instant color changes on interaction
- **Loading States**: Shimmer effects and progress indicators
- **Pull-to-Refresh**: Swipe down gesture on stats screen
- **Expandable Sections**: Smooth accordion animations

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Clean Architecture
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 36 (Android 14)

### Jetpack Libraries
- **Compose BOM**: Latest stable version
- **Navigation Compose**: Type-safe navigation
- **Room Database**: Local data persistence (v2.6.1)
- **DataStore**: Preferences and scores storage
- **Lifecycle ViewModel**: State management
- **Hilt**: Dependency injection

### Third-Party Libraries
- **Moshi**: JSON parsing and serialization (v1.15.0)
- **Material Icons Extended**: Comprehensive icon library

### Features
- **Sound System**: Android ToneGenerator for audio feedback
- **Notifications**: AlarmManager + NotificationManager
- **Vibration**: Haptic feedback with VibrationEffect
- **Animations**: Spring physics and tween animations

## 📱 Screens

### ✅ All Screens Implemented

1. **Home Screen** ✨
   - Welcome card with user greeting
   - Dynamic category grid (2 columns)
   - Quick start button
   - Stats and settings navigation
   - Streak display

2. **Quiz Screen** 🎮
   - Smooth swipe transitions between questions
   - Timer with color-coded countdown
   - Four answer options with animations
   - Separate correct/incorrect feedback cards
   - Confetti celebration on correct answers
   - Progress bar and score tracking

3. **Results Screen** 🏆
   - Animated trophy entrance
   - Score breakdown with statistics
   - Personal best celebration
   - Category performance display
   - Retry and home navigation
   - Social sharing option

4. **Stats Dashboard** 📊
   - Interactive line chart (progress over time)
   - Pie chart (category breakdown)
   - Trophy cabinet with 12 badges
   - Animated streak counter with fire effect
   - Overview cards (score, quizzes, average)
   - Pull-to-refresh functionality

5. **Settings Screen** ⚙️
   - Theme toggle (Light/Dark/Auto)
   - Notification preferences (3 types)
   - Sound effects toggle with preview
   - Vibration toggle with preview
   - Data management (clear/reset)
   - About section with app info

## 🚀 Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or device (API 26+)

## 📦 Dependencies

### Key Dependencies in `libs.versions.toml`
- **Compose BOM**: Material 3 and Compose UI
- **Navigation Compose**: Screen navigation
- **Dagger Hilt**: Dependency injection
- **Room**: Database (v2.6.1)
- **Moshi**: JSON parsing (v1.15.0)
- **DataStore**: Preferences storage
- **Material Icons Extended**: Icon library

### Gradle Configuration
```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Moshi for JSON
implementation("com.squareup.moshi:moshi:1.15.0")
implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
```

### Permissions in AndroidManifest
```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

## 🎯 Key Features Completed

### ✅ Core Functionality
- [x] All 5 screens fully implemented
- [x] Question loading from JSON files
- [x] Room Database integration
- [x] Score persistence with DataStore
- [x] Complete navigation flow

### ✅ User Experience
- [x] Sound effects and haptic feedback
- [x] Smooth swipe animations
- [x] Confetti celebrations
- [x] Loading states and error handling
- [x] Dark mode support

### ✅ Advanced Features
- [x] Stats dashboard with charts
- [x] 12 achievement badges
- [x] Streak tracking with fire animation
- [x] Notification system (3 types)
- [x] Settings with full customization

### ✅ Data Management
- [x] JSON question parser
- [x] Auto-reload on app start
- [x] Personal best tracking
- [x] Category statistics
- [x] Clear/reset functionality

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK API 26+

### Installation
1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/quizly.git
   ```

2. Open in Android Studio
   - File → Open → Select project directory

3. Sync Gradle files
   - Wait for dependencies to download

4. Build and run
   - Select device/emulator (API 26+)
   - Click Run ▶️

### Adding Questions
Questions are loaded from `app/src/main/assets/questions.json`:

```json
{
  "version": 1,
  "categories": [
    {
      "id": 1,
      "name": "Science",
      "questions": [
        {
          "id": "SCI_001",
          "text": "Your question here",
          "options": [...],
          "correctAnswerId": "B",
          "difficulty": "EASY"
        }
      ]
    }
  ]
}
```

## 📝 Project Stats

- **Total Files**: 50+ Kotlin files
- **Lines of Code**: ~15,000+
- **Screens**: 5 complete screens
- **Components**: 50+ reusable composables
- **Animations**: 40+ unique animations
- **Database Tables**: 1 (questions)
- **Notification Channels**: 3
- **Achievement Badges**: 12

## 🎨 Color Palette

### Primary
- **Electric Blue**: #00A8E8 (Primary brand color)
- **Electric Blue 60**: #60C4E8 (Light variant)

### Status Colors
- **Success Green**: #4CAF50 (Correct answers)
- **Error Red**: #F44336 (Incorrect answers)
- **Warning Orange**: #FF9800 (Warnings)
- **Info Blue**: #2196F3 (Information)

### Category Colors
- **Science**: Purple (#8B5CF6)
- **History**: Amber (#F59E0B)
- **Geography**: Green (#10B981)
- **Literature**: Pink (#EC4899)
- **Video Games**: Indigo (#6366F1)
- **Technology**: Cyan (#06B6D4)
- **Sports**: Teal (#14B8A6)
- **Food**: Yellow (#EAB308)

### UI Colors
- **Background Light**: #FFFFFF
- **Background Dark**: #121212
- **Surface**: Material 3 dynamic
- **On Surface**: Adaptive contrast

## 📸 Screenshots

### Home Screen
- Welcome card with branding
- Category grid with dynamic counts
- Quick start button

### Quiz Screen
- Question with swipe transitions
- Timer and progress indicators
- Animated feedback cards

### Results Screen
- Trophy celebration
- Score breakdown
- Personal best highlight

### Stats Dashboard
- Interactive charts
- Trophy cabinet
- Streak counter with fire

### Settings Screen
- Theme toggle with preview
- Notification controls
- Sound/haptic toggles

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Developer

Built by **DevDen Team** with ❤️

## 🙏 Acknowledgments

- Material 3 Design System
- Jetpack Compose team
- Android community
- All quiz question contributors

## 📞 Support

For support, email devden.team@example.com or open an issue on GitHub.

---

**⭐ Star this repo if you find it helpful!**

Built with ❤️ using Jetpack Compose | © 2024 DevDen Team


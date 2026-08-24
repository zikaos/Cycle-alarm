# 🌙 Cycle Alarm — Sleep Cycle Calculator & Wake-Up Optimizer

> **Wake up at the peak of your natural 90-minute sleep cycles feeling refreshed, not groggy.**

**Cycle Alarm** is a modern, privacy-first Android application designed according to circadian biology and sleep science. By aligning your bedtime and wake times with standard 90-minute human sleep cycles (plus customizable fall-asleep buffers), Cycle Alarm prevents you from waking up during deep REM or slow-wave sleep — reducing morning sleep inertia and helping you discover your optimal daily sleep duration.

---

## ✨ Features

- ⏱️ **Dual Calculation Modes**:
  - **Sleep Now**: Instantly calculates optimal wake-up times if you head to bed right now.
  - **Wake Up At**: Calculates the exact bedtimes needed to wake up fully energized at your desired alarm time.
- ⏳ **Customizable Fall-Asleep Buffer**: Adjust the time it takes you to drift off (0 to 30 minutes, 10-minute default) so every cycle calculation is accurate to your personal routine.
- ⏰ **Built-in Alarm & System Integration**:
  - Direct exact alarm scheduling via Android `AlarmManager`.
  - Seamless one-tap integration with your device's native Clock / Alarm app.
- 💤 **Cycle-Aware & Custom Snooze**:
  - Choose between micro-snoozes (5m, 10m, 15m) or a full **90-minute sleep cycle nap**.
- 🌟 **Morning Quality Feedback**:
  - Rate your wake-up energy (1–5 stars with mood indicators).
  - Select quick feeling tags (*Instant Wake*, *Refreshed*, *Deep Sleep*, *Snoozed*) and add optional notes.
- 📊 **Ideal Sleep Pattern & Correlation Insights**:
  - Automatically correlates your post-wake ratings with each cycle count (3x, 4x, 5x, 6x, 7x).
  - Identifies your personal "sweet spot" duration for peak daytime productivity.
- 🎨 **Modern Material Design 3 UI**:
  - Deep Twilight dark palette with vibrant Lavender and Mint accents.
  - Dynamic Theme toggle (Dark / Light / System).
  - Edge-to-edge support with fluid layout scaling.
- 🔒 **100% Offline & Private**: All sleep ratings and history are stored securely on-device using a local Room Database. No tracking, no account required, no telemetry.

---

## 🔬 The Science Behind Cycle Alarm

The human sleep architecture consists of repeating ~90-minute cycles transitioning through:
1. **Light Sleep (N1 / N2)**
2. **Deep Slow-Wave Sleep (N3)**
3. **REM (Rapid Eye Movement)**

Waking up in the middle of Deep Sleep causes **Sleep Inertia** — that foggy, groggy feeling that can persist for hours even after 8+ hours in bed. Waking up at the end of a complete 90-minute cycle ensures your body is in light sleep, allowing you to wake up naturally alert and clear-headed.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (100% Declarative UI)
- **Design System**: Material Design 3 (M3)
- **Architecture**: Clean Architecture / MVVM (Model-View-ViewModel) + StateFlow / Coroutines
- **Local Persistence**: Android Jetpack Room Database + SharedPreferences
- **Background & Timing**: `AlarmManager` (Exact Alarms), Foreground Services, Notifications
- **Compatibility**: Android 8.0 (API level 26) and higher

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17+
- Android SDK 35

### Building from Source
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/cycle-alarm.git
   cd cycle-alarm

# 📅 Android App Scheduler

An Android application that allows users to schedule the launch of any installed app at a specific time, with full support for canceling, updating, and querying the execution status. Built using Kotlin, Clean Architecture, Hilt, Room, RxJava3, and AlarmManager.

---
<p align="center">
  <img alt="Screenshot_20250729_142938" src="https://github.com/user-attachments/assets/398b1113-7138-466a-a2e4-24933cea961a" width="30%" />
  <img alt="Screenshot_20250729_143054" src="https://github.com/user-attachments/assets/26448cae-ff64-454a-8b08-9932f4d0a3fd" width="30%" />
  <img alt="Screenshot_20250729_142826" src="https://github.com/user-attachments/assets/d2f8e9e2-56d0-4726-9eb9-0a9cce1a3a25" width="30%" />
</p>

---

## 🚀 Features

- ⏰ Schedule any installed app to launch at a specific time.
- ✅ Support for marking schedules as **Executed**, **Cancelled**, or **Pending**.
- 🔁 Modify or cancel scheduled app launches.
- 📦 List all installed apps with app name and icon.
- 🚫 Prevent time conflicts between scheduled launches.
- 📊 View schedule history and status.
- ⚡ Reactive updates using RxJava3.

---

## 🧱 Tech Stack

| Layer            | Tech Used                            |
|------------------|--------------------------------------|
| Language         | Kotlin                               |
| Architecture     | MVVM + Clean Architecture            |
| Dependency DI    | Hilt                                 |
| DB Persistence   | Room                                 |
| Async / Reactive | RxJava3                              |
| Scheduling       | AlarmManager                         |
| UI               | XML + ViewBinding + RecyclerView     |

---

## 📂 Project Structure

```text
com.example.android.app.scheduler/
│
├── core/                      # Base classes, constants, Extensions
│
├── data/
│   ├── local/                 # Room DB, DAO, Entities
│   ├── mapper/                # Entities to Model Mapper
│   └── repository/            # ScheduleRepository
│
├── di/
│   └── AppModule.kt           # Hilt module for DI
│   ├── RepositoryModule.kt    # Provides domain repository bindings
│   └── ScheduleEntryPoint.kt  # Hilt EntryPoint for alarm-triggered receivers
│
├── domain/
│   └── model/                 # Domain models like Schedule, ScheduleStatus
│   └── repository/            # Domain-level repository interface
│
├── platform/
│   └── AppLaunchReceiver.kt   # Handles scheduled app launch
│
├── presentation/
│   ├── schedule_viewer/       # ScheduleViewerActivity, ViewModel, Adapter
│   ├── schedule_editor/       # Schedule creation/editing UI + ViewModel
│   ├── app_picker/            # AppPickerActivity, ViewModel, Adapter
│   └── shared/                # Common UI elements (components)
```
---

## 🚫 Known Limitations
### 🔒 App Launch Restrictions in Background or Terminated State
Due to Android's modern [background execution restrictions](https://developer.android.com/guide/components/activities/background-starts) (especially from Android 10+ and stricter in Android 12+), launching another app at a scheduled time does not always work, particularly in the following scenarios:

- When your app is in the background

- When your app is force-stopped or terminated

- When the target app is not in recent tasks or has no foreground activity

### 🧪 Approaches Tried (But Restricted)

| Approach                                                          | Result                                                                 |
| ----------------------------------------------------------------- | ---------------------------------------------------------------------- |
| ✅ `AlarmManager` + `PendingIntent` + `BroadcastReceiver`          | Works **only when app is in foreground**     |
| ❌ Launching other apps via `startActivity` in `BroadcastReceiver` | Blocked in **background/terminated state**                             |
| ❌ Starting `ForegroundService` with Notification                  | System allows service but **blocks launching other apps**              |

---

## 📌 Summary
Reliably launch another app at a scheduled time on newer Android versions unless:

- App is in the foreground, or

- App using system privileges (e.g. device owner, system app)

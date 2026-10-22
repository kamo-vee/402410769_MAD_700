MySmartPantry — Android Studio sourc3

This is the native Android implementation for the Mobile App Development 700 practical assignment. The app code is Java, its screens and row layouts are XML, and pantry/recipe data is stored locally in SQLite.

Open and run

1. Install Android Studio with Android SDK 35 (or allow Android Studio to install the required SDK).
2. In Android Studio, choose **Open** and select the extracted `MySmartPantry` folder containing `settings.gradle`.
3. Allow Gradle to sync. The included Gradle 8.9 wrapper downloads the matching Gradle distribution; use a compatible Gradle JDK such as JDK 21.
4. Start an Android emulator or connect an Android device with USB debugging enabled.
5. Run the `app` configuration. The launcher activity opens the Pantry screen.

From a terminal, you can also build the debug APK with `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`). Android SDK 35 must be installed.

The first launch seeds a starter pantry and 18 recipes. Pantry records are kept in the app's SQLite database after the app is closed. Delete ingredients from **Pantry** to test the no-match message and the strict recipe filter.

Included features

- Java Activities and XML layouts for Pantry, Add/Edit Ingredient, Suggested Recipes, Recipe Detail, and Settings.
- Intent-based navigation and recipe ID passing to the detail screen.
- SQLiteOpenHelper database with pantry CRUD and seeded recipe records.
- RecyclerView lists backed by SQLite and custom Java adapters.
- Add/edit form checks ingredient name, positive quantity, supported unit, and optional `YYYY-MM-DD` expiry date.
- Strict recipe matching: all named ingredients must exist in sufficient quantities. It normalizes basic plurals and converts supported units within matching dimensions (mass, volume, or count). Different dimensions never match.
- Settings preference for expiry reminders, saved with SharedPreferences.

 Strict matching to explain in your demonstration

`PantryDatabase.getStrictSuggestions()` iterates every recipe and every required ingredient. It totals matching pantry quantities after canonicalizing ingredient names and converting compatible units. If any required amount is short, it rejects the recipe immediately. It returns a recipe only after every requirement passes. The suggestions screen only displays that returned list; it does not promote partial matches.

 Submission work you must complete yourself

- Create your public GitHub repository and make genuine, incremental commits while developing. Do not invent a commit history after the fact.
- Add your real student details and repository URL to the report.
- Run this Android app and capture genuine screenshots for the report.
- Record and narrate your own 5–7 minute demonstration, including GitHub history, CRUD, persistence, a strict-match test, and explanations of code you understand.
- Write your own actual challenges/reflection and sign the institution's originality declaration.

The browser preview in the parent project is a separate interactive demonstration; it is not an Android emulator screenshot and does not replace the native Android run evidence.

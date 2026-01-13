import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {

    // 1) Zapnutí / vypnutí Dark Mode
    val DARK_MODE = booleanPreferencesKey("dark_mode")

    // 2) Uložené jméno uživatele
    val USERNAME = stringPreferencesKey("username")

    // 3) Velikost fontu (např. 14, 16, 18…)
    val FONT_SIZE = intPreferencesKey("font_size")
}
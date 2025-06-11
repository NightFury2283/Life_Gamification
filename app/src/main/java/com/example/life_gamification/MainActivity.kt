package com.example.life_gamification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.life_gamification.presentation.AppNavHost
import com.example.life_gamification.ui.theme.Life_gamificationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // 1. Получаем инстанс базы данных
//        val db = AppDatabase.getDatabase(applicationContext)
//
//        // 2. Создаём репозиторий и use case'ы
//        val userStatRepository = UserStatRepositoryImpl(db.userStatDao())
//        val addCustomStatUseCase = AddCustomStatUseCase(userStatRepository)
//        val deleteCustomStatUseCase = DeleteCustomStatUseCase(userStatRepository)
//        val getCustomStatUseCase = GetCustomStatUseCase(db.userStatDao())
//
//        // 3. Создаём ViewModel
//        val statusViewModel = StatusViewModel(
//            addCustomStatUseCase = addCustomStatUseCase,
//            deleteCustomStatUseCase = deleteCustomStatUseCase,
//            getCustomStatUseCase = getCustomStatUseCase,
//            userId =
//        )

        // 4. Передаём его в AppNavHost
        setContent {
            Life_gamificationTheme {
                AppNavHost(appContext = this)
            }
        }
    }
}

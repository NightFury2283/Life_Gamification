package com.example.life_gamification.presentation

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.life_gamification.presentation.auth.AuthViewModel
import com.example.life_gamification.presentation.auth.SignInScreen
import com.example.life_gamification.presentation.home.HomeScreen
import com.example.life_gamification.data.auth.FirebaseAuthSource
import com.example.life_gamification.data.auth.AuthRepositoryImpl
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.domain.auth.LoginWithGoogleUseCase
import com.example.life_gamification.presentation.status.StatusScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(appContext: Context) {
    val navController = rememberNavController()
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("461383006220-j22in4hv8iegblm3lfbn3b5rh3o6cs9s.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(appContext, gso) }

    val viewModel = remember {
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseSource = FirebaseAuthSource(firebaseAuth)
        val repo = AuthRepositoryImpl(firebaseSource)
        val useCase = LoginWithGoogleUseCase(repo)
        AuthViewModel(useCase)
    }

    val loginState by viewModel.loginState.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                viewModel.loginWithGoogle(token)

                val user = UserEntity(
                    id = account.id!!,
                    name = account.displayName ?: "Без имени",
                    email = account.email
                )

                val db = AppDatabase.getDatabase(appContext)

                CoroutineScope(Dispatchers.IO).launch {
                    db.userDao().insertUser(user)
                }
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
        }
    }

    LaunchedEffect(loginState) {
        if (loginState?.isSuccess == true) {
            navController.navigate("status") {
                popUpTo("sign_in") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "sign_in") {
        composable("sign_in") {
            SignInScreen(onSignInClick = {
                launcher.launch(googleSignInClient.signInIntent)
            })
        }
        composable("status") {
            StatusScreen(navController)
        }
    }
}

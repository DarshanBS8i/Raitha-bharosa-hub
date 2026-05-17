package com.raitha.bharosahub

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raitha.bharosahub.data.remote.WeatherClient
import com.raitha.bharosahub.util.AppViewModelFactory
import com.raitha.bharosahub.util.LocaleHelper
import com.raitha.bharosahub.ui.theme.RaithaBharosaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(if (newBase != null) LocaleHelper.wrap(newBase) else newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaithaBharosaTheme {
                RaithaBharosaMain()
            }
        }
    }
}

@Composable
fun LocaleAwareWrapper(languageKey: String, content: @Composable () -> Unit) {
    val baseContext = LocalContext.current
    val localizedContext = remember(languageKey) {
        LocaleHelper.setLocale(baseContext, languageKey)
    }
    val localizedConfig = remember(languageKey) {
        localizedContext.resources.configuration
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfig
    ) {
        content()
    }
}

@Composable
fun RaithaBharosaMain() {
    val context = LocalContext.current
    val app = context.applicationContext as RaithaBharosaApp
    val factory = AppViewModelFactory(app.repository, WeatherClient.api)

    var currentLang by remember { mutableStateOf(LocaleHelper.getSavedLanguage(context)) }
    var languagePickedForOnboarding by remember { mutableStateOf(false) }
    val profileState by app.repository.userProfile.collectAsState(initial = null)
    var onboardingCompleted by remember { mutableStateOf(false) }
    val hasProfile = onboardingCompleted || (profileState != null)
    val coroutineScope = rememberCoroutineScope()

    LocaleAwareWrapper(languageKey = currentLang) {
        if (!hasProfile) {
            if (!languagePickedForOnboarding) {
                com.raitha.bharosahub.ui.onboarding.LanguageSelectionScreen(
                    currentLang = currentLang,
                    onLanguageSelected = { lang ->
                        LocaleHelper.setLocale(context, lang)
                        currentLang = lang
                        languagePickedForOnboarding = true
                    }
                )
            } else {
                val onboardingViewModel: com.raitha.bharosahub.ui.onboarding.OnboardingViewModel = viewModel(factory = factory)
                com.raitha.bharosahub.ui.onboarding.OnboardingScreen(
                    viewModel = onboardingViewModel,
                    currentLang = currentLang,
                    onComplete = { onboardingCompleted = true }
                )
            }
        } else {
            MainAppShell(factory, currentLang, onLangChange = { lang ->
                LocaleHelper.setLocale(context, lang)
                currentLang = lang
                coroutineScope.launch {
                    val p = profileState
                    if (p != null) {
                        app.repository.saveProfile(p.copy(lang = lang))
                    }
                }
            }, onReset = {
                coroutineScope.launch {
                    app.repository.clearProfile()
                    onboardingCompleted = false
                    languagePickedForOnboarding = false
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(factory: AppViewModelFactory, currentLang: String, onLangChange: (String) -> Unit, onReset: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), color = Color.White, fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2D4F2B)),
                actions = {
                    TextButton(onClick = { onLangChange(if (currentLang == "en") "kn" else "en") }) {
                        Text(if (currentLang == "en") "KN" else "EN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.reset_profile)) },
                            onClick = {
                                showMenu = false
                                onReset()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFFF1CA), // Match background
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Default.Home else Icons.Default.Home, null) },
                    label = { Text(stringResource(R.string.dashboard), fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D4F2B),
                        unselectedIconColor = Color(0xFF2D4F2B).copy(alpha = 0.6f),
                        indicatorColor = Color(0xFFE1BEE7) // Light Purple pill
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Add, null) },
                    label = { Text(stringResource(R.string.input_data), fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D4F2B),
                        unselectedIconColor = Color(0xFF2D4F2B).copy(alpha = 0.6f),
                        indicatorColor = Color(0xFFE1BEE7)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.CalendarMonth, null) },
                    label = { Text(stringResource(R.string.action_plan), fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D4F2B),
                        unselectedIconColor = Color(0xFF2D4F2B).copy(alpha = 0.6f),
                        indicatorColor = Color(0xFFE1BEE7)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text(stringResource(R.string.history), fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2D4F2B),
                        unselectedIconColor = Color(0xFF2D4F2B).copy(alpha = 0.6f),
                        indicatorColor = Color(0xFFE1BEE7)
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    val vm: com.raitha.bharosahub.ui.tabs.dashboard.DashboardViewModel = viewModel(factory = factory)
                    com.raitha.bharosahub.ui.tabs.dashboard.DashboardScreen(vm)
                }
                1 -> {
                    val vm: com.raitha.bharosahub.ui.tabs.input.InputViewModel = viewModel(factory = factory)
                    com.raitha.bharosahub.ui.tabs.input.InputScreen(vm, currentLang, onSaveSuccess = { selectedTab = 0 })
                }
                2 -> {
                    val vm: com.raitha.bharosahub.ui.tabs.plan.ActionPlanViewModel = viewModel(factory = factory)
                    com.raitha.bharosahub.ui.tabs.plan.ActionPlanScreen(vm)
                }
                3 -> {
                    val vm: com.raitha.bharosahub.ui.tabs.history.HistoryViewModel = viewModel(factory = factory)
                    com.raitha.bharosahub.ui.tabs.history.HistoryScreen(vm)
                }
            }
        }
    }
}

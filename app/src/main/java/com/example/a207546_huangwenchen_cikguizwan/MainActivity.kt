package com.example.a207546_huangwenchen_cikguizwan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode = remember { mutableStateOf(true) }
            HealthTrackerTheme(darkMode = darkMode.value) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val viewModel: HealthViewModel = viewModel()
                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        darkMode = darkMode.value,
                        onDarkModeChanged = { darkMode.value = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: HealthViewModel,
    darkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = "welcome", modifier = Modifier.fillMaxSize()) {
        composable("welcome") { WelcomeScreen(navController, darkMode) }
        composable("home") { HomeScreen(navController, viewModel, darkMode) }
        composable("add_record") { AddRecordScreen(navController, viewModel, darkMode) }
        composable("stats") { StatsScreen(navController, viewModel, darkMode) }
        composable("settings") { SettingsScreen(navController, viewModel, darkMode, onDarkModeChanged) }
    }
}

@Composable
fun BackgroundBg(darkMode: Boolean, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (darkMode) Color(0x99000000) else Color.Transparent)
        ) {
            content()
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavHostController, darkMode: Boolean) {
    BackgroundBg(darkMode = darkMode) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Health Tracker", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Matric: 207546", fontSize = 18.sp, color = Color.White)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Get Started")
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HealthViewModel, darkMode: Boolean) {
    val healthState = remember { mutableStateOf(HealthData()) }

    LaunchedEffect(viewModel) {
        viewModel.healthData.observeForever { data ->
            healthState.value = data
        }
    }

    BackgroundBg(darkMode = darkMode) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Matric: 207546", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            BasicTextField(
                value = healthState.value.userName,
                onValueChange = viewModel::updateUserName,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(0.9f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                decorationBox = { inner ->
                    if (healthState.value.userName.isEmpty()) Text("Enter your name", color = Color.Gray)
                    inner()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            var tipText by remember { mutableStateOf("") }
            Button(
                onClick = {
                    tipText = if (healthState.value.userName.isNotEmpty()) "Hello ${healthState.value.userName}!" else "Please input name"
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
            ) {
                Text("Submit", color = Color.White)
            }
            if (tipText.isNotEmpty()) Text(tipText, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("add_record") }, modifier = Modifier.fillMaxWidth()) {
                Text("Add New Health Record")
            }
            Spacer(modifier = Modifier.height(16.dp))
            StepCounterUI(healthState.value)
            Spacer(modifier = Modifier.height(16.dp))
            ExerciseCard()
            Spacer(modifier = Modifier.height(16.dp))
            HeartRateCardUI()
            Spacer(modifier = Modifier.height(16.dp))
            WaterDrinkCard(viewModel, healthState.value)
            Spacer(modifier = Modifier.height(20.dp))
            BottomNav(navController)
        }
    }
}

@Composable
fun AddRecordScreen(navController: NavHostController, viewModel: HealthViewModel, darkMode: Boolean) {
    var titleTxt by remember { mutableStateOf("") }
    var valueTxt by remember { mutableStateOf("") }

    BackgroundBg(darkMode = darkMode) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Add New Record", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            BasicTextField(
                value = titleTxt,
                onValueChange = { titleTxt = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(0.9f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                decorationBox = { innerTextField ->
                    if (titleTxt.isEmpty()) {
                        Text("Record Title", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            BasicTextField(
                value = valueTxt,
                onValueChange = { valueTxt = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(0.9f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                decorationBox = { innerTextField ->
                    if (valueTxt.isEmpty()) {
                        Text("Record Value", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if (titleTxt.isNotBlank() && valueTxt.isNotBlank()) {
                    viewModel.addNewRecord(titleTxt, valueTxt)
                    navController.navigate("stats")
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Record")
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { navController.navigate("home") }) {
                Text("Back")
            }
        }
    }
}

@Composable
fun StatsScreen(navController: NavHostController, viewModel: HealthViewModel, darkMode: Boolean) {
    val healthState = remember { mutableStateOf(HealthData()) }
    val records = remember { mutableStateOf(emptyList<HealthRecordEntity>()) }

    LaunchedEffect(viewModel) {
        viewModel.healthData.observeForever { data ->
            healthState.value = data
        }
        viewModel.allRecords.collect { list ->
            records.value = list
        }
    }

    BackgroundBg(darkMode = darkMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Health Stats", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            StatItemShow("Steps", "${healthState.value.steps}/6000")
            StatItemShow("Water", "${healthState.value.waterAmount}/2000 ml")
            StatItemShow("Name", healthState.value.userName.ifEmpty { "Not Set" })

            Spacer(modifier = Modifier.height(20.dp))
            Text("Saved Records", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            records.value.forEach { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(record.title, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(record.value, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Back Home")
            }
        }
    }
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: HealthViewModel,
    darkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {
    BackgroundBg(darkMode = darkMode) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", color = Color.White, fontSize = 18.sp)
                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkModeChanged
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = viewModel::resetWater, modifier = Modifier.fillMaxWidth()) {
                Text("Reset Water Count")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Back Home")
            }
        }
    }
}

@Composable
fun StepCounterUI(state: HealthData) {
    var expand by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expand = !expand },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${state.steps}", fontSize = 72.sp, fontWeight = FontWeight.Light)
            Text("/6000 Steps", color = LightGray)
            if (expand) Text("Daily Average : 0", fontSize = 14.sp, color = LightGray)
        }
    }
}

@Composable
fun ExerciseCard() {
    var expand by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expand = !expand },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(Secondary), contentAlignment = Alignment.Center) {
                    Text("🏃", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Exercise", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
            if (expand) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("0.0", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("KM", color = LightGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("0h0m", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("TIME", color = LightGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("0.0", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("KCAL", color = LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun HeartRateCardUI() {
    var expand by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expand = !expand },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).background(Tertiary), contentAlignment = Alignment.Center) {
                    Text("❤️")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Heart Rate Tracker", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            if (expand) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Check your heart rate for better health.", color = LightGray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun WaterDrinkCard(vm: HealthViewModel, state: HealthData) {
    var expand by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expand = !expand },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(Primary), contentAlignment = Alignment.Center) {
                    Text("💧")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Water Tracker", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            if (expand) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${state.waterAmount} ml / 2000 ml", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clickable { vm.addWater(300) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                    ) {
                        Text("+300ml")
                    }
                }
            }
        }
    }
}

@Composable
fun StatItemShow(title: String, content: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = LightGray)
            Text(content, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BottomNav(nav: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.White.copy(0.95f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.clickable { nav.navigate("home") }, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏠", fontSize = 22.sp)
            Text("Home", fontSize = 12.sp)
        }
        Column(modifier = Modifier.clickable { nav.navigate("stats") }, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📊", fontSize = 22.sp)
            Text("Stats", fontSize = 12.sp)
        }
        Column(modifier = Modifier.clickable { nav.navigate("settings") }, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚙️", fontSize = 22.sp)
            Text("Setting", fontSize = 12.sp)
        }
    }
}

@Preview
@Composable
fun PreviewMainUI() {
    HealthTrackerTheme {
        WelcomeScreen(rememberNavController(), darkMode = true)
    }
}
package com.alcidev.onequestionjournal

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alcidev.onequestionjournal.manager.SettingsManager
import com.alcidev.onequestionjournal.model.JournalContent
import com.alcidev.onequestionjournal.model.JournalDBInstance
import com.alcidev.onequestionjournal.model.JournalQuestions
import com.alcidev.onequestionjournal.scheduler.NotificationScheduler
import com.alcidev.onequestionjournal.ui.theme.OneQuestionJournalTheme
import com.alcidev.onequestionjournal.viewmodel.JournalViewModel
import com.alcidev.onequestionjournal.viewmodel.JournalViewModelFactory
import com.alcidev.onequestionjournal.viewmodel.NotificationViewModel
import com.alcidev.onequestionjournal.viewmodel.NotificationViewModelFactory


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = JournalDBInstance.getDatabase(application)
        val journalDao = db.journalDao()
        val factory = JournalViewModelFactory(journalDao)

        val notificationScheduler = NotificationScheduler(applicationContext)
        val notificationFactory = NotificationViewModelFactory(notificationScheduler)

        val destination = intent?.getStringExtra("DESTINATION")

        val questionFromNotif = intent?.getStringExtra("NOTIFICATION_TITLE").toString()

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen(journalVmFactory = factory, notificationFactory = notificationFactory, navController = navController) }
                composable("add_journal") { AddJournal(factory,true, questionFromNotif) }
            }

            LaunchedEffect(destination) {
                destination?.let {
                    navController.navigate(it)
                }
            }

            ScheduleNotification(notificationFactory)
            HomeScreen(journalVmFactory = factory, notificationFactory = notificationFactory, navController = navController)
        }
    }
}

@Composable
fun HomeScreen(journalVmFactory:JournalViewModelFactory,
               notificationFactory: NotificationViewModelFactory,
               navController:NavHostController){

    val journalQuestions = JournalQuestions()


    OneQuestionJournalTheme {
        Scaffold(containerColor = colorResource(R.color.background),
            topBar = {TransparentAppBar(notificationFactory)},
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = { AddJournal(journalVmFactory, qn = journalQuestions.getRandomQuestion()) }) { innerPadding ->
            JournalList(journalVmFactory, Modifier.padding(innerPadding))
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "InlinedApi")
@Composable
fun CheckAndRequestNotificationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    var permissionGranted by rememberSaveable { mutableStateOf( ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if (isGranted) {
            onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            permissionGranted = true
            onPermissionGranted()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun NotificationSettings(isEnabled:Boolean, onDismissRequest: () -> Unit,
                         onConfirmation: (Boolean) -> Unit){

    var checked by remember { mutableStateOf(isEnabled) }


    AlertDialog(
        containerColor = colorResource(R.color.card_bg),
        titleContentColor = colorResource(R.color.question),
        textContentColor = colorResource(R.color.answer),
        modifier = Modifier.border(width = 2.dp, color = colorResource(R.color.border), shape = RoundedCornerShape(25.dp)),
        title = {
            Column {
                Text(
                    text = stringResource(R.string.settings),
                    color = colorResource(R.color.question),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Text(
                        text = stringResource(R.string.notification_toggle),
                        color = colorResource(R.color.answer),
                        modifier = Modifier.padding(end = 15.dp).align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(10.dp))
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                        },
                        modifier = Modifier.padding(end = 15.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorResource(R.color.question),
                            checkedTrackColor = colorResource(R.color.answer),
                            uncheckedThumbColor = colorResource(R.color.answer),
                            uncheckedTrackColor = colorResource(R.color.card_bg),
                        )
                    )
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.buttonColors(containerColor =colorResource(R.color.question)),
                onClick = {
                    onConfirmation(checked)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.buttonColors(containerColor =colorResource(R.color.question)),
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentAppBar(notificationVmFac: NotificationViewModelFactory) {

    val settingsManager = SettingsManager(LocalContext.current)

    val isEnabled = remember { mutableStateOf(settingsManager.areNotificationsEnabled()) }

    val openSettings = remember { mutableStateOf(false) }

    val notificationVm:NotificationViewModel = viewModel(factory = notificationVmFac)

    if (isEnabled.value) {
        ScheduleNotification(notificationVmFac)
    } else {
        notificationVm.toggleNotification(false, 0 ,0)
    }

    if(openSettings.value) {

        NotificationSettings(
            isEnabled = isEnabled.value,
            onDismissRequest = {
            openSettings.value = false
        }, onConfirmation = { it ->
            settingsManager.saveSettings(it)
            isEnabled.value = it
            openSettings.value = false
        })

    }
    TopAppBar(
        title = {
            Text(
                text = "",
                color = Color.White,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = colorResource(R.color.question),
            titleContentColor = colorResource(R.color.question),
            actionIconContentColor = colorResource(R.color.question)
        ),
        actions = {
            IconButton(onClick = {openSettings.value = true}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colorResource(R.color.question)
                )
            }
        },
        modifier = Modifier
            .background(Color.Transparent)
            .zIndex(1f)
    )
}


@Composable
fun ScheduleNotification(notificationVmFac:NotificationViewModelFactory) {
    val notificationVm:NotificationViewModel = viewModel(factory = notificationVmFac)
    CheckAndRequestNotificationPermission({ notificationVm.toggleNotification(true, 21, 0) })
}

@Composable
fun AddJournal(journalVmFactory: JournalViewModelFactory, openDialog:Boolean  = false, qn:String = "") {

    val journalViewModel:JournalViewModel = viewModel(factory = journalVmFactory)


    val openAlertDialog = remember { mutableStateOf(openDialog) }
    if (openAlertDialog.value) {
        AddToJournalForm(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = { question, answer ->
                val time = journalViewModel.getCurrentFormattedDateTime()
                val jc = JournalContent(question = question, answer = answer, time = time)
                journalViewModel.addJournal(jc)
                openAlertDialog.value = false
            },
            qstn = qn
        )
    }

    return FloatingActionButton(
        shape = CircleShape,
        containerColor = colorResource(R.color.card_bg),
        contentColor = colorResource(R.color.answer),
        modifier = Modifier.border(1.dp, color = colorResource(R.color.border),shape = CircleShape),
        onClick = {
            openAlertDialog.value = true
        },
    ) {
        Icon(Icons.Filled.Add, "Add to Journal.")
    }
}

@Composable
fun AddToJournalForm(
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    qstn:String
) {
    val journalQuestions = JournalQuestions()


    val question = remember {
        mutableStateOf(qstn)
    }

    val questionList = journalQuestions.getQuestions()

    val dropDownStatus = remember {
        mutableStateOf(false)
    }

    val maxChar = 150

    val answer = remember { mutableStateOf("") }

    AlertDialog(
        containerColor = colorResource(R.color.card_bg),
        titleContentColor = colorResource(R.color.question),
        textContentColor = colorResource(R.color.answer),
        modifier = Modifier.border(width = 2.dp, color = colorResource(R.color.border), shape = RoundedCornerShape(25.dp)),
        title = {
            Row(modifier = Modifier.clickable {
                dropDownStatus.value = true
            }){
                Text(text = if(question.value != "") question.value else journalQuestions.getRandomQuestion(),
                    textDecoration = TextDecoration.Underline)
            }
            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
            ) {
                DropdownMenu(
                    expanded = dropDownStatus.value,
                    onDismissRequest = {
                        dropDownStatus.value = false
                    },
                    modifier = Modifier.heightIn(min = 50.dp, max = 300.dp)
                        .background(color = colorResource(R.color.dropdown_bg))
                        .border(
                            width = 1.5.dp,
                            color = colorResource(R.color.border),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    questionList.forEach { q ->
                        DropdownMenuItem(
                            onClick = {
                                dropDownStatus.value = false
                                question.value = q
                            },
                            text = { Text(text = q, color = colorResource(R.color.answer)) }
                        )
                    }
                }
            }
        },
        text = {
            TextField(value = answer.value, onValueChange = {
                if(it.length <= maxChar) answer.value = it
            }, label = {Text(text = "Enter your thoughts", color = colorResource(R.color.question))})
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                colors = ButtonDefaults.buttonColors(containerColor =colorResource(R.color.question)),
                onClick = {
                    onConfirmation(question.value, answer.value)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.buttonColors(containerColor =colorResource(R.color.question)),
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}



@Composable
fun JournalList(journalFactory: JournalViewModelFactory, modifier: Modifier = Modifier) {
    val journalViewModel:JournalViewModel = viewModel(factory = journalFactory)
    val journals = journalViewModel.journals.collectAsState().value
    val isLoading = journalViewModel.isLoading.collectAsState().value

    if(isLoading){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }
    } else if(journals.isEmpty()){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    alignment = Alignment.Center,
                    painter = painterResource(id = R.drawable.placeholder),
                    contentDescription = "Placeholder",
                    modifier = Modifier.height(200.dp).width(200.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Let's start adding to this space!",

                    color = colorResource(R.color.question),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(30.dp)
                )
            }
        }
    }
    else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp), modifier = modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(journals) { journal ->
                JournalItem(
                    question = journal.question,
                    answer = journal.answer,
                    time = journal.time
                )
            }
        }
    }
}

@Composable
fun JournalItem(question:String, answer:String, time:String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardColors(
            containerColor = colorResource(R.color.card_bg),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 1.5.dp,
                color = colorResource(R.color.border),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = time,
                fontSize = 20.sp,
                color = colorResource(R.color.time)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = question,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.question)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = answer,
                fontSize = 22.sp,
                color = colorResource(R.color.answer)
            )
        }
    }
}

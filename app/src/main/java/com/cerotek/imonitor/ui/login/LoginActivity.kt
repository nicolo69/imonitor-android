package com.cerotek.imonitor.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.cerotek.imonitor.IMonitorApplication
import com.cerotek.imonitor.network.MockInterceptor
import com.cerotek.imonitor.ui.main.MainActivity
import com.cerotek.imonitor.ui.theme.*
import com.cerotek.imonitor.util.SecurePreferences
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var securePrefs: SecurePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        securePrefs = SecurePreferences(this)
        
        setContent {
            IMonitorTheme {
                LoginScreen(
                    viewModel = viewModel,
                    securePrefs = securePrefs,
                    onLoginSuccess = { navigateToMain() },
                    onShowToast = { message -> 
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
        
        observeViewModel()
        observeViewModel()
        // checkAutoLogin() // Disabilitato login automatico su richiesta utente
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Success -> {
                        // Save credentials
                        val username = viewModel.currentUsername
                        val password = viewModel.currentPassword
                        if (username.isNotBlank() && password.isNotBlank()) {
                            securePrefs.saveCredentials(username, password)
                        }
                        
                        // Save user info
                        val user = state.loginResponse.user
                        securePrefs.saveUserInfo(
                            firstName = user.firstName,
                            lastName = user.lastName,
                            age = user.age,
                            pathology = user.pathology
                        )
                        
                        navigateToMain()
                    }
                    else -> { /* Handled in Compose UI */ }
                }
            }
        }
    }

    private fun checkAutoLogin() {
        val credentials = securePrefs.getCredentials()
        if (credentials != null) {
            viewModel.setCredentials(credentials.first, credentials.second)
            viewModel.login(credentials.first, credentials.second)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    securePrefs: SecurePreferences,
    onLoginSuccess: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var debugTapCount by remember { mutableStateOf(0) }
    var showDebugOptions by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    // Load saved credentials
    // Load saved credentials - RIMOSSO per richiesta utente (vuole inserire lui i dati)
    /*
    LaunchedEffect(Unit) {
        val credentials = securePrefs.getCredentials()
        if (credentials != null) {
            username = credentials.first
            password = credentials.second
        }
    }
    */
    
    // Handle login state
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Error -> {
                onShowToast((loginState as LoginState.Error).message)
            }
            else -> {}
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF87CEEB),  // Sky blue
                        Color(0xFFADD8E6)   // Light blue
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Centra verticalmente il contenuto se c'è spazio
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Logo Cerotek con ombra leggera
            Surface(
                modifier = Modifier
                    .size(130.dp)
                    .clickable {
                        debugTapCount++
                        if (debugTapCount >= 5) {
                            showDebugOptions = !showDebugOptions
                            MockInterceptor.ENABLED = !MockInterceptor.ENABLED
                            val mockStatus = if (MockInterceptor.ENABLED) "ABILITATO" else "DISABILITATO"
                            onShowToast("Debug mode\nMock Server: $mockStatus")
                            debugTapCount = 0
                        }
                    },
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.cerotek.imonitor.R.drawable.ic_logo),
                        contentDescription = "Cerotek Logo",
                        modifier = Modifier
                            .size(90.dp), // Logo leggermente più piccolo nel cerchio bianco
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // BENVENUTO
            Text(
                text = "BENVENUTO",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "INSERISCI LE CREDENZIALI PER ACCEDERE",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Username Field Moderno
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        label = { Text("Username") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFE3F2FD),
                            unfocusedContainerColor = Color(0xFFE3F2FD).copy(alpha = 0.7f),
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Password Field Moderno
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None 
                                              else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility 
                                                 else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF666666)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (username.isNotBlank() && password.isNotBlank()) {
                                    viewModel.login(username, password)
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFE3F2FD),
                            unfocusedContainerColor = Color(0xFFE3F2FD).copy(alpha = 0.7f),
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // ENTRA Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (username.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(username, password)
                            } else {
                                onShowToast("Inserisci username e password")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = loginState !is LoginState.Loading,
                        shape = RoundedCornerShape(16.dp), // Meno rotondo, più moderno
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEB3B),  // Yellow
                            contentColor = Color(0xFF333333)
                        )
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF333333)
                            )
                        } else {
                            Text(
                                "ENTRA", 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // PASSWORD? link
                    TextButton(
                        onClick = { /* Password recovery */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "PASSWORD?",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Debug Options
                    AnimatedVisibility(
                        visible = showDebugOptions,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider()
                            
                            Text(
                                text = "🔧 Debug Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusYellow
                            )
                            
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BackgroundLight
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Mock Server: ${if (MockInterceptor.ENABLED) "ON" else "OFF"}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "Test User: admin",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "Test Pass: password",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            
                            Button(
                                onClick = {
                                    username = "admin"
                                    password = "password"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusYellow
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Usa credenziali test", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Versione 1.2.0",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "© 2026 Cerotek S.r.l.",
                    fontSize = 10.sp,
                    color = Color(0xFF666666).copy(alpha = 0.7f)
                )
            }
        }
    }
}

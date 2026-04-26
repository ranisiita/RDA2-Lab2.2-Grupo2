package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.UserPreferences
import com.example.myapplication.ui.FormViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    val userPrefs = remember { UserPreferences(context) }
                    val viewModel: FormViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                val savedStateHandle = extras.createSavedStateHandle()
                                @Suppress("UNCHECKED_CAST")
                                return FormViewModel(savedStateHandle, userPrefs) as T
                            }
                        }
                    )
                    
                    Column(modifier = Modifier.padding(innerPadding)) {
                        ResilientFormScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ResilientFormScreen(viewModel: FormViewModel) {
    val nameDisk by viewModel.nameFromDisk.observeAsState("")
    var nameInput by remember { mutableStateOf("") }
    
    // Esto hace que cuando DataStore termine de cargar (y nameDisk tenga el valor real), 
    // se lo pasemos a nuestro cuadro de texto.
    androidx.compose.runtime.LaunchedEffect(nameDisk) {
        if (nameDisk.isNotEmpty() && nameInput.isEmpty()) {
            nameInput = nameDisk
        }
    }
// Implementación de Predictive Back (Navegación de Android 16)
    BackHandler(enabled = nameInput.isNotEmpty()) {
// Lógica para interceptar el regreso si hay cambios sin guardar
        Log.d("NAV", "El usuario intentó retroceder con datos en el formulario" )
    }
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Borrador de Perfil", style = MaterialTheme.typography.headlineMedium)
// Campo persistido en DISCO (DataStore)
        OutlinedTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it
                viewModel.saveName(it)
            },
            label = { Text("Nombre") },
            trailingIcon = {
                androidx.compose.animation.AnimatedVisibility(
                    visible = viewModel.showSaveIcon,
                    enter = androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.fadeOut()
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                        contentDescription = "Guardado en disco",
                        tint = androidx.compose.ui.graphics.Color(0xFF4CAF50) // Verde
                    )
                }
            }
        )
// Campo persistido en MEMORIA/PROCESO (SavedStateHandle)
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Email") }
        )
    }
}
package ru.myitschool.work.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.myitschool.work.R
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.nav.AuthScreenDestination
import ru.myitschool.work.ui.nav.MainScreenDestination

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory()),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is AuthAction.NavigateToMain -> {
                    navController.navigate(MainScreenDestination) {
                        popUpTo(AuthScreenDestination) { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(16.dp))

        when (val currentState = state) {
            is AuthState.Data -> {
                AuthContent(
                    viewModel = viewModel,
                    state = currentState,
                    errorMessage = errorMessage
                )
            }
            is AuthState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun AuthContent(
    viewModel: AuthViewModel,
    state: AuthState.Data,
    errorMessage: String?
) {
    var inputText by remember { mutableStateOf(state.inputText) }

    TextField(
        modifier = Modifier.testTag(TestIds.Auth.CODE_INPUT).fillMaxWidth(),
        value = inputText,
        onValueChange = {
            val filtered = it
                .filter { char -> char.isLetterOrDigit() }
                .take(4)
                .uppercase()
            inputText = filtered
            viewModel.onIntent(AuthIntent.TextInput(filtered))
        },
        label = { Text(stringResource(R.string.auth_label)) },
        isError = errorMessage != null,
        supportingText = {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = true
    )
    Spacer(modifier = Modifier.size(16.dp))
    Button(
        modifier = Modifier.testTag(TestIds.Auth.SIGN_BUTTON).fillMaxWidth(),
        onClick = {
            viewModel.onIntent(AuthIntent.Send(inputText))
        },
        enabled = inputText.length == 4 && errorMessage == null
    ) {
        Text(stringResource(R.string.auth_sign_in))
    }
}
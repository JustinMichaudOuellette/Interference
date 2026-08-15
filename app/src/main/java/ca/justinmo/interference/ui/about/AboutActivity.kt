package ca.justinmo.interference.ui.about

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.justinmo.interference.R
import ca.justinmo.interference.ui.theme.JustinTheme

class AboutActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: ""
        val appDescription = intent.getStringExtra(EXTRA_APP_DESCRIPTION) ?: ""
        val initialYear = intent.getIntExtra(EXTRA_INITIAL_YEAR, 2026)

        setContent {
            JustinTheme(dynamicColor = false) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.title_about)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(android.R.string.cancel)
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        AboutScreen(
                            appName = appName,
                            appDescription = appDescription,
                            initialYear = initialYear,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_APP_NAME = "EXTRA_APP_NAME"
        const val EXTRA_APP_DESCRIPTION = "EXTRA_APP_DESCRIPTION"
        const val EXTRA_INITIAL_YEAR = "EXTRA_INITIAL_YEAR"
    }
}

package ca.justinmo.interference

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import ca.justinmo.interference.ui.MainScreen
import ca.justinmo.interference.ui.MainViewModel
import ca.justinmo.interference.ui.about.AboutActivity
import ca.justinmo.interference.ui.theme.JustinTheme

class InterferenceActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        
        setContent {
            JustinTheme {
                MainScreen(
                    viewModel = viewModel,
                    onAboutClick = { launchAbout() }
                )
            }
        }
    }

    private fun launchAbout() {
        val intent = Intent(this, AboutActivity::class.java).apply {
            putExtra(AboutActivity.EXTRA_APP_NAME, getString(R.string.app_name))
            putExtra(AboutActivity.EXTRA_APP_DESCRIPTION, getString(R.string.about_description))
        }
        startActivity(intent)
    }
}

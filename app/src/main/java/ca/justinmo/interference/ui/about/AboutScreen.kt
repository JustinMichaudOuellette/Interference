package ca.justinmo.interference.ui.about

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import ca.justinmo.interference.R
import java.time.Year

@Composable
fun AboutScreen(
  appName: String,
  appDescription: String,
  initialYear: Int,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
  val versionName = packageInfo.versionName
  val currentYear = Year.now().value
  val shareUrl = "https://github.com/JustinMichaudOuellette/Interference"
  val shareMessage = stringResource(R.string.share_message_template, appName, shareUrl)

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = appName,
      style = MaterialTheme.typography.displayMedium,
      fontWeight = FontWeight.Thin,
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center,
    )
    if (versionName != null) {
      Text(
        text = stringResource(R.string.version_label, versionName),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(top = 8.dp),
        textAlign = TextAlign.Center,
      )
    }
    Text(
      text = appDescription,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(top = 32.dp),
      textAlign = TextAlign.Center,
    )

    FilledTonalButton(
      onClick = {
        val sendIntent = Intent().apply {
          action = Intent.ACTION_SEND
          putExtra(Intent.EXTRA_TEXT, shareMessage)
          type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
      },
      modifier = Modifier
        .padding(vertical = 32.dp)
        .widthIn(min = 160.dp),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
      Icon(Icons.Outlined.Share, contentDescription = null)
      Spacer(Modifier.width(8.dp))
      Text(stringResource(R.string.btn_share))
    }

    Text(
      text = buildAnnotatedString {
        append("© $initialYear")
        if (currentYear > initialYear) {
          append(" - $currentYear")
        }
        append(" ")
        withLink(
          LinkAnnotation.Url(
            "https://www.justinmo.ca",
            TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline))
          )
        ) {
          append("Justin Michaud-Ouellette")
        }
      },
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.outline,
      textAlign = TextAlign.Center,
    )
  }
}

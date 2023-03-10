package uticodes.tutorials.hyperlinktextwithtalkbackdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uticodes.tutorials.hyperlinktextwithtalkbackdemo.ui.theme.HyperlinkTextWithTalkBackDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HyperlinkTextWithTalkBackDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val links = mutableMapOf(
                            "Terms" to "https://policies.google.com/terms?hl=en-US",
                            "Privacy Policy" to "https://policies.google.com/privacy?hl=en-US"
                        )
                        LinkedText(
                            Modifier,
                            description = "Continue to view Google Terms and Privacy Policy",
                            linkedText = links,
                            textStyle = TextStyle(textAlign = TextAlign.Center)
                        )
                    }

                }
            }
        }
    }
}

const val TAG_URL = "URL"

@Composable
fun LinkedText(
    modifier: Modifier = Modifier,
    description: String,
    linkedText: Map<String, String>,
    textStyle: TextStyle = TextStyle.Default,
) {
    var openDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    val termsText = linkedText.keys.first()
    val privacyPolicyText = linkedText.keys.last()
    val termsUrl = linkedText.getOrDefault(termsText, 0)
    val privacyPolicyUrl = linkedText.getOrDefault(privacyPolicyText, 1)

    val annotatedString = buildAnnotatedString {
        append(description)

        for (key in linkedText.keys) {
            val startIndex = description.indexOf(key, ignoreCase = true)
            val endIndex = startIndex + key.length
            addStyle(
                SpanStyle(
                    color = Blue,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                ),
                startIndex,
                endIndex
            )

            addStringAnnotation(
                tag = TAG_URL,
                annotation = linkedText[key].toString(),
                start = startIndex,
                end = endIndex
            )
        }
    }

    ClickableText(
        text = annotatedString,
        style = textStyle,
        onClick = {
            annotatedString.getStringAnnotations(TAG_URL, it, it)
                .firstOrNull()?.let { stringAnnotations ->
                    uriHandler.openUri(stringAnnotations.item)
                }
        },
        modifier = modifier
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Continue to click on Google Terms and Privacy Policy",
                        action = { openDialog = true; true }
                    )
                )
            }
    )

    if (openDialog) {

        AlertDialog(
            modifier = Modifier.padding(20.dp),
            onDismissRequest = { openDialog = false },
            title = {
                Text(
                    text = "Google $termsText and $privacyPolicyText",
                    style = MaterialTheme.typography.h6
                )
            },
            text = {
                Column {
                    Text(
                        text = termsText,
                        Modifier
                            .clickable {
                                uriHandler.openUri(termsUrl.toString())
                            },
                        color = Blue,
                        style = MaterialTheme.typography.body1
                    )

                    Text(
                        text = privacyPolicyText,
                        Modifier
                            .padding(
                                top = 20.dp
                            ).clickable {
                            uriHandler.openUri(privacyPolicyUrl.toString())
                        },
                        color = Blue,
                        style = MaterialTheme.typography.body1
                    )

                }

            },
            confirmButton = {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(14.dp)
                        .clickable { openDialog = false }
                )
            }
        )
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HyperlinkTextWithTalkBackDemoTheme {
        Greeting("Android")
    }
}
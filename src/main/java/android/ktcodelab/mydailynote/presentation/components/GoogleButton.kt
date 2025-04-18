package android.ktcodelab.mydailynote.presentation.components

import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.ui.theme.GoogleButtonBG
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    loadingState: Boolean = false,
    primaryText: String = "Sign in with Google",
    secondaryText: String = "Please wait...",
    icon: Int = R.drawable.google_logo,
    shape: Shape = Shapes().extraSmall,
    borderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    backgroundColor: Color = GoogleButtonBG,
    borderStrokeWidth: Dp = 1.dp,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit

) {

    var buttonText by remember {
        mutableStateOf(primaryText)
    }

    LaunchedEffect(key1 = loadingState) {

        buttonText = if (loadingState) secondaryText else primaryText
    }

    Surface(
        modifier = modifier
            .clickable(enabled = !loadingState) {onClick()},
        shape = shape,
        border = BorderStroke(width = borderStrokeWidth, color = borderColor),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Google Logo",
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = buttonText,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize, color = Color.Black)
            )

            if (loadingState) {

                Spacer(modifier = Modifier.width(16.dp))

                CircularProgressIndicator(

                    modifier = Modifier
                        .size(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

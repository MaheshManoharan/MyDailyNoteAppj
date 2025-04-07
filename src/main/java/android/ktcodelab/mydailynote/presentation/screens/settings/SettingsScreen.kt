package android.ktcodelab.mydailynote.presentation.screens.settings

import android.content.Context
import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.pinlock.PinManager
import android.ktcodelab.mydailynote.presentation.components.DisplayAlertDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateToCreatePin: () -> Unit,
    navigateToChangePin: () -> Unit,
    navigateBack:  () -> Unit = {},
) {

    val currentUser = Firebase.auth.currentUser
    //val viewModel: SettingsViewModel = viewModel()
    val context = LocalContext.current

    var dialogOpened by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
           TopAppBar(title = {

               Text(
                   text = stringResource(R.string.settings_text),
                   textAlign = TextAlign.Start,
                   style = MaterialTheme.typography.titleLarge,
                   modifier = Modifier.padding(start = 8.dp)
               )
           },
           navigationIcon = {
               IconButton(
                   onClick = navigateBack,
                   content = {
                       Icon(
                           imageVector = Icons.Default.ArrowBack,
                           contentDescription = "Back Button",
                           tint = MaterialTheme.colorScheme.onSurface
                       )
                   }
               )
           }
         )
        },
        bottomBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp),
                contentAlignment = Alignment.BottomCenter,
                content = {
                    Text(
                        text = "Version ${getCurrentVersionCode(context)}",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                    )
                }
            )
        },
        content = {

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = it),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            AsyncImage(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(shape = CircleShape),
                                model = currentUser?.photoUrl.toString(),
                                placeholder = painterResource(id = R.drawable.ic_avatar),
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentUser?.displayName.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentUser?.email.toString(),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        content = {

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    Icon(
                                        modifier = Modifier.size(30.dp),
                                        painter = painterResource(id = R.drawable.ic_lock),
                                        contentDescription = null,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { navigateToCreatePin() }
                                            .padding(all = 16.dp),
                                        text = stringResource(R.string.create_pin_lock_text),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            )

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {

                                    if (PinManager.pinExists()){
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(id = R.drawable.ic_change_pin),
                                            contentDescription = null,
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { navigateToChangePin() }
                                                .padding(all = 16.dp),
                                            text = stringResource(R.string.change_pin_lock_text),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    else{
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(id = R.drawable.ic_change_pin),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(all = 16.dp),
                                            text = stringResource(R.string.change_pin_lock_text),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                                        )
                                    }

                                }
                            )

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {

                                    if (PinManager.pinExists()){
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(id = R.drawable.ic_disable_pin),
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { dialogOpened = true }
                                                .padding(all = 16.dp),
                                            text = stringResource(R.string.disable_pin_lock_text),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    else {
                                        Icon(
                                            modifier = Modifier.size(30.dp),
                                            painter = painterResource(id = R.drawable.ic_disable_pin),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(all = 16.dp),
                                            text = stringResource(R.string.disable_pin_lock_text),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                                        )
                                    }

                                }
                            )


                        }
                    )
                }
            )

        }
    )

    DisplayAlertDialog(
        title = context.getString(R.string.disable_pin_lock_text),
        message = context.getString(R.string.disable_pin_lock_message_text),
        dialogOpened = dialogOpened,
        onCloseDialog = { dialogOpened = false },
        onYesClicked = {
            PinManager.clearPin()
            //viewModel.clearPinToFirebase()
            Toast.makeText(context, "Pin Disabled", Toast.LENGTH_SHORT).show()
        }
    )
}

@Suppress("DEPRECATION")
private fun getCurrentVersionCode(context: Context): String {

    val packageManager = context.packageManager
    val packageInfo = packageManager.getPackageInfo(context.packageName, 0)

    return packageInfo.versionName
}
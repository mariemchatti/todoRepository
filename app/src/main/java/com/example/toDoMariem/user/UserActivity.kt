package com.example.toDoMariem.user

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.toDoMariem.data.Api
import com.example.toDoMariem.data.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.KFunction1

class UserActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userViewModel: UserViewModel = viewModel()
            userViewModel.fetchUser()
            UserContent(userViewModel, this::pickPhotoWithPermission)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pickPhotoWithPermission(photoPickerLauncher: () -> Unit) {
        val storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        when {
            ContextCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_GRANTED ->
                photoPickerLauncher()
            shouldShowRequestPermissionRationale(storagePermission) ->
                showMessage("Permission needed to pick photos.")
            else ->
                requestPermissionLauncher.launch(storagePermission)
        }
    }


    private fun showMessage(message: String) {
        // Utilisez un Snackbar ou une autre méthode pour afficher un message
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
        } else {
        }
    }
}

@Composable
fun UserContent(userViewModel: UserViewModel, onPickPhoto: KFunction1<() -> Unit, Unit>) {

    val userWebService = Api.userWebService
    val viewModelScope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureUri by lazy {
        val contentValues = ContentValues()
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureUri?.let { uri ->
                val multipartImage = uri.toRequestBody(context.contentResolver)
                viewModelScope.launch {
                    try {
                        userWebService.updateAvatar(multipartImage)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val multipartImage = selectedUri.toRequestBody(context.contentResolver)
            viewModelScope.launch {
                try {
                    val response = userWebService.updateAvatar(multipartImage)
                } catch (e: Exception) {
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            photoPicker.launch("image/*")
        } else {
        }
    }

    val user by userViewModel.userStateFlow.collectAsState()
    var newName by remember { mutableStateOf("") }

    Column {
        Text(text = "Nom actuel : ${user?.name ?: "Non défini"}")

        // Champ pour modifier le nom
        TextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Modifier le nom") }
        )
        Button(onClick = {
            userViewModel.updateUserName(newName)
        }) {
            Text("Mettre à jour le nom")
        }

        Button(onClick = { takePicture.launch(captureUri) }) {
            Text("Take Picture")
        }
        Button(onClick = { onPickPhoto { photoPicker.launch("image/*") } }) {
            Text("Pick Photo")
        }
    }
}

private fun Bitmap.toRequestBody(): MultipartBody.Part {
    val tmpFile = File.createTempFile("avatar", "jpg")
    tmpFile.outputStream().use {
        this.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.jpg",
        body = tmpFile.readBytes().toRequestBody()
    )
}

private fun Uri.toRequestBody(contentResolver: ContentResolver): MultipartBody.Part {
    val fileInputStream = contentResolver.openInputStream(this) ?: throw FileNotFoundException("File not found")
    val fileBody = fileInputStream.readBytes().toRequestBody()
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.jpg",
        body = fileBody
    )
}

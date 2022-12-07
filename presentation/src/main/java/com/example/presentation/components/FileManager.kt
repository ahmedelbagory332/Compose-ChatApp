package com.example.presentation.components

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.domain.utils.states.UserState
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import com.example.presentation.utiles.FilePath
import com.example.presentation.utiles.initRecorder
import com.example.presentation.utiles.root
import com.example.presentation.utiles.sendVoiceMessage
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

fun createUriToTakePictureByCamera(
    applicationContext: Application,
    chatPageViewModel: ChatPageViewModel
): Uri {
    //gets the temp_images dir
    val tempImagesDir = File(
        applicationContext.filesDir, //this function gets the external cache dir
        "temp_images"
    ) //gets the directory for the temporary images dir
    tempImagesDir.mkdir() //Create the temp_images dir

    val tempImage = File(
        tempImagesDir, //prefix the new abstract path with the temporary images dir path
        chatPageViewModel.cameraImageName.value
    )
    //Returns the Uri object to be used with ActivityResultLauncher
    return FileProvider.getUriForFile(
        applicationContext,
        "${applicationContext.packageName}.provider", tempImage
    )
}


fun deleteRecursive(fileOrDirectory: File) {
    if (fileOrDirectory.isDirectory) {
        for (child in fileOrDirectory.listFiles()!!) {
            deleteRecursive(child)
        }
    }
    fileOrDirectory.delete()
}


@Composable
fun prepareCameraAndUploadPicture(
    applicationViewModel: ApplicationViewModel,
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState,
): ManagedActivityResultLauncher<Uri, Boolean> {
    val coroutineScope = rememberCoroutineScope()

    val imageCropLauncher =
        imageCropper(coroutineScope, applicationViewModel, chatPageViewModel, authViewModel, state)

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
            if (it) {
                val cropOptions = CropImageContractOptions(
                    createUriToTakePictureByCamera(
                        applicationViewModel.application,
                        chatPageViewModel
                    ),
                    CropImageOptions()
                )
                imageCropLauncher.launch(cropOptions)
            }

        }
    return imagePickerLauncher
}

@Composable
fun fileAttachPicker(
    applicationViewModel: ApplicationViewModel,
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState,
): ManagedActivityResultLauncher<String, Uri?> {
    val coroutineScope = rememberCoroutineScope()

    val imageCropLauncher =
        imageCropper(coroutineScope, applicationViewModel, chatPageViewModel, authViewModel, state)


    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { fileUri->
            val fileType: String = applicationViewModel.application.contentResolver.getType(fileUri!!)!!
            if(fileType.contains("image/jpg")||fileType.contains("image/jpeg")||fileType.contains("image/png")) {
                //photo
                val cropOptions = CropImageContractOptions(fileUri, CropImageOptions())
                imageCropLauncher.launch(cropOptions)
            }
            else if (fileType.contains("video/mp4")||fileType.contains("video/3gp")) {
                //video

                chatPageViewModel.sendFile(
                    messageSenderId = authViewModel.getCurrentUser()!!.uid,
                    messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                    messageReceiverId = state.user[0].userId.toString(),
                    messageReceiverName = state.user[0].name.toString(),
                    fileType = "video",
                    fileUri = fileUri,
                )


            }
            else if (fileType.contains("officedocument")){
                chatPageViewModel.sendFile(
                    messageSenderId = authViewModel.getCurrentUser()!!.uid,
                    messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                    messageReceiverId = state.user[0].userId.toString(),
                    messageReceiverName = state.user[0].name.toString(),
                    fileType = "document",
                    fileUri = fileUri,
                )

            }
            else if (fileType.contains("application/pdf")){
                // pdf file
                chatPageViewModel.sendFile(
                    messageSenderId = authViewModel.getCurrentUser()!!.uid,
                    messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                    messageReceiverId = state.user[0].userId.toString(),
                    messageReceiverName = state.user[0].name.toString(),
                    fileType = "pdf",
                    fileUri = fileUri,
                )
            }
            else if (fileType.contains("audio")){
                // audio file

                chatPageViewModel.sendFile(
                    messageSenderId = authViewModel.getCurrentUser()!!.uid,
                    messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                    messageReceiverId = state.user[0].userId.toString(),
                    messageReceiverName = state.user[0].name.toString(),
                    fileType = "audio",
                    fileUri = fileUri,
                )
            }
            else if (fileType.contains("gif")){
                // gif file

                chatPageViewModel.sendFile(
                    messageSenderId = authViewModel.getCurrentUser()!!.uid,
                    messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                    messageReceiverId = state.user[0].userId.toString(),
                    messageReceiverName = state.user[0].name.toString(),
                    fileType = "gif",
                    fileUri = fileUri,
                )
            }
            else{
                Log.d("TBEGO", "onActivityResult: $fileType ")

            }
        }
    return filePickerLauncher
}

@Composable
fun imageCropper(
    coroutineScope: CoroutineScope,
    applicationViewModel: ApplicationViewModel,
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState
): ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult> {
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the cropped image
            coroutineScope.launch {
//                val compressedImageFile = Compressor.compress(applicationViewModel.application, File("${applicationViewModel.application.filesDir}/temp_images/${chatPageViewModel.cameraImageName.value}"))
                val compressedImageFile = Compressor.compress(
                    applicationViewModel.application,
                    File(result.getUriFilePath(applicationViewModel.application, true).toString())
                )

                chatPageViewModel.sendFile(
                    messageSenderId = authViewModel.getCurrentUser()!!.uid,
                    messageReceiverId = state.user[0].userId.toString(),
                    messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
                    messageReceiverName = state.user[0].name.toString(),
                    fileType = "image",
                    fileUri = FilePath.getUri(compressedImageFile),
                )

                deleteRecursive(File("${applicationViewModel.application.filesDir}/temp_images"))
                deleteRecursive(File("${applicationViewModel.application.cacheDir}/compressor"))
                deleteRecursive(
                    File(
                        result.getUriFilePath(applicationViewModel.application, true).toString()
                    )
                )
            }

        } else {
            // an error occurred cropping
            val exception = result.error
            Log.d("TAG", "ImageSelectorAndCropper:${exception} ")

        }
    }
    return imageCropLauncher
}

@Composable
 fun voiceRecorderPermissions(
    applicationViewModel: ApplicationViewModel,
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    val voiceRecorderPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }

        if (isGranted) {

            initRecorder(applicationViewModel)
            sendVoiceMessage(
                applicationViewModel,
                root,
                chatPageViewModel,
                authViewModel,
                state
            )

        } else {

            Toast.makeText(
                applicationViewModel.application,
                "PERMISSION DENIED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    return voiceRecorderPermissionLauncher
}

@Composable
 fun filePickerPermission(
    filePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    applicationViewModel: ApplicationViewModel
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    val filePickerPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }

        if (isGranted) {

            filePickerLauncher.launch("*/*")

        } else {

            Toast.makeText(
                applicationViewModel.application,
                "PERMISSION DENIED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    return filePickerPermissionLauncher
}

@Composable
 fun takePicturePermission(
    takePictureLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    applicationViewModel: ApplicationViewModel,
    chatPageViewModel: ChatPageViewModel
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    val takePicturePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }

        if (isGranted) {

            takePictureLauncher.launch(
                createUriToTakePictureByCamera(
                    applicationViewModel.application,
                    chatPageViewModel
                )
            )
        } else {

            Toast.makeText(
                applicationViewModel.application,
                "PERMISSION DENIED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    return takePicturePermissionLauncher
}

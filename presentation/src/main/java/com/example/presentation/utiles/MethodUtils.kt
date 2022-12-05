package com.example.presentation.utiles

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import com.example.domain.model.ChatModel
import com.example.domain.utils.states.UserState
import com.example.presentation.screens.*
import com.example.presentation.ui.view_models.ApplicationViewModel
import com.example.presentation.ui.view_models.AuthViewModel
import com.example.presentation.ui.view_models.ChatPageViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



var myAudioRecorder: MediaRecorder? =  null
 var minVal = "0"
 var maxVal = "0"
 val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
 val mediaPlayer:MediaPlayer = MediaPlayer()
 val myDir = File("$root/Bego Chat/Bego Recorders")


  fun initRecorder(applicationViewModel: ApplicationViewModel) {
    if (myAudioRecorder == null) {
        myAudioRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(applicationViewModel.application)
        } else {
            MediaRecorder()
        }
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
    }
}


 fun sendVoiceMessage(

    applicationViewModel: ApplicationViewModel,
    root: String,
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState
) {


    val myDir = File("$root/Bego Chat/Bego Recorders")
    if (!myDir.exists())
        myDir.mkdirs()

    if (chatPageViewModel.isNewRecord.value) {
        /* isNewRecord
                                    // عشان لما الميثود تستدعي للمره التانيه عشان نوقف التسجيل متغيرش اسم التسجيل لاسم جديد فيحصل لغبطه لما يجي يترفع
                                    و نغير القيمه دي لما التسجيل يترفع عشان لما نيجى نرفع واحد جديد
                                    */

        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ROOT)
        val now: Date = Date()
        chatPageViewModel.recordFileName.value = "Recorder ${formatter.format(now)}.3gp" //file name
    }
    myAudioRecorder!!.setOutputFile("$root/Bego Chat/Bego Recorders/${chatPageViewModel.recordFileName.value}")
    if (chatPageViewModel.isNewRecord.value) {
        chatPageViewModel.showTimer.value = true
        chatPageViewModel.showIcons.value = false
        chatPageViewModel.isRecording.value = false
        chatPageViewModel.isNewRecord.value = false
        try {
            myAudioRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        myAudioRecorder!!.start()
        Toast.makeText(applicationViewModel.application, "Recording started", Toast.LENGTH_LONG)
            .show()

    } else {

        chatPageViewModel.showIcons.value = true
        chatPageViewModel.showTimer.value = false
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        Toast.makeText(
            applicationViewModel.application,
            "sending Audio Recorder....",
            Toast.LENGTH_LONG
        ).show()
        chatPageViewModel.sendFile(
            messageSenderId = authViewModel.getCurrentUser()!!.uid,
            messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
            messageReceiverId = state.user[0].userId.toString(),
            messageReceiverName = state.user[0].name.toString(),
            fileType = "record",
            fileUri = Uri.fromFile(File("$root/Bego Chat/Bego Recorders/${chatPageViewModel.recordFileName.value}")),
        )

        chatPageViewModel.resetMediaRecorder()

    }
}

 fun sendTextMessage(
    chatPageViewModel: ChatPageViewModel,
    authViewModel: AuthViewModel,
    state: UserState
) {
    chatPageViewModel.sendMessage(
        messageSenderId = authViewModel.getCurrentUser()!!.uid,
        messageSenderName = authViewModel.getCurrentUser()!!.displayName.toString(),
        messageReceiverId = state.user[0].userId.toString(),
        messageReceiverName = state.user[0].name.toString(),
        messageText = chatPageViewModel.textMessage.value.text
    )
    chatPageViewModel.textMessage.value = TextFieldValue("")
}

fun voiceMessage(
    list: Array<out File>?,
    messages: ChatModel,
    index: Int,
    chatPageViewModel: ChatPageViewModel,
    applicationViewModel: ApplicationViewModel
) {
    if (list!!.contains(File("$root/Bego Chat/Bego Recorders/${messages.messageText}"))) {

        //file already downloaded
        playVoiceMessage(
            "$root/Bego Chat/Bego Recorders/${messages.messageText}",
            mediaPlayer,
            index,
            chatPageViewModel,
        )

    } else {
        // Create request for android download manager
        val downloadManager =
            applicationViewModel.application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(messages.messageFile))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

        // set title
        request.setTitle("Downloading...")

        //set the local destination for download file to a path within the application's external files directory
        request.setDestinationUri(Uri.fromFile(File(myDir, messages.messageText.toString())))
        request.setMimeType("*/*")
        downloadManager.enqueue(request)
        playVoiceMessage(
            messages.messageFile!!,
            mediaPlayer,
            index,
            chatPageViewModel,
        )


    }
}


fun playVoiceMessage(
    file: String,
    mediaPlayer: MediaPlayer,
    newPosition: Int,
    chatPageViewModel: ChatPageViewModel,
) {

    if ( chatPageViewModel.isRecordClicked.value) {

        if ( chatPageViewModel.isPaused.value) {
            if ( chatPageViewModel.currentVoiceMessagePosition.value == newPosition) {
                // this mean we clicked on same record
                mediaPlayer.seekTo(chatPageViewModel.length.value) // in case we paused record and resumed it again
            }
            else {
                chatPageViewModel.sliderPosition.value = 0f
                mediaPlayer.reset()
                mediaPlayer.setDataSource(file)
                mediaPlayer.prepare()
                chatPageViewModel.oldVoiceMessagePosition.value = newPosition
            }

        } else {

            mediaPlayer.setDataSource(file)
            mediaPlayer.prepare()
            chatPageViewModel.oldVoiceMessagePosition.value = newPosition
        }

        initMediaPlayer(mediaPlayer, chatPageViewModel)

    }
    else {
        if(chatPageViewModel.oldVoiceMessagePosition.value != newPosition) {
            // if user play another voice message and there is one already play
            if (mediaPlayer.isPlaying) {

                mediaPlayer.pause()
                chatPageViewModel.sliderPosition.value = 0f
                mediaPlayer.reset()
                mediaPlayer.setDataSource(file)
                mediaPlayer.prepare()
                chatPageViewModel.oldVoiceMessagePosition.value = newPosition
                initMediaPlayer(mediaPlayer, chatPageViewModel)
            }

        }
        else{
            mediaPlayer.pause()
            chatPageViewModel.length.value = mediaPlayer.currentPosition
            chatPageViewModel.isRecordClicked.value = true
            chatPageViewModel.isPaused.value = true
            chatPageViewModel.currentVoiceMessagePosition.value = newPosition
        }
    }



}

fun initMediaPlayer(
    mediaPlayer: MediaPlayer,
    chatPageViewModel: ChatPageViewModel
) {
    mediaPlayer.setOnCompletionListener {
        it.reset()
        chatPageViewModel.isPaused.value = false
        chatPageViewModel.isRecordClicked.value = true
        chatPageViewModel.selectedIndex.value = -1
    }


    minVal = timerConversion(mediaPlayer.currentPosition.toFloat().toLong()).toString()
    maxVal = mediaPlayer.duration.toString()
    val handler = Handler(Looper.getMainLooper())

    val runnable: Runnable = object : Runnable {
        override fun run() {
            try {
                minVal = timerConversion(mediaPlayer.currentPosition.toFloat().toLong()).toString()
                chatPageViewModel.sliderPosition.value = mediaPlayer.currentPosition.toFloat()
                handler.postDelayed(this, 500)
            } catch (ed: IllegalStateException) {
                ed.printStackTrace()
            }
        }
    }
    handler.postDelayed(runnable, 500)


    mediaPlayer.start()
    chatPageViewModel.isRecordClicked.value = false
}

fun getDuration(filePath:String,context: Application): String {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(context, Uri.parse(filePath))
    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    return timerConversion(durationStr!!.toLong())
}



fun timerConversion(value: Long): String {
    val audioTime: String
    val dur = value.toInt()
    val hrs = dur / 3600000
    val mns = dur / 60000 % 60000
    val scs = dur % 60000 / 1000
    audioTime = if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mns, scs)
    } else {
        String.format("%02d:%02d", mns, scs)
    }
    return audioTime
}
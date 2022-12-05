package com.example.presentation.utiles


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.presentation.R
import com.google.firebase.storage.UploadTask
import java.util.*
import javax.inject.Inject


class MyNotificationManager  constructor(private val mCtx: Application) {

     fun createUploadMediaNotification(progress: UploadTask.TaskSnapshot?, isFinished:Boolean) {

         val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
         val notificationManager =  mCtx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val notificationChannel = NotificationChannel(
                 "Channel_id_default", "Channel_name_default", NotificationManager.IMPORTANCE_HIGH
             )
             val attributes = AudioAttributes.Builder()
                 .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                 .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                 .build()

             notificationChannel.description = "Channel_description_default"
             notificationChannel.enableLights(true)
             notificationChannel.enableVibration(true)
             notificationChannel.setSound(soundUri, attributes)
             notificationManager.createNotificationChannel(notificationChannel)
         }
         val notificationBuilder = NotificationCompat.Builder(mCtx, "Channel_id_default")

         if(isFinished){

             notificationBuilder
                 .setWhen(System.currentTimeMillis())
                 .setContentTitle("Uploaded")
                 .setContentText("Upload finished")
                 .setProgress(0, 0, false)
                 .setAutoCancel(true)
                 .setOngoing(false)
                 .setSmallIcon(R.drawable.ic_launcher_foreground)
                 .setTicker(mCtx.resources.getString(R.string.app_name))
                 .setPriority(NotificationCompat.PRIORITY_HIGH)
                 .setSound(soundUri)

             notificationManager.notify(101, notificationBuilder.build())


         }else{
             notificationBuilder
                 .setWhen(System.currentTimeMillis())
                 .setSmallIcon(R.drawable.ic_launcher_background)
                 .setTicker(mCtx.resources.getString(R.string.app_name))
                 .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                 .setContentTitle("Uploading...")
                 .setContentText("Upload in progress")
                 .setAutoCancel(true)
                 .setOngoing(true)
                 .setOnlyAlertOnce(true)
                 .setPriority(NotificationCompat.PRIORITY_HIGH)
                 .setProgress(progress!!.totalByteCount.toInt(), 0, true)
             notificationManager.notify(101, notificationBuilder.build())

             notificationBuilder.setProgress(progress.totalByteCount.toInt(), progress.bytesTransferred.toInt(), false)
                 .setAutoCancel(false)
             notificationManager.notify(101, notificationBuilder.build())
         }



    }

}
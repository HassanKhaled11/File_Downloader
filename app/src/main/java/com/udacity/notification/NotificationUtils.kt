package com.udacity.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.udacity.DetailActivity
import com.udacity.R
import com.udacity.downloadOption.Details

class NotificationUtils(private val context:Context){

    companion object{
        const val CHANNEL_ID  = "mychannel"
        const val REQUEST_CODE  = 0
    }

    init {
        createChannel()
    }
    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    CHANNEL_ID, "channel_1", NotificationManager.IMPORTANCE_LOW
                )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "The File is Dowloaded"

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)

        } else {
            Toast.makeText(context, "low Version", Toast.LENGTH_LONG).show()
        }

    }

    fun sendNotification(context: Context, messageBody: String, details: Details?){


        val intent = Intent(context,DetailActivity::class.java).putExtra("mykey",details)
        val contentPendingIntent = PendingIntent.getActivity(context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val action = NotificationCompat.Action.Builder(null,
            "Show File",
            contentPendingIntent).build()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            .setContentTitle("File Downloaded")
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .addAction(action)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()


        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1,notification)

    }

    fun NotificationManager.cancelNotifications(){
        cancelAll()
    }

}



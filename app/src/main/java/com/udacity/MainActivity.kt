package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
//import com.udacity.databinding.ActivityMainBinding
import com.udacity.downloadOption.Details
import com.udacity.downloadOption.DownloadOption
import com.udacity.notification.NotificationUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var  button:LoadingButton
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

          val service = NotificationUtils(applicationContext)

              button = findViewById(R.id.loadingButton)
               button.setOnClickListener{
               val option = getDownLoadOption()
                if (option != null)
                {
                    binding.loadingButton.startAnimation()
                    try {
                        download(getUrl(option),getTitle(option))

                        }catch (e:Exception){

                        Log.i("MainActivity" , "faild downloading" )

                        }
                }
                else
                {
                    Toast.makeText(this,"No item Selected",Toast.LENGTH_LONG).show()

                }
        }
   }

    private fun getTitle(option: DownloadOption): String{

        return when (option) {
            DownloadOption.Glide -> resources.getString(R.string.GlideImage)
            DownloadOption.Repository -> resources.getString(R.string.LoadingRepo)
            DownloadOption.Retrofit -> resources.getString(R.string.RetrofitClient)
            else -> {""}
        }

    }

    private fun getUrl(option: DownloadOption) : String{

       return when (option) {
           DownloadOption.Glide ->"https://github.com/bumptech/glide"
           DownloadOption.Repository ->"https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
           DownloadOption.Retrofit ->"https://github.com/square/retrofit"
           else -> {""}
       }
    }

    private fun getDownLoadOption(): DownloadOption? {
        val radio:RadioGroup = findViewById(R.id.radio_group)

       return when(radio.checkedRadioButtonId){
           R.id.glide -> DownloadOption.Glide
           R.id.repository -> DownloadOption.Repository
           R.id.retrofit -> DownloadOption.Retrofit
           else -> null
       }

     }
    @SuppressLint("Range")
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var service = NotificationUtils(applicationContext)
            var details : Details? = null

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID ){
                val q  = DownloadManager.Query()
                q.setFilterById(id)
                val downloadManger = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor:Cursor = downloadManger.query(q)
                if (cursor.moveToFirst()){

                    button.stopAnimation()

                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val title  = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))

                    if (status == DownloadManager.STATUS_SUCCESSFUL){
                        details = Details(title,"Successful")
                    }
                    if (status == DownloadManager.STATUS_FAILED){
                        details = Details(title,"Faild")
                    }

                    service.sendNotification(applicationContext,"The File is downloaded",details)

                }
            }
        }
    }

    private fun download(URL: String, title: String) {

        val file = File(getExternalFilesDir(null), "/dowloandedfiles")

        if (!file.exists()) {
            file.mkdirs()
        }
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/downloadedfiles/file")

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)    // enqueue puts the download request in the queue.
    }



}

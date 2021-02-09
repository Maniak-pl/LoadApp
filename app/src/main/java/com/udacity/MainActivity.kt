package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.util.Constants.URL_GLIDE
import com.udacity.util.Constants.URL_RETROFIT
import com.udacity.util.Constants.URL_UDACITY
import com.udacity.util.toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        initListeners()
    }

    private fun initListeners() {
        downloadBtn.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.optionGlide -> download(URL_GLIDE)
                R.id.optionLoadApp -> download(URL_UDACITY)
                R.id.optionRetrofit -> download(URL_RETROFIT)
                else -> download(null)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent.action

            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val manager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                downloadBtn.setState(ButtonState.Completed)
                            } else {
                                downloadBtn.setState(ButtonState.Completed)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download(url: String?) {
        downloadBtn.setState(ButtonState.Clicked)
        if (url != null) {
            downloadBtn.setState(ButtonState.Loading)
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            downloadBtn.setState(ButtonState.Completed)
            toast(R.string.not_choose)
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

}

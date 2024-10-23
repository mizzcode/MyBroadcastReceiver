package com.misbah.mybroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.misbah.mybroadcastreceiver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var binding: ActivityMainBinding? = null
    companion object {
        const val ACTION_DOWNLOAD_STATUS = "download_status"
    }
    private lateinit var downloadReceiver: BroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding?.root)

        binding?.btnPermission?.setOnClickListener(this)
        binding?.btnDownload?.setOnClickListener(this)

        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(context, "Download Selesai", Toast.LENGTH_SHORT).show()
            }
        }

        val downloadIntentFilter = IntentFilter(ACTION_DOWNLOAD_STATUS)
        registerReceiver(downloadReceiver, downloadIntentFilter, RECEIVER_NOT_EXPORTED)
    }

    private var requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_permission -> requestPermissionLauncher.launch(android.Manifest.permission.RECEIVE_SMS)
            R.id.btn_download -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    val notifyFinishIntent = Intent().setAction(ACTION_DOWNLOAD_STATUS)
                    sendBroadcast(notifyFinishIntent)
                }, 3000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
        binding = null
    }
}
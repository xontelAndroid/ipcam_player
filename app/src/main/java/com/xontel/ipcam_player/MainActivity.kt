package com.xontel.ipcam_player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.xontel.ipcam_player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.hik.setOnClickListener {
            startActivity(Intent(this, HikActivity::class.java))
        }
        binding.dah.setOnClickListener {
            startActivity(Intent(this, DahActivity::class.java))
        }

    }
}
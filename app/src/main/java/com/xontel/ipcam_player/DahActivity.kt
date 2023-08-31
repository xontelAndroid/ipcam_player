package com.xontel.ipcam_player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.xontel.ipcam_player.dahua.DahuaPlayer
import com.xontel.ipcam_player.dahua.DahuaUtil
import com.xontel.ipcam_player.databinding.ActivityDahBinding
import com.xontel.ipcam_player.databinding.ActivityHikBinding
import com.xontel.ipcam_player.hikvision.HIKPlayer
import com.xontel.ipcam_player.hikvision.HikUtil

class DahActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDahBinding ;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dah)
        val player = DahuaPlayer(this, false)
        val logId = DahuaUtil.loginNormalDevice(this, CamDevice(
            "hik",
            "egycam.xontel.net",
            "admin",
            "X0n954321",
            CamDeviceType.DAHUA.value
        ))
        binding.playerView.init()
        player.attachView(binding.playerView, IpCam(
            0,
            1,
            "ddd",
            CamDeviceType.DAHUA.value,
            logId,
            true
        ), false
        )
    }
}
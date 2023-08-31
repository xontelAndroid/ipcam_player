package com.xontel.ipcam_player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.xontel.ipcam_player.databinding.ActivityHikBinding
import com.xontel.ipcam_player.hikvision.CamPlayerView
import com.xontel.ipcam_player.hikvision.HIKPlayer
import com.xontel.ipcam_player.hikvision.HikUtil

class HikActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHikBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hik)
        val player = HIKPlayer(this, false)
        val logId = HikUtil.loginNormalDevice(this, CamDevice(
            "hik",
            "egycam.xontel.net",
            "admin",
            "X0nPAssw0rd_000",
            CamDeviceType.HIKVISION.value
        ))
        binding.playerView.init()
        player.attachView(binding.playerView, IpCam(
            2,
            1,
            "ddd",
            CamDeviceType.HIKVISION.value,
            logId.toLong(),
            true
        ), false
        )
    }
}
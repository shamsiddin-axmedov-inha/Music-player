package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicplayer.models.formatDuration

class MusicService: Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService{
            return this@MusicService
        }
    }

    fun showNotification(){
        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPa[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPa[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.music_player_icon_splash_screen))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_baseline_chevron_left_24, "Previous", null)
            .addAction(R.drawable.play_icon, "Play", null)
            .addAction(R.drawable.ic_baseline_chevron_right_24, "Next", null)
            .addAction(R.drawable.ic_baseline_exit_to_app_24, "Exit", null)
            .build()

        startForeground(13, notification)
    }

    fun seekBarSetUp(){
        runnable = Runnable {
            PlayerActivity.binding.startTvSeekbar.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPlayActivity.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}
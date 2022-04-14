package com.example.musicplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.formatDuration

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    companion object {
        lateinit var musicListPa: ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for starting Service
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        initializeLayout()

        binding.back.setOnClickListener{
            finish()
        }
        binding.playPauseBtn.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.previousBtn.setOnClickListener {
            prevNextSong(increment = false)
        }
        binding.nextBtn.setOnClickListener {
            prevNextSong(increment = true)
        }

        binding.seekBarPlayActivity.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                musicListPa = ArrayList()
                musicListPa.addAll(MainActivity.MusicListMA)
                musicListPa.shuffle()
                setLayout()
            }
        }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(musicListPa[songPosition].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_splash_screen)
                    .centerCrop()
            )
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPa[songPosition].title
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPa[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.ic_baseline_pause_24)
            binding.startTvSeekbar.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.finishTvSeekbar.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPlayActivity.progress = 0
            binding.seekBarPlayActivity.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        binding.playPauseBtn.setIconResource(R.drawable.ic_baseline_pause_24)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.playPauseBtn.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicListPa.size - 1 == songPosition) {
                songPosition = 0
            } else {
                ++songPosition
            }
        } else {
            if (0 == songPosition) {
                songPosition = musicListPa.size - 1
            } else {
                --songPosition
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetUp()
//        musicService!!.showNotification()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }


}

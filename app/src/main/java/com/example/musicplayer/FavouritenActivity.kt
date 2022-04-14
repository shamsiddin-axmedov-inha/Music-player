package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityFavouritenBinding

class FavouritenActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavouritenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFavouritenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
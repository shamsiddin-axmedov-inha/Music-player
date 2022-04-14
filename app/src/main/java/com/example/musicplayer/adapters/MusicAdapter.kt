package com.example.musicplayer.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.MusicViewBinding
import com.example.musicplayer.models.Music
import com.example.musicplayer.models.formatDuration

class MusicAdapter(private val context: Context, private val musicList: ArrayList<Music>): RecyclerView.Adapter<MusicAdapter.Vh>() {

    inner class Vh(binding: MusicViewBinding): RecyclerView.ViewHolder(binding.root){
        val title = binding.songName
        val album = binding.songAlbumMv
        val image = binding.imageMusicView
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_splash_screen).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener{
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int = musicList.size
}
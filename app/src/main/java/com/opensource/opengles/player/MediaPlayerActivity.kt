package com.opensource.opengles.player

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.opensource.opengles.R
import kotlinx.android.synthetic.main.media_player.*

class MediaPlayerActivity: AppCompatActivity() {

    lateinit var mPlayer: MediaPlayer
    private val videoUrl:String = "/sdcard/2.mp4"
    //------------------ 普通视频渲染播放 ----------
    lateinit var holder: SurfaceHolder
    //-------------------- opengl 渲染相关 -----------
    protected var renderer: MediaGLRenderer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.media_player)
        mPlayer = MediaPlayer()
        setupViews()
        initViews()
    }

    private fun setupViews() {
        gv_surface.setEGLContextClientVersion(3)
    }

    private fun initViews() {
        btn_play.setOnClickListener {
            startPlay()
        }
        btn_glplay.setOnClickListener {

        }
    }

    private fun startPlay(){
        mPlayer.reset()
        mPlayer.setDataSource(videoUrl)
        //安卓6.0以后
        if(Build.VERSION.SDK_INT >=23){
            //配置播放器
            var aa = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build();
            mPlayer.setAudioAttributes(aa);
        }else{
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        holder = sv_surface.holder
        mPlayer.setDisplay(holder)
        mPlayer.prepare()
        mPlayer.start()

    }



}
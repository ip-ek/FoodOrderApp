package com.ipk.foodorderapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_splash_screen.*
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            setContentView(R.layout.activity_splash_screen)
            try {
                videoView.setVideoPath("android.resource://" + packageName + "/raw/splash_screen") //set the path of the video that we need to use in our VideoView
                videoView.setOnCompletionListener {
                    val r = object:Runnable{
                        override fun run() {
                            startActivity(Intent(this@SplashScreenActivity,MainActivity::class.java))
                            finish()
                        }
                    }
                    Handler().postDelayed(r,0) //delay to starting new activity
                }
                videoView.start()

            }catch (e: Exception){
                e.printStackTrace()
                startActivity(Intent(this@SplashScreenActivity,MainActivity::class.java))
                finish()
            }
        }else{
            setContentView(R.layout.splash_screen_sdk)
            val background = object : Thread() {
                override fun run() {
                    try {
                        sleep(5000)
                    }catch (e : Exception){
                        e.printStackTrace()
                    }finally {
                        startActivity(Intent(this@SplashScreenActivity,MainActivity::class.java))
                        finish()
                    }
                }
            }
            background.start()
        }
    }
}
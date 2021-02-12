package com.ipk.foodorderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_splash_screen.*
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        try {
            //set time in mili
            //Thread.sleep(5000);

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
            e.printStackTrace();
            startActivity(Intent(this@SplashScreenActivity,MainActivity::class.java))
            finish()
        }


    }
}
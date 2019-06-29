package com.heng.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_slide_puzzle_captcha).setOnClickListener {
            startActivity(Intent(this, SlidePuzzleCaptchaActivity::class.java))
        }
        findViewById<View>(R.id.btn_text_captcha).setOnClickListener {
            startActivity(Intent(this, TextCaptchaActivity::class.java))
        }
    }
}

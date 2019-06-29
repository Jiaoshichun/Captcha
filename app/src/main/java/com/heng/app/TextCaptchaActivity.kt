package com.heng.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.heng.captcha.text.TextCaptchaView

class TextCaptchaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_captcha)
        val textCaptchaView = findViewById<TextCaptchaView>(R.id.text_captcha)

        textCaptchaView.setText("万水千山","千万聪水火领山")
        textCaptchaView.verifyCallBack = { isSuccess ->
            Toast.makeText(this, if (isSuccess) "验证成功" else "验证失败", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.refresh).setOnClickListener {
            textCaptchaView.refresh()
        }

    }
}

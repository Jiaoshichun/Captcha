package com.heng.app


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.heng.captcha.puzzle.DefaultCaptchaStrategy
import com.heng.captcha.puzzle.SlidePuzzleCaptchaHelper
import com.heng.captcha.puzzle.SlidePuzzleCaptchaView

class SlidePuzzleCaptchaActivity : AppCompatActivity() {
    private lateinit var slideCaptchaView: SlidePuzzleCaptchaView
    private lateinit var captchaHelper: SlidePuzzleCaptchaHelper

    private val picRes = arrayListOf<Int>(R.drawable.demo1, R.drawable.demo2, R.drawable.demo3, R.drawable.demo4)
    private var curPos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_puzzle_captcha)
        slideCaptchaView = findViewById(R.id.slide_captcha)
        SlidePuzzleCaptchaHelper.create(slideCaptchaView).setPuzzleDrawable(R.drawable.demo1)
            .setCaptchaStrategy(DefaultCaptchaStrategy(this))
            .matchedCallBack { matchedDegree, durcation ->  }
        captchaHelper = SlidePuzzleCaptchaHelper.create(slideCaptchaView)
        val flashView = captchaHelper.getCoverView()
            .findViewById<View>(R.id.img_flash)
        captchaHelper
            .matchedCallBack { matchedDegree, duration ->
                if (matchedDegree < 0.1) {
                    Toast.makeText(this, "匹配成功,匹配时长:" + duration.toFloat() / 1000, Toast.LENGTH_SHORT)
                        .show()
                    TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, -1f,
                        Animation.RELATIVE_TO_SELF,
                        0f, Animation.RELATIVE_TO_SELF, 0f
                    ).let {
                        it.duration = 500
                        it.fillAfter = true
                        flashView.animation = it
                        flashView.visibility = View.VISIBLE
                        it.setAnimationListener(object : AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {

                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                flashView.visibility = View.GONE
                            }

                            override fun onAnimationStart(animation: Animation?) {

                            }

                        })
                        it.start()
                    }
                } else {
                    captchaHelper.resetSeekBar()
                }
            }
        captchaHelper.getCoverView()
            .findViewById<View>(R.id.img_refresh)
            .setOnClickListener {
                captchaHelper.resetPuzzle()
            }

        findViewById<View>(R.id.change_pic).setOnClickListener {
            captchaHelper.setPuzzleDrawable(picRes[(curPos++) % picRes.size])
//            加载网络图片
//            captchaHelper.setPuzzleNetWorkImage("http://img2.imgtn.bdimg.com/it/u=3893146502,314297687&fm=214&gp=0.jpg")
        }
        findViewById<View>(R.id.change_position).setOnClickListener {
            captchaHelper.resetPuzzle()
        }

    }
}



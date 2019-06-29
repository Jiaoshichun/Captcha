package com.heng.captcha.puzzle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.view.View
import java.net.URLConnection

/**
 * 滑动验证的辅助工具类
 */
class SlidePuzzleCaptchaHelper private constructor(private val slideCaptchaView: SlidePuzzleCaptchaView) {
    companion object {
        fun create(slideCaptchaView: SlidePuzzleCaptchaView): SlidePuzzleCaptchaHelper =
            SlidePuzzleCaptchaHelper(slideCaptchaView)
    }

    /**
     * 设置拼图图片资源
     */
    fun setPuzzleDrawable(@DrawableRes resId: Int): SlidePuzzleCaptchaHelper {
        slideCaptchaView.reset(false)
        slideCaptchaView.puzzleView.reset()
        slideCaptchaView.puzzleView.setImageResource(resId)
        return this
    }

    /**
     * 设置拼图Bitmap
     */
    fun setPuzzleBitmap(bitmap: Bitmap): SlidePuzzleCaptchaHelper {
        slideCaptchaView.reset(false)
        slideCaptchaView.puzzleView.reset()
        slideCaptchaView.puzzleView.setImageBitmap(bitmap)
        return this
    }

    /**
     * 设置拼图drawable
     */
    fun setPuzzleDrawable(drawable: Drawable): SlidePuzzleCaptchaHelper {
        slideCaptchaView.reset(false)
        slideCaptchaView.puzzleView.reset()
        slideCaptchaView.puzzleView.setImageDrawable(drawable)
        return this
    }

    /**
     * 设置拼图网络图片
     */
    fun setPuzzleNetWorkImage(url: String): SlidePuzzleCaptchaHelper {
        BitmapLoaderManager { bitmap ->
            if (bitmap != null)
                setPuzzleBitmap(bitmap)
        }.execute(url)
        return this
    }

    /**
     * 重置seekBar位置
     */
    fun resetSeekBar(): SlidePuzzleCaptchaHelper {
        slideCaptchaView.reset()
        return this
    }

    /**
     * 重置拼图 缺口位置会更新
     */
    fun resetPuzzle(): SlidePuzzleCaptchaHelper {
        slideCaptchaView.reset(false)
        slideCaptchaView.puzzleView.reset()
        return this
    }

    /**
     * 设置 缺块的 绘制策略  默认使用 DefaultCaptchaStrategy
     */
    fun setCaptchaStrategy(captchaStrategy: PuzzleCaptchaStrategy): SlidePuzzleCaptchaHelper {
        slideCaptchaView.puzzleView.setCaptchaStrategy(captchaStrategy)
        return this
    }

    /**
     * 验证结果回调  输入匹配度((滑块x坐标-缺口x坐标)/缺口宽度) 0的时候完全匹配
     */
    fun matchedCallBack(matchedCallBack: (matchedDegree: Float, duration: Long) -> Unit): SlidePuzzleCaptchaHelper {
        slideCaptchaView.matchedCallBack = matchedCallBack
        return this
    }

    /**
     * 获取验证码覆盖层
     */
    fun getCoverView(): View {
        return slideCaptchaView.fLayoutCover
    }
}
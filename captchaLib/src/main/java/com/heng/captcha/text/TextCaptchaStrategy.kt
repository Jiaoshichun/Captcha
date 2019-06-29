package com.heng.captcha.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.heng.captcha.Utils

interface TextCaptchaStrategy {

    /**
     * 获取正常文字绘制笔
     */
    fun getTextPaint(): Paint

    /**
     * 获取选中后文字绘制笔
     */
    fun getSelectedTextPaint(): Paint

    /**
     * 绘制背景
     * @param canvas 画布
     * @param centerX 文字的x轴中心位置
     * @param centerY 文字的y轴中心位置
     */
    fun drawBackGround(canvas: Canvas, centerX: Float, centerY: Float)

    /**
     * 绘制选中后背景
     * @param canvas 画布
     * @param centerX 文字的x轴中心位置
     * @param centerY 文字的y轴中心位置
     */
    fun drawSelectedBackGround(canvas: Canvas, centerX: Float, centerY: Float)

    /**
     * 获取每块的宽高
     * @return first 宽 second 高
     */
    fun getEachWidthAndHeight(): Pair<Int, Int>
}

open class DefaultTextCaptchaStrategy(val context: Context) : TextCaptchaStrategy {


    private val bgPaint = Paint()

    private val bgSelectedPaint = Paint()

    init {
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL_AND_STROKE
        bgPaint.color = Color.WHITE

        bgSelectedPaint.isAntiAlias = true
        bgSelectedPaint.style = Paint.Style.FILL_AND_STROKE
        bgSelectedPaint.color = Color.BLUE

    }

    override fun drawSelectedBackGround(canvas: Canvas, centerX: Float, centerY: Float) {
        canvas.drawCircle(centerX, centerY, Utils.dp2px(context, 20f).toFloat(), bgSelectedPaint)
    }

    override fun drawBackGround(canvas: Canvas, centerX: Float, centerY: Float) {
        canvas.drawCircle(centerX, centerY, Utils.dp2px(context, 20f).toFloat(), bgPaint)
    }


    override fun getEachWidthAndHeight() = Pair(Utils.dp2px(context, 40f), Utils.dp2px(context, 40f))


    override fun getSelectedTextPaint(): Paint {
        val textSelectedPaint = Paint()
        textSelectedPaint.isAntiAlias = true
        textSelectedPaint.textSize = Utils.dp2px(context, 18f).toFloat()
        textSelectedPaint.color = Color.WHITE
        return textSelectedPaint
    }

    override fun getTextPaint(): Paint {
        val textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textSize = Utils.dp2px(context, 18f).toFloat()
        textPaint.color = Color.BLUE
        return textPaint
    }

}
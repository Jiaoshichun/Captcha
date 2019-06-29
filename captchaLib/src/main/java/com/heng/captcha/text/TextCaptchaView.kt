package com.heng.captcha.text

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.MotionEvent
import java.util.*

/**
 * 文本验证  文字在水平方向上平均分布 在竖直方向上随机分布
 */
class TextCaptchaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    //所有的文本集合
    private var allCharList = mutableListOf<TextInfo>()

    //选中的文本信息
    private var selectCharList = mutableListOf<TextInfo>()
    //目标文字
    private var targetText = ""
    //文字宽度
    private var textWidth = 0f
    //文字 FontMetrics 信息
    private var fontMetrics: Paint.FontMetrics? = null

    private var captchaStrategy: TextCaptchaStrategy = DefaultTextCaptchaStrategy(context)

    //选中后文字的画笔
    private var textSelectedPaint = captchaStrategy.getSelectedTextPaint()

    //正常状态的文字画笔
    private var textPaint = captchaStrategy.getTextPaint()

    //每一个文字的宽高
    private var everyWH: Pair<Float, Float>? = null
    //验证回调
    var verifyCallBack: ((isSuccess: Boolean) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //如果文本集合为空 或目标文字为空 则不进行文字的绘制
        if (allCharList.isEmpty() || targetText.isBlank()) return
        if (everyWH == null) {
            everyWH = captchaStrategy.getEachWidthAndHeight()
        }

        if (fontMetrics == null) fontMetrics = textPaint.fontMetrics
        if (textWidth == 0f) {
            textWidth = textPaint.measureText("测")

        }
        //宽度范围
        val scopeWidth = width - paddingLeft - paddingRight - everyWH!!.first
        //高度范围
        val scopeHeight = height - paddingTop - paddingBottom - everyWH!!.second

        //每个文字的最大宽度
        val perWidth = scopeWidth / allCharList.size

        allCharList.forEachIndexed { index, info ->
            //中心坐标X
            val centerX = paddingLeft + (index * perWidth) + perWidth / 2 + everyWH!!.first / 2
            //中心坐标Y
            val centerY = paddingTop + scopeHeight * info.randomValue + everyWH!!.second / 2

            //设置 文字的范围
            info.rectF.left = centerX - everyWH!!.first / 2
            info.rectF.right = centerX + everyWH!!.first / 2
            info.rectF.top = centerY - everyWH!!.second / 2
            info.rectF.bottom = centerY + everyWH!!.second / 2

            if (info.isSelected) {
                captchaStrategy.drawSelectedBackGround(canvas, centerX, centerY)
            } else {
                captchaStrategy.drawBackGround(canvas, centerX, centerY)
            }

            //绘制文字
            canvas.drawText(
                info.text,
                centerX - textWidth / 2,
                centerY + (fontMetrics!!.descent - fontMetrics!!.ascent) / 2 - fontMetrics!!.descent,
                if (info.isSelected) textSelectedPaint else textPaint
            )

        }

    }

    fun refresh() {
        selectCharList.clear()
        allCharList.forEach {
            it.isSelected = false
            it.refreshRandom()
        }
        postInvalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && selectCharList.size != targetText.length) {
            val x = event.x
            val y = event.y
            for (info in allCharList) {
                if (info.rectF.contains(x, y)) {
                    info.isSelected = !info.isSelected
                    if (info.isSelected) {
                        selectCharList.add(info)
                        if (selectCharList.size == targetText.length) {
                            val joinToString = selectCharList.joinToString(separator = "") { it.text }
                            val isSuccess = joinToString == targetText
                            postDelayed({
                                verifyCallBack?.invoke(isSuccess)
                                if (!isSuccess) selectCharList.let { list ->
                                    list.forEach { it.isSelected = false }
                                    list.clear()
                                }
                                postInvalidate()
                            }, 500)

                        }
                    } else {
                        selectCharList.remove(info)
                    }
                    invalidate()
                    break
                }
            }

        }

        return super.onTouchEvent(event)
    }

    /**
     * 设置文本内容
     */
    fun setText(targetText: String, allText: String) {
        this.targetText = targetText
        allCharList.clear()
        allText.forEach {
            allCharList.add(TextInfo(it.toString()))
        }
        invalidate()
    }

    /**
     * 设置文字绘制策略
     */
    fun setCaptchaStrategy(captchaStrategy: TextCaptchaStrategy) {
        this.captchaStrategy = captchaStrategy
        textPaint = captchaStrategy.getTextPaint()
        textSelectedPaint = captchaStrategy.getSelectedTextPaint()
        everyWH = null
        fontMetrics = null
        textWidth = 0f
        postInvalidate()
    }
}

private class TextInfo(
    //文本信息
    var text: String
) {
    //是否被选中
    var isSelected: Boolean = false
    //范围
    var rectF: RectF = RectF()
    var randomValue: Float
    private val random = Random()

    init {
        randomValue = random.nextFloat()
    }

    fun refreshRandom() {
        randomValue = random.nextFloat()
    }
}
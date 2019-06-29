package com.heng.captcha.puzzle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL_AND_STROKE
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.Path.Op.REVERSE_DIFFERENCE
import android.graphics.RectF
import com.heng.captcha.Utils
import java.util.Random

/**
 * 拼图的绘制策略
 */
interface PuzzleCaptchaStrategy {
    /**
     * 获取滑块的path
     */
    fun getBlockPath(): Path

    /**
     * 绘制滑块阴影
     */
    fun drawBlockShadow(
        canvas: Canvas,
        path: Path
    )

    /**
     * 绘制缺口内容
     * 此方法会在onDraw中多次调用 ，避免在方法内容创建大量对象
     */
    fun drawGap(
        canvas: Canvas,
        path: Path
    )

    /**
     * 获取抠图位置 相对于验证码左上角的位置
     * @param xMin  缺口x坐标的最小值  缺口的宽度
     * @param xMax  缺口x坐标的最大值  拼图宽度-缺口宽度
     * @param yMin  缺口y坐标的最小值  缺口的高度
     * @param yMax  缺口y坐标的最大值  拼图高度-缺口高度
     * @return first值为x轴坐标 second值为y轴坐标
     */
    fun getGapPosition(
        xMin: Float,
        xMax: Float,
        yMin: Float,
        yMax: Float
    ): Pair<Float, Float>
}

open class DefaultCaptchaStrategy(private val context: Context) : PuzzleCaptchaStrategy {

    private val shadowPaint = Paint()
    private val gapPaint = Paint()

    init {
        gapPaint.color = Color.parseColor("#aa000000")
        gapPaint.isAntiAlias = true
        gapPaint.style = FILL_AND_STROKE

        shadowPaint.style = STROKE
        shadowPaint.color = Color.WHITE
        shadowPaint.alpha = 200
        shadowPaint.strokeWidth = Utils.dp2px(context, 1f)
        shadowPaint.setShadowLayer(
            Utils.dp2px(context, 10f),
            Utils.dp2px(context, 2f),
            Utils.dp2px(context, 2f),
            Color.WHITE
        )
    }

    //绘制滑块阴影
    override fun drawBlockShadow(
        canvas: Canvas,
        path: Path
    ) {
        canvas.drawPath(path, shadowPaint)
    }

    //获取滑块图形
    override fun getBlockPath(): Path {
        val blockPath = Path()
        val path1 = Path()
        path1.addRect(
            RectF(
                Utils.dp2px(context, 0f),
                Utils.dp2px(context, 0f),
                Utils.dp2px(context, 50f),
                Utils.dp2px(context, 50f)
            ),
            CW
        )
        blockPath.op(path1, REVERSE_DIFFERENCE)
        blockPath.addArc(
            RectF(
                Utils.dp2px(context, 40f),
                Utils.dp2px(context, 15f),
                Utils.dp2px(context, 60f),
                Utils.dp2px(context, 35f)
            ), -270f, 180f
        )
        val path2 = Path()
        path2.addArc(
            RectF(
                Utils.dp2px(context, 15f),
                Utils.dp2px(context, 40f),
                Utils.dp2px(context, 35f),
                Utils.dp2px(context, 60f)
            ), -360f, 180f
        )
        blockPath.op(path2, Path.Op.XOR)
        return blockPath
    }

    //绘制缺口内容
    override fun drawGap(
        canvas: Canvas,
        path: Path
    ) {
        canvas.drawPath(path, gapPaint)
    }

    //获取缺口的 位置
    override fun getGapPosition(
        xMin: Float,
        xMax: Float,
        yMin: Float,
        yMax: Float
    ): Pair<Float, Float> {
        val random = Random()
        return Pair(
            random.nextFloat() * (xMax - xMin) + xMin, random.nextFloat() * (yMax - yMin) + yMin
        )
    }
}
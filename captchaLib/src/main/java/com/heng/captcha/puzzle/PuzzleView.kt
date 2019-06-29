package com.heng.captcha.puzzle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.widget.ImageView.ScaleType.FIT_XY

class PuzzleView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

  private val blockPaint = Paint()
  private lateinit var blockPath: Path
  private var slideBitmap: Bitmap? = null
  //缺块位置
  private var gapPosition: Pair<Float, Float>? = null

  // 滑动的X坐标
  private var slidePositionX: Float = 0f
  private var isGameOver = false
  private var captchaStrategy: PuzzleCaptchaStrategy

  private var gapWidth: Float = 0f //缺口的 宽度
  private var gapHeight: Float = 0f// 缺口的 高度

  init {
    scaleType = FIT_XY
    setLayerType(LAYER_TYPE_SOFTWARE, null)
    captchaStrategy = DefaultCaptchaStrategy(context)
    setCaptchaStrategy(captchaStrategy)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (drawable == null || isGameOver) return

    // 设置 缺口的大小
    if (gapHeight == 0f || gapWidth == 0f) {
      val rectF = RectF()
      blockPath.computeBounds(rectF, true)
      gapWidth = rectF.right - rectF.left
      gapHeight = rectF.bottom - rectF.top
    }

    if (gapPosition == null) {
      //获取缺口位置
      gapPosition =
        captchaStrategy.getGapPosition(gapWidth, width - gapWidth, gapHeight, height - gapHeight)
      blockPath.offset(gapPosition!!.first, gapPosition!!.second)
    }

    //生成 滑块 bitmap
    if (slideBitmap == null) {
      createSlideBitmap()
    }
    //绘制缺块内容
    captchaStrategy.drawGap(canvas,blockPath)

    //绘制滑块
    canvas.drawBitmap(slideBitmap!!, slidePositionX, gapPosition!!.second, blockPaint)
  }

  /**
   *   生成 滑块 bitmap
   */
  private fun createSlideBitmap() {
    val tempBitmap = Bitmap.createBitmap(width, height, ARGB_8888)
    val clipCanvas = Canvas(tempBitmap!!)
    val bounds = drawable.copyBounds()
    drawable.setBounds(0, 0, width, height)
    clipCanvas.clipPath(blockPath)
    drawable.draw(clipCanvas)
    drawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom)
    captchaStrategy.drawBlockShadow(clipCanvas, blockPath)

    slideBitmap =
      Bitmap.createBitmap(
          tempBitmap, gapPosition!!.first.toInt(), gapPosition!!.second.toInt(),
          gapWidth.toInt(), gapHeight.toInt()
      )
    tempBitmap.recycle()
  }

  /**
   * 拖动 滑块
   * @param progress x轴滑动比例
   */
  internal fun dragView(progress: Float) {
    slidePositionX = progress * (width - (slideBitmap?.width ?: 0))
    invalidate()
  }

  /**
   * 重置
   */
  internal fun reset() {
    gapHeight = 0f
    gapWidth = 0f
    isGameOver = false
    slideBitmap?.recycle()
    slideBitmap = null
    gapPosition = null

    //获取滑块的path
    blockPath = captchaStrategy.getBlockPath()


    //获取缺口的宽高
    val rectF = RectF()
    blockPath.computeBounds(rectF, true)
    gapWidth = rectF.right - rectF.left
    gapHeight = rectF.bottom - rectF.top

    postInvalidate()
  }

  /**
   * 成功调用
   */
  fun onSuccess() {
    isGameOver = true
    postInvalidate()
  }

  /**
   * 设置验证码样式策略
   */
  fun setCaptchaStrategy(captchaStrategy: PuzzleCaptchaStrategy) {
    this.captchaStrategy = captchaStrategy
    reset()
  }

  /**
   * 获取匹配度
   */
  fun getMatchedDegree(): Float {
    if (slideBitmap == null || gapPosition == null) return 0f
    return Math.abs(gapPosition!!.first - slidePositionX) / slideBitmap!!.width
  }

}
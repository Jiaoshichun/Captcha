package com.heng.captcha.puzzle

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.heng.captcha.R
import com.heng.captcha.R.styleable
import com.heng.captcha.Utils

/**
 * 滑动验证码
 */
open class SlidePuzzleCaptchaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnSeekBarChangeListener {

    private val imgPuzzleBg: ImageView //拼图背景
    internal val puzzleView: PuzzleView  //拼图
    private val sbDrag: SeekBar
    private val txtDragTip: TextView  // seekBar滑动提示
    private var wHRatio = "16:9" //拼图宽高比例
    internal val fLayoutCover: FrameLayout //拼图上面的覆盖层
    private var isShowTipText = true
    //验证结果回调
    internal var matchedCallBack: ((matchedDegree: Float, duration: Long) -> Unit)? = null
    private var startTime = 0L //开始拖动时间

    init {
        View.inflate(context, R.layout.slide_puzzle_captcha_view, this)
        imgPuzzleBg = findViewById(R.id.img_puzzle_bg)
        puzzleView = findViewById(R.id.img_puzzle)
        sbDrag = findViewById(R.id.sb_drag)
        fLayoutCover = findViewById(R.id.flayout_cover)
        txtDragTip = findViewById(R.id.txt_drag_tip)
        sbDrag.setOnSeekBarChangeListener(this)
        val params = imgPuzzleBg.layoutParams as ConstraintLayout.LayoutParams
        params.dimensionRatio = wHRatio
        imgPuzzleBg.layoutParams = params
        if (attrs != null)
            initCustomAttrs(context, attrs)
    }

    /**
     * 初始化自定义属性
     */
    private fun initCustomAttrs(
        context: Context,
        attrs: AttributeSet
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, styleable.SlidePuzzleCaptchaView)
        for (index in 0..typedArray.indexCount) {
            when (val attr = typedArray.getIndex(index)) {
                //设置滑块提示文字
                styleable.SlidePuzzleCaptchaView_dragTipText ->
                    typedArray.getString(attr)?.let {
                        txtDragTip.text = it
                    }
                //设置滑块文字颜色
                styleable.SlidePuzzleCaptchaView_dragTipTextColor ->
                    typedArray.getColorStateList(attr)?.let {
                        txtDragTip.setTextColor(it)
                    }

                //设置进度条 滑动球
                styleable.SlidePuzzleCaptchaView_seekBarThumb ->
                    typedArray.getDrawable(attr)?.let {
                        sbDrag.thumb = it
                    }
                //设置进度条样式
                styleable.SlidePuzzleCaptchaView_seekBarProgressDrawable ->
                    typedArray.getDrawable(attr)?.let {
                        sbDrag.progressDrawable = it
                    }

                //设置进度条的高度
                styleable.SlidePuzzleCaptchaView_seekBarHeight -> {
                    typedArray.getDimension(attr, Utils.dp2px(context, 40f).toFloat())
                        .let {
                            val params = sbDrag.layoutParams
                            params.height = it.toInt()
                            sbDrag.layoutParams = params
                        }
                }
                //seekBar的 minHeight
                styleable.SlidePuzzleCaptchaView_seekBarMinHeight -> {
                    typedArray.getDimension(attr, -1f)
                        .let {
                            if (it != -1f) {
                                sbDrag.minimumHeight = it.toInt()
                            }
                        }
                }
                //设置seekBar的margin值
                styleable.SlidePuzzleCaptchaView_seekBarMargin -> {
                    typedArray.getDimension(attr, -1f)
                        .let {
                            if (it != -1f) {
                                val params = sbDrag.layoutParams as MarginLayoutParams
                                params.topMargin = it.toInt()
                                params.bottomMargin = it.toInt()
                                params.leftMargin = it.toInt()
                                params.rightMargin = it.toInt()
                                sbDrag.layoutParams = params
                            }
                        }
                }
                styleable.SlidePuzzleCaptchaView_seekBarMarginTop -> {
                    typedArray.getDimension(attr, -1f)
                        .let {
                            if (it != -1f) {
                                val params = sbDrag.layoutParams as MarginLayoutParams
                                params.topMargin = it.toInt()
                                sbDrag.layoutParams = params
                            }
                        }
                }
                styleable.SlidePuzzleCaptchaView_seekBarMarginBottom -> {
                    typedArray.getDimension(attr, -1f)
                        .let {
                            if (it != -1f) {
                                val params = sbDrag.layoutParams as MarginLayoutParams
                                params.bottomMargin = it.toInt()
                                sbDrag.layoutParams = params
                            }
                        }
                }
                styleable.SlidePuzzleCaptchaView_seekBarMarginLeft -> {
                    typedArray.getDimension(attr, -1f)
                        .let {
                            if (it != -1f) {
                                val params = sbDrag.layoutParams as MarginLayoutParams
                                params.leftMargin = it.toInt()
                                sbDrag.layoutParams = params
                            }
                        }
                }
                styleable.SlidePuzzleCaptchaView_seekBarMarginRight -> {
                    typedArray.getDimension(attr, -1f)
                        .let {
                            if (it != -1f) {
                                val params = sbDrag.layoutParams as MarginLayoutParams
                                params.rightMargin = it.toInt()
                                sbDrag.layoutParams = params
                            }
                        }
                }
                //设置拼图背景样式
                styleable.SlidePuzzleCaptchaView_puzzleBackground -> typedArray.getDrawable(attr)?.let {
                    imgPuzzleBg.background = it
                }

                //设置拼图覆盖层样式
                styleable.SlidePuzzleCaptchaView_puzzleCoverLayout -> {
                    val resourceId = typedArray.getResourceId(attr, -1)
                    if (resourceId != -1) {
                        View.inflate(context, resourceId, fLayoutCover)
                    }
                }
                //设置拼图宽高比
                styleable.SlidePuzzleCaptchaView_puzzleRatio -> typedArray.getString(attr)?.let {
                    wHRatio = it
                    val params = imgPuzzleBg.layoutParams as ConstraintLayout.LayoutParams
                    params.dimensionRatio = wHRatio
                    imgPuzzleBg.layoutParams = params
                }
                //设置拼图与背景的间距
                styleable.SlidePuzzleCaptchaView_puzzlePadding -> {
                    val padding = typedArray.getDimension(attr, Utils.dp2px(context, 5f).toFloat())
                        .toInt()
                    val params = puzzleView.layoutParams as MarginLayoutParams
                    params.topMargin = padding
                    params.bottomMargin = padding
                    params.leftMargin = padding
                    params.rightMargin = padding
                    puzzleView.layoutParams = params
                }
                styleable.SlidePuzzleCaptchaView_puzzlePaddingLeft -> {
                    val paddingLeft = typedArray.getDimension(attr, Utils.dp2px(context, 5f).toFloat())
                        .toInt()
                    val params = puzzleView.layoutParams as MarginLayoutParams
                    params.leftMargin = paddingLeft
                    puzzleView.layoutParams = params
                }
                styleable.SlidePuzzleCaptchaView_puzzlePaddingRight -> {
                    val paddingRight = typedArray.getDimension(attr, Utils.dp2px(context, 5f).toFloat())
                        .toInt()
                    val params = puzzleView.layoutParams as MarginLayoutParams
                    params.rightMargin = paddingRight
                    puzzleView.layoutParams = params
                }
                styleable.SlidePuzzleCaptchaView_puzzlePaddingTop -> {
                    val paddingTop = typedArray.getDimension(attr, Utils.dp2px(context, 5f).toFloat())
                        .toInt()
                    val params = puzzleView.layoutParams as MarginLayoutParams
                    params.topMargin = paddingTop
                    puzzleView.layoutParams = params
                }
                styleable.SlidePuzzleCaptchaView_puzzlePaddingBottom -> {
                    val paddingBottom = typedArray.getDimension(attr, Utils.dp2px(context, 5f).toFloat())
                        .toInt()
                    val params = puzzleView.layoutParams as MarginLayoutParams
                    params.bottomMargin = paddingBottom
                    puzzleView.layoutParams = params
                }
            }
        }
        typedArray.recycle()
    }

    /**
     * 提示文字展示和隐藏动画
     */
    private fun tipTextAnimation(isShow: Boolean) {
        if (isShowTipText == isShow) return
        this.isShowTipText = isShow
        val start = if (isShow) 0f else 1f
        val end = if (isShow) 1f else 0f
        val alphaAnimation = AlphaAnimation(start, end)
        alphaAnimation.fillAfter = true
        alphaAnimation.duration = 300
        txtDragTip.startAnimation(alphaAnimation)
    }

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        puzzleView.dragView(progress.toFloat() / seekBar!!.max)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        tipTextAnimation(false)
        startTime = System.currentTimeMillis()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        matchedCallBack?.invoke(puzzleView.getMatchedDegree(), System.currentTimeMillis() - startTime)
    }

    /**
     * 重置
     * @param hasAnim 是否有动画 默认true  在切换图片时不需要动画
     */
    internal fun reset(hasAnim: Boolean = true) {
        if (!hasAnim) {
            sbDrag.progress = 0
            tipTextAnimation(true)
            return
        }
        sbDrag.isEnabled = false
        val progress = sbDrag.progress
        postDelayed({
            ValueAnimator.ofInt(progress, 0)
                .apply {
                    duration = 300
                    this.addUpdateListener { animation ->
                        sbDrag.progress = animation.animatedValue as Int
                    }
                    this.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            tipTextAnimation(true)
                            sbDrag.isEnabled = true
                        }
                    })
                    start()
                }
        }, 300)
    }

}
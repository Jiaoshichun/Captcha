package com.heng.captcha

import android.content.Context

object Utils {
  fun dp2px(
    context: Context,
    dp: Float
  ): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
  }

  fun px2dp(
    context: Context,
    px: Float
  ): Int {
    val density = context.resources.displayMetrics.density
    return (px / density + 0.5f).toInt()
  }

  fun getDisplayWidth(context: Context): Int {
    return context.resources.displayMetrics.widthPixels
  }

  fun getDisplayHeight(context: Context): Int {
    return context.resources.displayMetrics.heightPixels
  }
}
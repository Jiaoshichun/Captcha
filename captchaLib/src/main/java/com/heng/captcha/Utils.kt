package com.heng.captcha

import android.content.Context

object Utils {
  fun dp2px(
    context: Context,
    dp: Float
  ): Float {
    val density = context.resources.displayMetrics.density
    return dp * density
  }

  fun px2dp(
    context: Context,
    px: Float
  ): Float {
    val density = context.resources.displayMetrics.density
    return px / density
  }

  fun getDisplayWidth(context: Context): Int {
    return context.resources.displayMetrics.widthPixels
  }

  fun getDisplayHeight(context: Context): Int {
    return context.resources.displayMetrics.heightPixels
  }
}
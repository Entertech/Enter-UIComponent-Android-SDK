package cn.entertech.uicomponentsdk.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout

fun Float.dp():Float{
   return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this,Resources.getSystem().displayMetrics)
}

fun View.toDrawable(context: Context): Drawable {
   this.measure(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
   )
   val b = Bitmap.createBitmap(
      this.getMeasuredWidth(),
      this.getMeasuredHeight(),
      Bitmap.Config.ARGB_8888
   )
   val c = Canvas(b)
   this.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight())
   this.draw(c)
   return BitmapDrawable(context.resources,b)
}
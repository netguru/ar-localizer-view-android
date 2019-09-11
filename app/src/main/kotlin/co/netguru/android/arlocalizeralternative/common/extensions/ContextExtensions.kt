package co.netguru.android.arlocalizeralternative.common.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import android.util.TypedValue

inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)

fun Context.getAttributeColor(@AttrRes attrColor: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrColor, typedValue, true)
    return typedValue.data
}

fun Context.getAttributeDrawable(@AttrRes attrDrawableRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrDrawableRes, typedValue, true)
    return typedValue.resourceId
}

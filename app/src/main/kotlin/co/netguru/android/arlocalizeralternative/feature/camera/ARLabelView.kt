package co.netguru.android.arlocalizeralternative.feature.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import co.netguru.android.arlocalizeralternative.R
import co.netguru.android.arlocalizeralternative.feature.compass.CompassData
import kotlin.math.min


class ARLabelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var distance: String = ""
    private var textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.ar_label_text)
        textSize = TEXT_SIZE
        textAlign = Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private var rectanglePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.ar_label_background)
        style = Paint.Style.FILL
    }

    private var shouldShowLabel = false
    private var positionX = 0f
    private var positionY = 0f



    companion object {
        private const val MAX_HORIZONTAL_ANGLE_VARIATION = 30f
        private const val MAX_VERTICAL_PITCH_VARIATION = 60f
        private const val TEXT_HORIZONTAL_PADDING = 50f
        private const val TEXT_VERTICAL_PADDING = 35f
        private const val LABEL_CORNER_RADIUS = 20f
        private const val TEXT_SIZE = 50f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(!shouldShowLabel) return

        val textWidth = textPaint.measureText(distance) / 2
        val textSize = textPaint.textSize

        canvas?.drawRoundRect(positionX - textWidth - TEXT_HORIZONTAL_PADDING, positionY - textSize - TEXT_VERTICAL_PADDING, positionX + textWidth + TEXT_HORIZONTAL_PADDING,
            positionY + TEXT_VERTICAL_PADDING, LABEL_CORNER_RADIUS, LABEL_CORNER_RADIUS, rectanglePaint)
        canvas?.drawText(distance, positionX, positionY, textPaint)
    }


    fun setCompassData(compassData: CompassData) {
        compassData.currentDestinationAzimuth
        shouldShowLabel = compassData.currentDestinationAzimuth in 0f..0f + MAX_HORIZONTAL_ANGLE_VARIATION || compassData.currentDestinationAzimuth in 360f - MAX_HORIZONTAL_ANGLE_VARIATION..360f
        positionX = when(compassData.currentDestinationAzimuth) {
            in 0f..0f + MAX_HORIZONTAL_ANGLE_VARIATION -> {
                width / 2 + (compassData.currentDestinationAzimuth * width / 2) / MAX_HORIZONTAL_ANGLE_VARIATION
            }
            in 330f..330f + MAX_HORIZONTAL_ANGLE_VARIATION -> {
                width / 2 - ((360f - compassData.currentDestinationAzimuth) * width / 2 / MAX_HORIZONTAL_ANGLE_VARIATION)
            }
            else -> 0f
        }

        positionY = when(compassData.orientationData.currentPitch) {
            in -MAX_VERTICAL_PITCH_VARIATION..0f -> {
                height / 2 - (compassData.orientationData.currentPitch * height / 2 / MAX_VERTICAL_PITCH_VARIATION)
            }
            in 0f..MAX_VERTICAL_PITCH_VARIATION -> {
                height /2 - (compassData.orientationData.currentPitch * height / 2 / MAX_VERTICAL_PITCH_VARIATION)
            }
            else -> 0f

        }

        distance = "${compassData.distanceToDestination} m"
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, specSize)
            }
        }
        return result
    }
}

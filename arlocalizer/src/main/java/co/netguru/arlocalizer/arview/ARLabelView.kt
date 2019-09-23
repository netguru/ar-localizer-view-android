package co.netguru.arlocalizer.arview

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import co.netguru.arlocalizer.R
import co.netguru.arlocalizer.arview.ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MAX
import co.netguru.arlocalizer.arview.ARLabelPositionUtils.HORIZONTAL_ANGLE_RANGE_MIN
import co.netguru.arlocalizer.arview.ARLabelPositionUtils.adjustLowPassFilterAlphaValue
import co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionX
import co.netguru.arlocalizer.arview.ARLabelPositionUtils.calculatePositionY
import co.netguru.arlocalizer.compass.CompassData
import kotlin.math.min


internal class ARLabelView @JvmOverloads constructor(
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

    private var animatedRectanglePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.ar_label_background)
        style = Paint.Style.STROKE
        strokeWidth =
            ANIMATED_RECTANGLE_STROKE_WIDTH
    }

    private var shouldShowLabel: Boolean = false
        set(value) {
            if (field != value && value) showUpAnimation()
            field = value
        }
    private var positionX = -1f
    private var positionY = -1f
    private var animatedRectangleSize = 0
    private var showUpAnimator: ValueAnimator? = null
    private var lowPassFilterAlphaListener: ((Float) -> Unit)? = null


    companion object {
        private const val TEXT_HORIZONTAL_PADDING = 50f
        private const val TEXT_VERTICAL_PADDING = 35f
        private const val LABEL_CORNER_RADIUS = 20f
        private const val TEXT_SIZE = 50f
        private const val PROPERTY_SIZE = "size"
        private const val ANIMATED_RECTANGLE_STROKE_WIDTH = 10f
        private const val ANIMATED_VALUE_MAX_SIZE = 120
        private const val ANIMATION_DURATION = 1000L
        private const val ACCELERATE_INTERPOLATOR_FACTOR = 2.5f
        private const val MAX_ALPHA_VALUE = 255f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!shouldShowLabel) return

        val textWidth = textPaint.measureText(distance) / 2
        val textSize = textPaint.textSize

        val left = positionX - textWidth - TEXT_HORIZONTAL_PADDING
        val top = positionY - textSize - TEXT_VERTICAL_PADDING
        val right = positionX + textWidth + TEXT_HORIZONTAL_PADDING
        val bottom = positionY + TEXT_VERTICAL_PADDING

        canvas?.drawRoundRect(
            left, top, right, bottom
            ,
            LABEL_CORNER_RADIUS,
            LABEL_CORNER_RADIUS, rectanglePaint
        )
        canvas?.drawText(distance, positionX, positionY, textPaint)

        if (showUpAnimator?.isRunning == true) {
            applyAnimationValues(canvas, left, top, right, bottom)
        }
    }

    private fun applyAnimationValues(
        canvas: Canvas?,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        animatedRectanglePaint.alpha =
            (MAX_ALPHA_VALUE - animatedRectangleSize * MAX_ALPHA_VALUE / ANIMATED_VALUE_MAX_SIZE).toInt()
        canvas?.drawRoundRect(
            left - animatedRectangleSize, top - animatedRectangleSize,
            right + animatedRectangleSize, bottom + animatedRectangleSize,
            LABEL_CORNER_RADIUS,
            LABEL_CORNER_RADIUS, animatedRectanglePaint
        )
    }

    fun setCompassData(compassData: CompassData) {
        shouldShowLabel = compassData.currentDestinationAzimuth in HORIZONTAL_ANGLE_RANGE_MIN
                || compassData.currentDestinationAzimuth in HORIZONTAL_ANGLE_RANGE_MAX

        positionX = calculatePositionX(compassData, width)
        positionY = calculatePositionY(compassData, height)

        distance = "${compassData.distanceToDestination} m"

        if (isInView()) {
            lowPassFilterAlphaListener?.invoke(adjustLowPassFilterAlphaValue(positionX, width))
        }

        invalidate()
    }

    fun setLowPassFilterAlphaListener(lowPassFilterAlphaListener: ((Float) -> Unit)?) {
        this.lowPassFilterAlphaListener = lowPassFilterAlphaListener
    }

    private fun isInView() = positionX > 0 && positionX < width

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    private fun showUpAnimation() {
        if (showUpAnimator?.isRunning == true) return
        val propertySize = PropertyValuesHolder.ofInt(
            PROPERTY_SIZE, 0,
            ANIMATED_VALUE_MAX_SIZE
        )
        showUpAnimator = ValueAnimator()
        showUpAnimator?.setValues(propertySize)
        showUpAnimator?.duration =
            ANIMATION_DURATION
        showUpAnimator?.interpolator = AccelerateInterpolator(ACCELERATE_INTERPOLATOR_FACTOR)
        showUpAnimator?.addUpdateListener { animation ->
            animatedRectangleSize = animation.getAnimatedValue(PROPERTY_SIZE) as? Int ?: 0
            invalidate()
        }
        showUpAnimator?.start()
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
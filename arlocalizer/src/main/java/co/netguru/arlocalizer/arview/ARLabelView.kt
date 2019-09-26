package co.netguru.arlocalizer.arview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import co.netguru.arlocalizer.R
import co.netguru.arlocalizer.arview.ARLabelUtils.adjustLowPassFilterAlphaValue
import co.netguru.arlocalizer.arview.ARLabelUtils.getShowUpAnimation
import co.netguru.arlocalizer.compass.CompassData
import kotlin.math.min


internal class ARLabelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

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

    private var arLabels: List<ARLabelProperties>? = null
    private var animators = mutableMapOf<Int, ARLabelAnimationData>()
    private var lowPassFilterAlphaListener: ((Float) -> Unit)? = null

    companion object {
        private const val TEXT_HORIZONTAL_PADDING = 50f
        private const val TEXT_VERTICAL_PADDING = 35f
        private const val LABEL_CORNER_RADIUS = 20f
        private const val TEXT_SIZE = 50f
        private const val ANIMATED_RECTANGLE_STROKE_WIDTH = 10f
        private const val ANIMATED_VALUE_MAX_SIZE = 120
        private const val MAX_ALPHA_VALUE = 255f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        arLabels
            ?.forEach {
                drawArLabel(canvas, it)
            }
    }

    private fun drawArLabel(canvas: Canvas?, arLabelProperties: ARLabelProperties) {

        val labelText = "${arLabelProperties.distance} m"
        val textWidthHalf = textPaint.measureText(labelText) / 2
        val textSize = textPaint.textSize

        val left = arLabelProperties.positionX - textWidthHalf - TEXT_HORIZONTAL_PADDING
        val top = arLabelProperties.positionY - textSize - TEXT_VERTICAL_PADDING
        val right = arLabelProperties.positionX + textWidthHalf + TEXT_HORIZONTAL_PADDING
        val bottom = arLabelProperties.positionY + TEXT_VERTICAL_PADDING

        canvas?.drawRoundRect(
            left, top, right, bottom
            ,
            LABEL_CORNER_RADIUS,
            LABEL_CORNER_RADIUS, rectanglePaint.apply { alpha = arLabelProperties.alpha }
        )
        canvas?.drawText(
            labelText,
            arLabelProperties.positionX,
            arLabelProperties.positionY,
            textPaint.apply { alpha = arLabelProperties.alpha }
        )

        if (animators[arLabelProperties.id]?.valueAnimator?.isRunning == true) {
            applyAnimationValues(canvas, left, top, right, bottom,
                animators[arLabelProperties.id]?.animatedSize ?: 0)
        }
    }

    private fun applyAnimationValues(
        canvas: Canvas?,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        animatedRectangleSize: Int
    ) {
        animatedRectanglePaint.alpha =
            getAnimatedAlphaValue(animatedRectangleSize)
        canvas?.drawRoundRect(
            left - animatedRectangleSize, top - animatedRectangleSize,
            right + animatedRectangleSize, bottom + animatedRectangleSize,
            LABEL_CORNER_RADIUS,
            LABEL_CORNER_RADIUS, animatedRectanglePaint
        )
    }

    private fun getAnimatedAlphaValue(animatedRectangleSize: Int) =
        (MAX_ALPHA_VALUE - animatedRectangleSize * MAX_ALPHA_VALUE / ANIMATED_VALUE_MAX_SIZE).toInt()

    fun setCompassData(compassData: CompassData) {
        val labelsThatShouldBeShown =
            ARLabelUtils.prepareLabelsProperties(compassData, width, height)

        showAnimationIfNeeded(arLabels, labelsThatShouldBeShown)

        arLabels = labelsThatShouldBeShown

        adjustAlphaFilterValue()

        invalidate()
    }

    private fun adjustAlphaFilterValue() {
        arLabels
            ?.find { isInView(it.positionX) }
            ?.let {
                lowPassFilterAlphaListener?.invoke(
                    adjustLowPassFilterAlphaValue(it.positionX, width)
                )
            }
    }

    private fun showAnimationIfNeeded(
        labelsShownBefore: List<ARLabelProperties>?,
        labelsThatShouldBeShown: List<ARLabelProperties>
    ) {
        labelsShownBefore?.let { checkForShowingUpLabels(labelsThatShouldBeShown, it) }
            ?: labelsThatShouldBeShown.forEach {
                animators[it.id] = getShowUpAnimation()
            }
    }

    private fun checkForShowingUpLabels(
        labelsThatShouldBeShown: List<ARLabelProperties>,
        labelsShownBefore: List<ARLabelProperties>
    ) {
        labelsThatShouldBeShown.minus(labelsShownBefore)
            .forEach { newLabels ->
                animators[newLabels.id] = getShowUpAnimation()
            }
    }


    fun setLowPassFilterAlphaListener(lowPassFilterAlphaListener: ((Float) -> Unit)?) {
        this.lowPassFilterAlphaListener = lowPassFilterAlphaListener
    }

    private fun isInView(positionX: Float) = positionX > 0 && positionX < width

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

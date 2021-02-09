package com.udacity.util.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import com.udacity.ButtonState
import com.udacity.R
import com.udacity.util.dpToPx
import com.udacity.util.spToPx
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val loadingRect = Rect()
    private var valueAnimator = ValueAnimator()
    private var progress = 0
    private var loadingState = 0

    private val textRect = Rect()
    private val circleRect = RectF()
    private val cornerPath = Path()
    private val cornerRadius = 4.dpToPx().toFloat()
    private var buttonText = "Download"

    private var textColor = Color.WHITE
    private var primaryColor = context.getColor(R.color.colorPrimary)
    private var progressColor = context.getColor(R.color.colorAccent)
    private var secondaryColor = context.getColor(R.color.colorPrimaryDark)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
            }
            ButtonState.Loading -> {
                setText("We are Downloading")
                valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000).apply {
                    addUpdateListener {
                        progress = it.animatedValue as Int
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            this@LoadingButton.buttonState = ButtonState.Completed
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                            super.onAnimationCancel(animation)
                            progress = 0
                            loadingState = 0
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                            super.onAnimationRepeat(animation)
                            loadingState = loadingState xor 1
                        }
                    })
                    interpolator = LinearInterpolator()
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    start()
                }
                this.isEnabled = false
            }
            ButtonState.Completed -> {
                setText("Download")
                valueAnimator.cancel()
                this.isEnabled = true
            }
        }
        requestLayout()
        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        textAlignment = TEXT_ALIGNMENT_CENTER
        textSize = 16.spToPx().toFloat()
        typeface = Typeface.DEFAULT_BOLD
    }

    init {
        context.withStyledAttributes(set = attrs, attrs = R.styleable.LoadingButton) {
            primaryColor = getColor(R.styleable.LoadingButton_primaryBackground, primaryColor)
            secondaryColor = getColor(R.styleable.LoadingButton_secondaryBackground, secondaryColor)
            progressColor = getColor(R.styleable.LoadingButton_progressColor, progressColor)
            textColor = getColor(R.styleable.LoadingButton_textColor, textColor)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        val minH = paddingTop + paddingBottom + suggestedMinimumHeight
        val w = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h = resolveSizeAndState(minH, heightMeasureSpec, 1)
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cornerPath.reset()
        cornerPath.addRoundRect(
            0f,
            0f,
            w.toFloat(),
            h.toFloat(),
            cornerRadius,
            cornerRadius,
            Path.Direction.CW
        )
        cornerPath.close()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            save()

            clipPath(cornerPath)
            drawColor(primaryColor)

            paint.getTextBounds(buttonText, 0, buttonText.length, textRect)
            val textX = width / 2f - textRect.width() / 2f
            val textY = height / 2f + textRect.height() / 2f - textRect.bottom

            var textOffset = 0
            if (buttonState == ButtonState.Loading) {
                paint.color = secondaryColor
                if (loadingState == 0) {
                    loadingRect.set(0, 0, width * progress / 360, height)
                } else {
                    loadingRect.set(width * progress / 360, 0, width, height)
                }
                drawRect(loadingRect, paint)

                paint.style = Paint.Style.FILL
                paint.color = progressColor
                val circleStartX = width / 2f + textRect.width() / 2f
                val circleStartY = height / 2f - 20
                circleRect.set(circleStartX, circleStartY, circleStartX + 40, circleStartY + 40)
                if (loadingState == 0) {
                    drawArc(circleRect, 0f, progress.toFloat(), true, paint)
                } else {
                    drawArc(
                        circleRect,
                        progress.toFloat(),
                        360f - progress.toFloat(),
                        true,
                        paint
                    )
                }
                textOffset = 35
            }

            paint.color = textColor
            drawText(buttonText, textX - textOffset, textY, paint)

            restore()
        }
    }

    override fun getSuggestedMinimumWidth(): Int {
        paint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        return textRect.width() - textRect.left + if (buttonState == ButtonState.Loading) 70 else 0
    }

    override fun getSuggestedMinimumHeight(): Int {
        paint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        return textRect.height()
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    private fun setText(text: String) {
        this.buttonText = text
        invalidate()
        requestLayout()
    }
}
package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import java.time.Duration
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator :ValueAnimator = ValueAnimator()
    private var buttonRec : Rect = Rect(0,0,0,0)
    private var buttonAnimateRect : Rect = Rect(0,0,0,0)
    private var circleRect : RectF  = RectF(0f,0f,0f,0f)
    private var textRect : Rect  = Rect()
    private var circleRadius = 0f
    private var text = "Download"
    private  var loadingText = "Downloadig...."
    private var fontSize = 50f
    private var butcolor = resources.getColor(R.color.primaryLightColor,null)
    private var butAnimateColor = resources.getColor(R.color.secondaryDarkColor,null)
    private var textColor = resources.getColor(R.color.primaryTextColor,null)
    private var cirleColor = resources.getColor(R.color.yellow,null)
    var currentProgress : Float = 0f
    var isAnimated : Boolean = false


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55f
        typeface = Typeface.create("", Typeface.BOLD)

    }

    init{
        isAnimated  = false
        buttonRec = Rect(0,0,0,0)
        buttonAnimateRect  = Rect(0,0,0,0)
        circleRect  = RectF(0f,0f,0f,0f)
        textRect = Rect()
        circleRadius = 0f
        text = "Download"
        loadingText = "Downloadig...."

    }

    fun startAnimation(){

        val durationTime = 1400L

        valueAnimator.cancel()
        isAnimated = true
        valueAnimator = ValueAnimator.ofFloat(currentProgress,1f)
            .apply {
            duration = durationTime
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener {
                currentProgress = it.animatedValue as Float
                invalidate()
            }

            doOnEnd {
                currentProgress = 0f
                isAnimated = false
            }

            doOnCancel {
                isAnimated = false
            }

            start()
        }

    }

    fun stopAnimation(){
        isAnimated = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthSize = width
        heightSize = height
        buttonRec = Rect(0,0,widthSize,heightSize)
        buttonAnimateRect = Rect(0,0,widthSize,heightSize)
        circleRadius = height/4f
        circleRect.top  = heightSize/2 - circleRadius
        circleRect.bottom = heightSize/2 + circleRadius

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            if (isAnimated){

                paint.color = butcolor
                drawRect(buttonRec,paint)

                paint.color = butAnimateColor

                buttonAnimateRect.right = ((widthSize.toFloat()*currentProgress).roundToInt())
                drawRect(buttonAnimateRect,paint)


                paint.color = textColor
                paint.getTextBounds(loadingText,0,loadingText.length,textRect)
                drawText(loadingText,widthSize/2f,heightSize/2f  - textRect.exactCenterY() ,paint)

                val angel = 360 * currentProgress
                paint.color = cirleColor
                circleRect.left  = widthSize / 2f + textRect.right / 2f + 4
                circleRect.right = circleRect.left + 2*circleRadius
                drawArc(circleRect,0f,angel,true,paint)

            }else{

                paint.color = butcolor
                drawRect(buttonRec,paint)

                paint.color = textColor
                paint.getTextBounds(text,0,text.length,textRect)
                drawText(text,widthSize/2f  ,heightSize/2f - textRect.exactCenterY() , paint)

            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}
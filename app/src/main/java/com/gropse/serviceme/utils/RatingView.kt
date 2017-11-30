package com.gropse.serviceme.utils

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout

import com.gropse.serviceme.R

class RatingView : LinearLayout {

    private var ratingOngoing = false

    private var mContext: Context? = null

    private var onRateListener: OnRateListener? = null
    private var mRatingAllowed: Boolean = false
    private var mNumStars = 5
    private var mRating = 0

    constructor(context: Context) : super(context) {

        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        this.mContext = context

        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        setIsRatingAllowed(false)

        val styleAttributesArray = context.obtainStyledAttributes(attrs, R.styleable.RatingView)
        mNumStars = styleAttributesArray.getInteger(R.styleable.RatingView_numStars, mNumStars)
        mRating = styleAttributesArray.getInteger(R.styleable.RatingView_rating, mRating)

        styleAttributesArray.recycle()

        addRatingStars()
        updateViewAfterRatingChanged(mRating - 1)
    }

    private fun addRatingStars() {
        if (mNumStars != 0) {
            for (i in 0 until mNumStars) {
                addView(ratingView)
            }
        }
    }

    private val ratingView: CheckBox
        get() {
            val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val checkBox = inflater.inflate(R.layout.rating_star_item, this, false) as CheckBox
            checkBox.id = childCount
            checkBox.setOnCheckedChangeListener(onCheckChangeListener)

            return checkBox
        }

    fun setRatingListener(onRateListener: OnRateListener) {
        this.onRateListener = onRateListener
    }

    private val onCheckChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (ratingOngoing) {
            return@OnCheckedChangeListener
        }
        if (buttonView.id >= 0 && buttonView.id <= childCount - 1) {
            updateViewAfterRatingChanged(buttonView.id)
        }
    }

    private fun updateViewAfterRatingChanged(checkedPosition: Int) {
        this.mRating = checkedPosition + 1
        ratingOngoing = true
        if (checkedPosition < childCount) {
            for (i in 0 until childCount) {
                (getChildAt(i) as CheckBox).isChecked = i <= checkedPosition
            }
            if (onRateListener != null) {
                onRateListener?.onRated(checkedPosition + 1, childCount)
            }
        }
        ratingOngoing = false
    }

    fun setIsRatingAllowed(mRatingAllowed: Boolean) {
        this.mRatingAllowed = mRatingAllowed
        setRatingStarsCheckAllowed()
    }

    private fun setRatingStarsCheckAllowed() {
        if (childCount != 0) {
            (0 until childCount)
                    .map { getChildAt(it) as CheckBox }
                    .forEach { it.isEnabled = this.mRatingAllowed }
        }
    }

    var rating: Int
        get() = mRating
        set(mRating) {
            this.mRating = mRating
            updateViewAfterRatingChanged(this.mRating - 1)
        }

    interface OnRateListener {
        fun onRated(ratingGiven: Int, totalRating: Int)
    }
}

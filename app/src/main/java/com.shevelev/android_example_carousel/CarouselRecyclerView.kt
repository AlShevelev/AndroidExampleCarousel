package com.shevelev.android_example_carousel

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.pow

class CarouselRecyclerView(
    context: Context,
    attrs: AttributeSet
) : RecyclerView(context, attrs) {

    private companion object {
        const val SCROLL_START = 1
        const val SCROLL_END = 0
        const val SCROLLING_FAST = 2
    }

    private var offsetToCenterScroll = -1
    private var lastItemTag: CarouselItemTag? = null

    private var currentScrollState = -1

    private var onItemSelectedListener: ((String) -> Unit)? = null
    private var lastPostId: String? = null

    fun <T : ViewHolder> initialize(newAdapter: Adapter<T>) {
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                post {
                    if(offsetToCenterScroll == -1) {
                        val child = getChildAt(0)
                        offsetToCenterScroll = -child.width/2
                    }

                    val sidePadding = (width / 2) - (getChildAt(0).width / 2)
                    setPadding(sidePadding, 0, sidePadding, 0)
                    scrollToPosition(0)

                    addOnScrollListener(object : OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            onScrollChanged()
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)

                            if(newState == SCROLL_END) {
                                lastItemTag?.let {
                                    scrollToPosition(it.position)

                                    if(currentScrollState == SCROLLING_FAST || currentScrollState == SCROLL_START) {
                                        postOnItemSelectedEvent(it.id)
                                    }
                                }
                            }
                            currentScrollState = newState
                        }
                    })
                }
            }
        })
        adapter = newAdapter
    }

    fun scrollToStartIndex(index: Int) {
        post{
            val startPosition = (adapter as CarouselAdapter).calculateStartPosition(index)
            (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(startPosition, offsetToCenterScroll)
        }
    }

    override fun scrollToPosition(position: Int) {
        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offsetToCenterScroll)
    }

    override fun smoothScrollToPosition(position: Int) {
        val scroller = object : LinearSmoothScroller(context) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return 0
            }
        }
        scroller.targetPosition = position
        (layoutManager as LinearLayoutManager).startSmoothScroll(scroller)
    }

    fun setOnItemSelectedListener(listener: ((String) -> Unit)?) {
        onItemSelectedListener = listener
    }

    private fun onScrollChanged() {
        post {
            val parentCenterX = (left + right) / 2

            var maxScale = Float.MIN_VALUE
            var maxScaleChildTag: CarouselItemTag? = null

            (0 until childCount).forEach { position ->
                val child = getChildAt(position)

                val childCenterX = (child.left + child.right) / 2
                val scaleValue = getScale(childCenterX, parentCenterX)

                val alpha = scaleValue - 1f

                child.scaleX = scaleValue
                child.scaleY = scaleValue
                child.alpha = alpha

                if(scaleValue > maxScale) {
                    maxScale = scaleValue
                    maxScaleChildTag = child.tag as CarouselItemTag
                }
            }

            if(maxScaleChildTag != lastItemTag) {
                lastItemTag = maxScaleChildTag

                if(currentScrollState != SCROLLING_FAST) {
                    postOnItemSelectedEvent(maxScaleChildTag!!.id)
                }
            }
        }
    }

    private fun getScale(childCenterX: Int, parentCenterX: Int): Float {
        val minScaleOffset = 1f
        val scaleFactor = 1f
        val spreadFactor = 300.0

        return (Math.E.pow(-(childCenterX - parentCenterX.toDouble()).pow(2.0) /
                (2 * spreadFactor.pow(2.0))) * scaleFactor + minScaleOffset)
            .toFloat()
    }

    private fun postOnItemSelectedEvent(id: String) {
        if(lastPostId == id) {
            return
        }
        lastPostId = id

        onItemSelectedListener?.invoke(id)
    }
}
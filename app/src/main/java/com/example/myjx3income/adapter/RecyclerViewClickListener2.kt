package com.example.myjx3income.adapter

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


class RecyclerViewClickListener2(
    context: Context?,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) :
    OnItemTouchListener {
    private val mGestureDetector: GestureDetector

    //内部接口，定义点击方法以及长按方法
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onItemLongClick(view: View?, position: Int)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        //把事件交给GestureDetector处理
        return mGestureDetector.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    init {
        mGestureDetector = GestureDetector(context,
            object : SimpleOnGestureListener() {
                //这里选择SimpleOnGestureListener实现类，可以根据需要选择重写的方法
                //单击事件
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val childView: View? = recyclerView.findChildViewUnder(e.x, e.y)
                    if (childView != null && mListener != null) {
                        mListener.onItemClick(
                            childView,
                            recyclerView.getChildLayoutPosition(childView)
                        )
                        return true
                    }
                    return false
                }

                //长按事件
                override fun onLongPress(e: MotionEvent) {
                    val childView: View? = recyclerView.findChildViewUnder(e.x, e.y)
                    if (childView != null && mListener != null) {
                        mListener.onItemLongClick(
                            childView,
                            recyclerView.getChildLayoutPosition(childView)
                        )
                    }
                }
            })
    }
}
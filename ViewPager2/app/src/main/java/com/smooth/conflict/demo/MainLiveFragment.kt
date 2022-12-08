package com.smooth.conflict.demo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.integration.testapp.CELL_COLORS
import androidx.viewpager2.integration.testapp.R
import androidx.viewpager2.integration.testapp.matchParent
import com.xunlei.tdlive.pulltorefresh.LogTag
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase
import com.xunlei.tdlive.pulltorefresh.PullToRefreshBase.OnRefreshListener2
import com.xunlei.tdlive.pulltorefresh.PullToRefreshRecyclerView2

/*
*  @author      : xunlei
*  @date        : 2022/12/8
*  @desc        : 1.0
*/
open class MainLiveFragment : Fragment() {

    val dataList: ArrayList<Data> = ArrayList()
    var mContainer: PullToRefreshRecyclerView2? = null
    var adapter: Adapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContainer = view.findViewById(R.id.container)
        mContainer?.mode = PullToRefreshBase.Mode.PULL_FROM_START
        mContainer?.setOnRefreshListener(object : OnRefreshListener2<RecyclerView> {
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<RecyclerView>) {
                Log.d(LogTag.TAG_LIVE_SMOOTH, "onPullDownToRefresh")
                mContainer?.postDelayed(runnable, 200)
            }

            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<RecyclerView>) {
                Log.d(LogTag.TAG_LIVE_SMOOTH, "onPullUpToRefresh")
            }
        })
        val manager = LinearLayoutManager(view.context)
        manager.orientation = LinearLayoutManager.VERTICAL
        mContainer?.refreshableView?.layoutManager = manager
        mContainer?.refreshableView?.isVerticalScrollBarEnabled = false
        mContainer?.refreshableView?.isHorizontalScrollBarEnabled = false
        adapter = Adapter(dataList)
        mContainer?.refreshableView?.adapter = adapter

        mContainer?.refreshableView?.addOnScrollListener(mOnScrollListener)

    }


    private val mOnScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
            }

    private var runnable = Runnable {
        val data1 = Data()
        data1.orientation = RecyclerView.HORIZONTAL
        for (item in 1..30) {

            data1.dataList.add(Item("$item"))
        }

        val data2 = Data()
        data2.orientation = RecyclerView.VERTICAL
        for (item in 1..30) {
            data2.dataList.add(Item("$item"))
        }

        dataList.clear()
        dataList.add(data1)
        dataList.add(data2)
        adapter = Adapter(dataList)
        mContainer?.refreshableView?.adapter = adapter

        mContainer?.onRefreshComplete()
    }

    class RvAdapter(var data: Data) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {
        override fun getItemCount(): Int {
            return data.dataList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context)
            tv.layoutParams = matchParent().apply {
                if (data.orientation == RecyclerView.HORIZONTAL) {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            tv.textSize = 20f
            tv.gravity = Gravity.CENTER
            tv.setTextColor(Color.GREEN)
            tv.setPadding(20, 55, 20, 55)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder) {
                tv.text =  data.dataList[position].title
                tv.setBackgroundResource(CELL_COLORS[position % CELL_COLORS.size])
            }
        }

        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        var recyclerView: RecyclerView? = null

        fun bindData(data: Data) {

            recyclerView?.let {
                val mg = LinearLayoutManager(it.context)
                mg.orientation = data.orientation
                it.layoutManager = mg
                it.adapter = RvAdapter(data)
            }
        }


        init {
            recyclerView = view.findViewById(R.id.recyclerview)
        }
    }

    class Item {
        var title: String = ""

        constructor(title: String) {
            this.title = title
        }
    }

    class Data {
        var orientation: Int = 0
        var dataList: ArrayList<Item> = ArrayList()
    }

    inner class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private var dataList: ArrayList<Data> = ArrayList()

        constructor(dataList: ArrayList<Data>) {
            this.dataList = dataList
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(context).inflate(R.layout.xllive_rv, parent, false)
            return Holder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data: Data = dataList[position]
            if (holder is Holder) {
                holder.bindData(data)
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }
    }
}
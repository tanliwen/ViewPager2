package com.smooth.conflict.demo

import android.graphics.Color
import android.os.Bundle
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
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.xunlei.tdlive.smartrefresh.XLSmartRefreshLayout

/*
*  @author      : xunlei
*  @date        : 2022/12/8
*  @desc        : 1.0
*/
open class MainLiveFragmentNew : Fragment(), OnRefreshLoadMoreListener {

    private val dataList: ArrayList<Data> = ArrayList()
    var mRefreshLayout: XLSmartRefreshLayout? = null
    var mRecyclerView: RecyclerView? = null
    var adapter: Adapter? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_live_new, container, false)
        return inflater.inflate(R.layout.fragment_live_new_no_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRefreshLayout = view.findViewById(R.id.refresh_layout)
        mRecyclerView = view.findViewById(R.id.recyclerViewRoot)
        mRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter = Adapter(dataList)
        mRecyclerView?.adapter = adapter

        mRefreshLayout?.setEnableRefresh(true)
        mRefreshLayout?.setEnableFooterFollowWhenNoMoreData(false)
        mRefreshLayout?.setEnableLoadMoreWhenContentNotFull(false)
        mRefreshLayout?.setOnRefreshLoadMoreListener(this)

    }

    private val mOnScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
            }

    private var runnable = Runnable {
        val data1 = Data()
        data1.orientation = RecyclerView.HORIZONTAL
        data1.backGroundColor = Color.YELLOW
        for (item in 1..30) {

            data1.dataList.add(Item("$item"))
        }

        val data2 = Data()
        data2.orientation = RecyclerView.VERTICAL
        data2.backGroundColor = Color.RED
        for (item in 1..20) {
            data2.dataList.add(Item("$item"))
        }

        val data3 = Data()
        data3.backGroundColor = Color.BLUE
        data3.orientation = RecyclerView.VERTICAL
        for (item in 1..10) {
            data3.dataList.add(Item("$item"))
        }

        dataList.clear()
        dataList.add(data1)
        dataList.add(data2)
        dataList.add(data3)
        adapter = Adapter(dataList)
        mRecyclerView?.adapter = adapter
        mRefreshLayout?.finishRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mRecyclerView?.postDelayed(runnable, 1000)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mRecyclerView?.postDelayed({ mRefreshLayout?.finishLoadMore() }, 1000)
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
                tv.setBackgroundColor(data.backGroundColor)
            }
        }

        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private var recyclerView: RecyclerView? = null

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
        var backGroundColor: Int = Color.WHITE
    }

    inner class Adapter(private var dataList: ArrayList<Data>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val view: View = LayoutInflater.from(context).inflate(R.layout.xllive_rv, parent, false)
            var view: View? = null
            view = if (viewType == RecyclerView.HORIZONTAL) {
//                LayoutInflater.from(context).inflate(R.layout.xllive_rv_no_host_v, parent, false)
                LayoutInflater.from(context).inflate(R.layout.xllive_rv_no_host_h, parent, false)
            } else {
                LayoutInflater.from(context).inflate(R.layout.xllive_rv_no_host_v, parent, false)
            }
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

        override fun getItemViewType(position: Int): Int {
            return dataList[position].orientation
        }
    }
}

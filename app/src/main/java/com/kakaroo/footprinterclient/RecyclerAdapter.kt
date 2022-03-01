package com.kakaroo.footprinterclient

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.kakaroo.footprinterclient.Entity.FootPrinter
import android.content.Intent




class RecyclerAdapter(private val context: Context, val listData: ArrayList<FootPrinter>?)
    : RecyclerView.Adapter<RecyclerAdapter.Holder>() {

    val mContext: Context = context
    var mCurIdx = 0
    //var index = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler,parent,false)
        return Holder(view).apply {
            //삭제버튼 클릭시 이벤트
            itemView.findViewById<Button>(R.id.bt_delete).setOnClickListener {
                var cursor = adapterPosition
                //강제로 null을 허용하기 위해 !! 사용
                var id : Long = listData?.get(cursor)?.id ?: -1L
                if(id != -1L) {
                    MainActivity().executeHttpRequest(Common.HTTP_DELETE, id)
                }
                listData?.remove(listData?.get(cursor))
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val footPrinter: FootPrinter? = listData?.get(position)
        if (footPrinter != null) {
            holder.setItem(footPrinter)
        }
    }

    override fun getItemCount(): Int = listData?.size ?: 0

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_id: TextView = itemView.findViewById(R.id.tv_id)
        private val tv_date: TextView = itemView.findViewById(R.id.tv_date)
        private val tv_time: TextView = itemView.findViewById(R.id.tv_time)
        private val tv_latitude: TextView = itemView.findViewById(R.id.tv_latitude)
        private val tv_longitude: TextView = itemView.findViewById(R.id.tv_longitude)


        fun setItem(footPrinter: FootPrinter) {
            var blongClick = false
            //index += 1
            //tv_id.text = index.toString()
            tv_id.text = footPrinter.id.toString()

            tv_date.text = footPrinter.date.toString()
            tv_time.text = footPrinter.time.toString()
            tv_latitude.text = context.getResources().getString(R.string.latitude) + ": " + footPrinter.latitude.toString()
            tv_longitude.text = context.getResources().getString(R.string.longitude) + ": " + footPrinter.longitude.toString()

            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(View.OnClickListener() {
                if(blongClick) {    //롱클릭이후 클릭이벤트도 불리기 때문에 중복처리 방지를 위해,, 다른 방법이 있을 것 같은데..
                    blongClick = false
                } else {
                    mCurIdx = adapterPosition
                    Log.d(Common.LOG_TAG, "setOnClickListener:[$mCurIdx] called")

                    if( (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
                        || (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) ){
                        ActivityCompat.requestPermissions(context as Activity, Common.MAP_PERMISSIONS, Common.MY_PERMISSION_LOCATION_ACCESS_ALL)
                    } else {
                        //MainActivity().permissionGranted(Common.MY_PERMISSION_LOCATION_ACCESS_ALL)
                        callMapActivity()
                        /*
                        val intent = Intent(context, GoogleMapsActivity::class.java)
                        var data : ArrayList<FootPrinter> = ArrayList<FootPrinter>()
                        listData?.get(adapterPosition)?.let { it1 -> data.add(it1) }
                        intent.putExtra(Common.INTENT_VALUE_NAME, data)
                        context.startActivity(intent) //액티비티 열기
                        */
                    }
                }
            })

            itemView.setOnLongClickListener(OnLongClickListener { _ ->
                blongClick = true
                val position = adapterPosition
                Log.d(Common.LOG_TAG, "setOnLongClickListener called, position: $position")
                if (position != RecyclerView.NO_POSITION) {
                    //a_itemLongClickListener.onItemLongClick(a_view, position)
                }
                false
            })
        }
    }

    fun callMapActivity() {
        val intent = Intent(mContext, MapsActivity::class.java)
        var data : ArrayList<FootPrinter> = ArrayList<FootPrinter>()

        listData?.filterIndexed { index, item ->  index >= mCurIdx && data.add(item)}

        /*for(item in listData?.get(MainActivity().mCurIdx)..listData?.last()) {
            data.add(item)
        }*/
        //listData?.forEach { data.add(it) }
        //listData?.get(MainActivity().mCurIdx)?.let { it1 -> data.add(it1) }
        intent.putExtra(Common.INTENT_VALUE_NAME, data)
        context.startActivity(intent) //액티비티 열기
    }
}
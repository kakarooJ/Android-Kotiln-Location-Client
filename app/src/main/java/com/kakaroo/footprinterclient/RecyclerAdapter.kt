package com.kakaroo.footprinterclient

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.kakaroo.footprinterclient.R
import com.kakaroo.footprinterclient.Entity.FootPrinter
import android.content.Intent




class RecyclerAdapter(private val context: Context, val listData: ArrayList<FootPrinter>?)
    : RecyclerView.Adapter<RecyclerAdapter.Holder>() {

    val mContext: Context = context

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
            var str_arr = footPrinter.time.toString().split(" ")   //"time": "2022-02-18 12:18:20"
            tv_id.text = footPrinter.id.toString()
            if(str_arr != null && str_arr.size >= 2) {
                tv_date.text = str_arr[0]
                tv_time.text = str_arr[1]
            }
            tv_latitude.text = context.getResources().getString(R.string.latitude) + ": " + footPrinter.latitude.toString()
            tv_longitude.text = context.getResources().getString(R.string.longitude) + ": " + footPrinter.longitude.toString()

            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(View.OnClickListener() {
                if(blongClick) {    //롱클릭이후 클릭이벤트도 불리기 때문에 중복처리 방지를 위해,, 다른 방법이 있을 것 같은데..
                    blongClick = false
                } else {
                    MainActivity().mCurIdx = adapterPosition
                    Log.d(Common.LOG_TAG, "setOnClickListener called")

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
        val intent = Intent(mContext, GoogleMapsActivity::class.java)
        var data : ArrayList<FootPrinter> = ArrayList<FootPrinter>()
        //TODO : Select 된 것들을 분류해서 저장
        listData?.forEach { data.add(it) }
        //listData?.get(MainActivity().mCurIdx)?.let { it1 -> data.add(it1) }
        intent.putExtra(Common.INTENT_VALUE_NAME, data)
        context.startActivity(intent) //액티비티 열기
    }
}
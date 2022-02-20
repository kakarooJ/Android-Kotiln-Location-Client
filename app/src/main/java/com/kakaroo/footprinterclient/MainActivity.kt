package com.kakaroo.footprinterclient

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakaroo.footprinterclient.Entity.FootPrinter
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import androidx.recyclerview.widget.RecyclerView
import com.kakaroo.footprinterclient.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    //object mFootPrinterList : ArrayList<FootPrinter>() { }
    public var mFootPrinterList : ArrayList<FootPrinter> = ArrayList<FootPrinter>()
    public var mCurIdx = 0

    //var mContext: Context = getActivity()

    //lateinit var mAdapter: RecyclerAdapter
    var mAdapter = RecyclerAdapter(this, mFootPrinterList)
    lateinit var mRecyclerView: RecyclerView
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    //전역변수로 binding 객체선언
    private var mBinding: ActivityMainBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mRecyclerView = binding.recyclerView

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)
        binding.recyclerView.adapter = mAdapter

        registerListener()
    }

    fun registerListener() {
        binding.btRead.setOnClickListener(ButtonListener())
        binding.btMultiSelect.setOnClickListener(ButtonListener())
    }

    inner class ButtonListener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (v != null) {
                var methodType : Int = Common.HTTP_POST
                when(v.id) {
                    R.id.bt_read -> {
                        executeHttpRequest(Common.HTTP_GET_ALL, 0)
                    }
                    R.id.bt_delete -> {
                        executeHttpRequest(Common.HTTP_DELETE, 0)
                    }
                    R.id.bt_multi_select -> {

                    }
                }
            }
        }
    }

    public fun executeHttpRequest(method : Int, id : Long) {
        //Result Text View에 출력할 Output
        var result : String = ""

        // HttpUrlConnection
        val th = Thread {

            try {
                var page =
                    Common.DEFAULT_URL + Common.URL_SLASH + Common.HTTP_REQ_METHOD_LIST[method].toLowerCase()

                if(method == Common.HTTP_GET || method == Common.HTTP_PUT || method == Common.HTTP_DELETE) {
                    page += Common.URL_SLASH + id.toString()
                } else if(method == Common.HTTP_GET_ALL) {
                    page += Common.URL_SLASH + Common.HTTP_REQ_POSTFIX_GET_ALL
                }

                var bNeedRequestParam: Boolean = false
                if (method == Common.HTTP_POST || method == Common.HTTP_PUT) {
                    bNeedRequestParam = true
                }

                var bJsonResponseParam: Boolean = true
                if (method == Common.HTTP_POST || method == Common.HTTP_PUT || method == Common.HTTP_DELETE) {
                    bJsonResponseParam = false
                }

                result += page + "\n"

                // URL 객체 생성
                val url = URL(page)
                // 연결 객체 생성
                val httpConn: HttpURLConnection = url.openConnection() as HttpURLConnection

                // Post 파라미터
                //val params = ("param=1"
                //        + "¶m2=2" + "sale")

                // 결과값 저장 문자열
                val sb = StringBuilder()

                // 연결되면
                if (httpConn != null) {
                    Log.i(Common.LOG_TAG, page + " connection succesfully")
                    result += "Connection successfully!" + "\n"
                    // 응답 타임아웃 설정
                    httpConn.setRequestProperty("Accept", "application/json")
                    httpConn.setRequestProperty("Content-type", "application/json; utf-8")
                    httpConn.setConnectTimeout(Common.HTTP_CONNECT_TIMEOUT)
                    // POST 요청방식
                    httpConn.setRequestMethod(Common.HTTP_REQ_METHOD_LIST[method])

                    // 포스트 파라미터 전달
                    //httpConn.getOutputStream().write(params.toByteArray(charset("utf-8")))

                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------
                    /*if(bNeedRequestParam) {
                        var json : String = ""
                        // build jsonObject
                        val jsonObject = JSONObject()
                        jsonObject.accumulate("title", binding.etTitle.text.toString())
                        jsonObject.accumulate("content", binding.etContent.text.toString())
                        jsonObject.accumulate("author", binding.etAuthor.text.toString())

                        // convert JSONObject to JSON to String
                        json = jsonObject.toString()

                        // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                        httpConn.doOutput = true
                        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
                        httpConn.doInput = true

                        val os : OutputStream = httpConn.outputStream
                        os.write(json.toByteArray(Charsets.UTF_8))
                        os.flush()
                        os.close()
                    }*/

                    // url에 접속 성공하면 (200)
                    var status : Int = try {
                        httpConn.responseCode
                    } catch (e: IOException) {
                        // HttpUrlConnection will throw an IOException if any 4XX
                        // response is sent. If we request the status again, this
                        // time the internal status will be properly set, and we'll be
                        // able to retrieve it.
                        Log.e(Common.LOG_TAG, "URL responseCode error :$e")
                        httpConn.responseCode
                    }
                    result += "Respose code:" + status + "\n"

                    if(status == HttpURLConnection.HTTP_OK) {
                        // 결과 값 읽어오는 부분
                        val br = BufferedReader(
                            InputStreamReader(
                                httpConn.inputStream, "utf-8"
                            )
                        )
                        var line: String?

                        while (br.readLine().also { line = it } != null) {
                            sb.append(line)
                        }
                        // 버퍼리더 종료
                        br.close()
                        Log.i(Common.LOG_TAG, "결과 문자열 :$sb")

                        if(!bJsonResponseParam) {
                            result + sb.toString()
                        } else {    //return 값이 JSON 이다.
                            mFootPrinterList.clear()    //이전 데이터를 클리어한다
                            mCurIdx = 0
                            //{"id":2,"time":"2022-02-18T12:18:20","latitude":"37.4083871","longitude":"127.0688526"}
                            // 안드로이드에서 기본적으로 제공하는 JSONObject로 JSON을 파싱
                            // getAll인 경우 JsonArray 형태이므로
                            if(method == Common.HTTP_GET_ALL) {
                                val jsonArray = JSONTokener(sb.toString()).nextValue() as JSONArray
                                for (i in 0 until jsonArray.length()) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    val id = jsonObject.getLong("id")
                                    val time = jsonObject.getString("time")
                                    val latitude = jsonObject.getDouble("latitude")
                                    val longitude = jsonObject.getDouble("longitude")
                                    result += "time: "+ time + "\nlatitude: " + latitude + "\nlongitude: " + longitude +"\n"
                                    mFootPrinterList?.add(FootPrinter(id = id, time = time, latitude = latitude, longitude = longitude))
                                }
                            } else {
                                val jsonObject = JSONObject(sb.toString())
                                val id = jsonObject.getLong("id")
                                val time = jsonObject.getString("time")
                                val latitude = jsonObject.getDouble("latitude")
                                val longitude = jsonObject.getDouble("longitude")
                                result += "time: "+ time + "\nlatitude: " + latitude + "\nlongitude: " + longitude +"\n"
                                mFootPrinterList?.add(FootPrinter(id = id, time = time, latitude = latitude, longitude = longitude))
                            }
                            mFootPrinterList.reverse()
                        }
                    }
                    // 연결 끊기
                    httpConn.disconnect()
                }

                //백그라운드 스레드에서는 메인화면을 변경 할 수 없음
                // runOnUiThread(메인 스레드영역)
                runOnUiThread {
                    //Toast.makeText(applicationContext, "응답$sb", Toast.LENGTH_SHORT).show()
                    mAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e(Common.LOG_TAG, "HttpURLConnection error :$e")
                result += "HttpURLConnection error! $e"
            } finally {
                Log.i(Common.LOG_TAG, "result: "+result)
                //binding.tvResult.setText(result)
            }
        }
        th.start()
    }

    fun showDialogBox(message : String) {
        var builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        //builder.setTitle(R.string.app_name)
        builder.setMessage(message)

        // 버튼 클릭시에 무슨 작업을 할 것인가!
        var listener = object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (p1) {
                    //DialogInterface.BUTTON_POSITIVE -> configureDB(true)
                    //DialogInterface.BUTTON_NEGATIVE -> null
                }
            }
        }
        builder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissionGranted(requestCode)
        } else {
            permissionDenied(requestCode)
        }
    }

    // 권한이 있는 경우 실행
    fun permissionGranted(requestCode: Int) {
        if(requestCode == Common.MY_PERMISSION_LOCATION_ACCESS_ALL) {
            mAdapter.callMapActivity() // 권한이 있는 경우 구글 지도를준비하는 코드 실행
        }
    }

    // 권한이 없는 경우 실행
    fun permissionDenied(requestCode: Int) {
        Toast.makeText(this
            , "권한 승인이 필요합니다."
            , Toast.LENGTH_LONG)
            .show()
    }

    /*fun callMapActivity() {
        val intent = Intent(mAdapter.mContext, GoogleMapsActivity::class.java)
        var data : ArrayList<FootPrinter> = ArrayList<FootPrinter>()
        data.add(mFootPrinterList[mCurIdx])
        //val data = mFootPrinterList[mCurIdx]

        intent.putExtra(Common.INTENT_VALUE_NAME, data)
        //TODO 선택된 아이템들을 따로 넣을 수 있게 처리
        startActivity(intent)
    }*/

}
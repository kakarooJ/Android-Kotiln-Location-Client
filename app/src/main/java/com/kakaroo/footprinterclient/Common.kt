package com.kakaroo.footprinterclient

import android.Manifest

object Common {
    val LOG_TAG                 :   String  = "Kakaroo"

    val HTTP_POST               :   Int     = 0
    val HTTP_GET                :   Int     = 1
    val HTTP_PUT                :   Int     = 2
    val HTTP_DELETE             :   Int     = 3
    val HTTP_GET_ALL            :   Int     = 4
    val HTTP_DELETE_ALL         :   Int     = 5

    val SHARED_PREF_NAME        :   String  = "shared_pref"

    val URL_SLASH               :   String  = "/"
    val DEFAULT_URL             :   String  = "http://3.35.40.166:8080"//"http://192.168.219.111:8080"//"http://10.0.2.2:8080"//"http://127.0.0.1:8080"
    val URL_PREF_KEY            :   String  = "url_key"

    val HTTP_REQ_METHOD_POST    :   String     = "POST"
    val HTTP_REQ_METHOD_GET     :   String     = "GET"
    val HTTP_REQ_METHOD_PUT     :   String     = "PUT"
    val HTTP_REQ_METHOD_DELETE  :   String     = "DELETE"
    val HTTP_REQ_METHOD_LIST = arrayOf<String>(HTTP_REQ_METHOD_POST, HTTP_REQ_METHOD_GET, HTTP_REQ_METHOD_PUT, HTTP_REQ_METHOD_DELETE, HTTP_REQ_METHOD_GET, HTTP_REQ_METHOD_DELETE)

    val HTTP_REQ_POSTFIX_GET_ALL :   String     = "list"
    val HTTP_REQ_POSTFIX_DELETE_ALL : String    = "all"

    val HTTP_CONNECT_TIMEOUT     :   Int     = 100000


    val MY_PERMISSION_LOCATION_ACCESS_ALL    =   100

    // 사용할 권한 array로 저장
    val MAP_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)

    val INTENT_VALUE_NAME           :   String      = "VALUE"

    val MARKER_WIDTH                :   Int     = 160
    val MARKER_HEIGHT               :   Int     = 160
    val MAP_ZOOM_VALUE              :   Float   = 15.0f

    val LIST_RECENT_INDEX           :   Int     = 0
}
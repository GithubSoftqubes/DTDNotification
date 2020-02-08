package com.dtdnotification

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    val TAG  = this.javaClass.canonicalName

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + resources.getString(R.string.server_key)
    private var TOPIC = "/topics/DtoDNotification"

    private val contentType = "application/json"
    var NOTIFICATION_TITLE: String? = null
    var NOTIFICATION_MESSAGE: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_send.setOnClickListener {


            NOTIFICATION_TITLE = edt_title.text.toString()
            NOTIFICATION_MESSAGE = edt_message.text.toString()

            val notification = JSONObject()
            val notifcationBody = JSONObject()


            try {
                notifcationBody.put("title", NOTIFICATION_TITLE)
                notifcationBody.put("message", NOTIFICATION_MESSAGE)
                notification.put("to", TOPIC)
                notification.put("data", notifcationBody)
            } catch (e: JSONException) {
                Log.e(TAG, "onCreate: " + e.message)
            }

            sendNotification(notification)

        }
    }

    private fun sendNotification(notification : JSONObject) {

        val jsonObjectRequest : JsonObjectRequest = object: JsonObjectRequest(FCM_API,notification,
            Response.Listener {
                response: JSONObject ->
                Log.i(TAG, "onResponse: " + response.toString())
                edt_title.setText("")
                edt_message.setText("")
            },
            Response.ErrorListener {
                Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onErrorResponse: Didn't work")
            }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = serverKey
                headers["Content-Type"] = contentType
                return headers
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }
}

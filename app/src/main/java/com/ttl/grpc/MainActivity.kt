package com.ttl.grpc

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ttl.grpc.protocol.LoginOrRegisterResponse
import com.ttl.grpc.protocol.LoginRequest
import com.ttl.grpc.protocol.MainGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var sendButton: Button? = null
    private var hostEdit: EditText? = null
    private var portEdit: EditText? = null
    private var messageEdit: EditText? = null
    private var resultText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendButton = findViewById(R.id.send_button) as Button
        hostEdit = findViewById(R.id.host_edit_text) as EditText
        portEdit = findViewById(R.id.port_edit_text) as EditText
        messageEdit = findViewById(R.id.message_edit_text) as EditText
        resultText = findViewById(R.id.grpc_response_text) as TextView
        resultText?.setMovementMethod(ScrollingMovementMethod())
    }

    fun sendMessage(view:View){
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(hostEdit!!.windowToken, 0)
        sendButton!!.isEnabled = false
        resultText!!.text = ""
       GrpcTask(this)
            .execute(
                hostEdit!!.text.toString(),
                messageEdit!!.text.toString(),
                portEdit!!.text.toString()
            )
    }
    private  inner class GrpcTask(activity: Activity) :
        AsyncTask<String?, Void?, String>() {
        private val activityReference: WeakReference<Activity>
        private var channel: ManagedChannel? = null
        override fun onPostExecute(result: String) {
            try {
                channel!!.shutdown().awaitTermination(1, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            val activity = activityReference.get() ?: return
            val resultText =
                activity.findViewById<View>(R.id.grpc_response_text) as TextView
            val sendButton =
                activity.findViewById<View>(R.id.send_button) as Button
            resultText.text = result
            sendButton.isEnabled = true
        }

        init {
            activityReference = WeakReference(activity)
        }

        override fun doInBackground(vararg params: String?): String {
            val host = params[0]
            val message = params[1]
            val portStr = params[2]
            val port =
                if (TextUtils.isEmpty(portStr)) 0 else Integer.valueOf(portStr)
            return try {
                channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
                val stub: MainGrpc.MainBlockingStub = MainGrpc.newBlockingStub(channel)
                val request = LoginRequest.newBuilder().setUsername("mirza.arslan").setPassword("12345").build()
                val reply: LoginOrRegisterResponse = stub.loginOrRegister(request)
                reply.token
            } catch (e: Exception) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                pw.flush()
                String.format("Failed... : %n%s", sw)
            }
        }


    }
}

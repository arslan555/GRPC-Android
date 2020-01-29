package com.ttl.grpc.model

import android.util.Log
import com.ttl.grpc.protocol.*
import io.grpc.ManagedChannelBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface ServerApi {
    fun getPort(): Int
    fun setPort(port:Int)
    fun getHost(): String
    fun setHost(host:String)
    fun login(username: String, password: String): Boolean
    fun register(username: String,password: String):Boolean
    fun loginOrRegister(username: String, password: String): LoginOrRegisterResponse

    class TokenMissingException: Exception("Token is missing, Call login() first")
    class Channel {
        internal var port: Int = 5351
        internal var host: String = "192.168.8.100"
        private var connector: MainGrpc.MainBlockingStub?=null
        fun setHost(host: String) {
            if (host != this.host) {
                this.host = host
                reset()
            }
        }

        fun setPort(port: Int) {
            if (port != this.port) {
                this.port = port
                reset()
            }
        }

        private fun reset() {
            connector = null
        }
        internal fun stub(): MainGrpc.MainBlockingStub{
            if (connector == null) {
                val channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext(true)
                    .build()
                connector = MainGrpc.newBlockingStub(channel)
            }
            return connector!!
        }
    }
    class Factory {
        companion object {
            fun create(): ServerApi{
             return object : ServerApi{
                 private var mToken: String? = null
                 private val mDateFormat: DateFormat
                 private var mLoggedInUser: String? = null
                 private val mChannel: Channel = Channel()
                 init {
                     mDateFormat = SimpleDateFormat("MM-dd hh:mm:ss", Locale.ENGLISH)
                 }
                 override fun getPort() = mChannel.port
                 override fun setPort(port: Int) {
                     mChannel.setPort(port)
                 }
                 override fun getHost() = mChannel.host
                 override fun setHost(host: String) {
                     mChannel.setHost(host)
                 }
                 override fun login(username: String, password: String): Boolean {
                     val request  = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
                    val response = mChannel.stub().login(request)
                     if (response.loggedIn){
                         mToken = response.token
                         mLoggedInUser = username
                         Log.i("Token","token is $mToken")
                     }else{
                         Log.i("Token","Login Failed error: ${response.error}")
                         mLoggedInUser = null
                     }
                     return response.loggedIn
                 }

                 override fun register(username: String, password: String): Boolean {
                    val request = RegisterRequest.newBuilder().setUsername(username).setPassword(password).build()
                    val response = mChannel.stub().register(request)
                     if (response.isRegistered) {
                         Log.i("Register", "Register successful")
                         mLoggedInUser = username
                     } else {
                         Log.i("Response", "Register failed, error: ${response.error}")
                         mLoggedInUser = null
                     }

                     return response.isRegistered
                 }
                 override fun loginOrRegister(username: String, password: String): LoginOrRegisterResponse {
                     val request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build()
                     val response = mChannel.stub().loginOrRegister(request)

                     if (response.loggedIn) {
                         mToken = response.token
                         Log.i("Login", "Login successful, token is $mToken")
                         mLoggedInUser = username
                     } else {
                         Log.i("Login", "Login failed, error: ${response.error}")
                         mLoggedInUser = null
                     }

                     return response
                 }

             }
            }
        }
    }

}
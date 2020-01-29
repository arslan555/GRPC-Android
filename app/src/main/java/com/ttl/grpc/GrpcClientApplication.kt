package com.ttl.grpc

import android.app.Application
import android.content.Context
import com.ttl.grpc.model.ServerApi

class GrpcClientApplication : Application() {
    private var mServerApi: ServerApi?=null
    fun getServerApi(): ServerApi {
        if (mServerApi == null) mServerApi = ServerApi.Factory.create()
        return mServerApi!!
    }

    fun setServerApi(serverApi: ServerApi) {
        mServerApi = serverApi
    }

    companion object {
        fun get(context: Context): GrpcClientApplication = context.applicationContext as GrpcClientApplication
    }
}
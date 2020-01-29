package com.ttl.grpc.presenter

interface Presenter<in V> {
    fun attachView(view: V)
    fun detachView()
}
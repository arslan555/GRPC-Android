package com.ttl.grpc.view

import kotlin.Error

interface LoginMvpView: MvpView{
    fun showLoading()
    fun loggedIn(performRegister: Boolean)
    fun showError(error:com.ttl.grpc.protocol.Error)
    fun showUserNameError(tipsId: Int)
    fun showPasswordError(tipsId: Int)
    fun resetError()

}
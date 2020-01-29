package com.ttl.grpc.presenter

import com.ttl.grpc.GrpcClientApplication
import com.ttl.grpc.model.Codes
import com.ttl.grpc.model.ServerApi
import com.ttl.grpc.protocol.Error
import com.ttl.grpc.protocol.LoginOrRegisterResponse
import com.ttl.grpc.protocol.LoginResponse
import com.ttl.grpc.view.LoginMvpView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginPresenter : Presenter<LoginMvpView>/*, LoaderManager.LoaderCallbacks<Cursor>*/ {
    private var mView: LoginMvpView? = null
    private lateinit var mServerApi: ServerApi
    override fun attachView(view: LoginMvpView) {
        mView = view
        mServerApi = GrpcClientApplication.get(view.getContext()).getServerApi()
    }

    override fun detachView() {
        mView = null
    }
    public fun attemptLgin(username: String, password: String){
        mView?.showLoading()
        Observable.create(ObservableOnSubscribe<LoginOrRegisterResponse>{ subscriber ->
            try {
               subscriber.onNext(mServerApi.loginOrRegister(username,password))
                subscriber.onComplete()
            }catch (ex: Throwable){
                subscriber.onError(ex)
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                response ->
                if (response.loggedIn){
                    mView?.loggedIn(response.performedRegister)
                }else{
                    mView?.showError(response.error)
                }
            },
                {
                    e ->
                    mView?.showError(Error.newBuilder().setCode(Codes.LOCAL_ERROR).setMessage(e.toString()).build())
                }
            )
    }


}
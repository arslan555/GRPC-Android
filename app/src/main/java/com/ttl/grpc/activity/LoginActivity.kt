package com.ttl.grpc.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import com.ttl.grpc.MainActivity
import com.ttl.grpc.R
import com.ttl.grpc.presenter.LoginPresenter
import com.ttl.grpc.protocol.Error
import com.ttl.grpc.view.LoginMvpView

class LoginActivity : AppCompatActivity(), LoginMvpView {
    private lateinit var mUserNameView: AutoCompleteTextView
    private lateinit var mPasswordView: EditText
    private lateinit var mProgressView: View
    private lateinit var mLoginFormView: View
    private lateinit var mPresenter: LoginPresenter
    private val mMockUserName = "mirza.arslan"
    private val mMockPassword = "dreamtobe"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mPresenter = LoginPresenter()
        mPresenter.attachView(this)
        // Set up the login form.
        mUserNameView = findViewById(R.id.username) as AutoCompleteTextView
        mUserNameView.setText(mMockUserName)


        mPasswordView = findViewById(R.id.password) as EditText
        mPasswordView.setText(mMockPassword)


        val signInOrRegisterBtn = findViewById(R.id.sign_in_or_register_btn) as Button
        signInOrRegisterBtn.setOnClickListener {
            mPresenter.attemptLgin(mUserNameView.text.toString(), mPasswordView.text.toString())
        }

        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }

    override fun resetError() {
        mUserNameView.error = null
        mPasswordView.error = null
    }

    override fun getContext(): Context {
        return this
    }

    override fun showPasswordError(tipsId: Int) {
        mPasswordView.error = getString(tipsId)
        mPasswordView.requestFocus()
    }

    override fun showUserNameError(tipsId: Int) {
        mUserNameView.error = getString(tipsId)
        mUserNameView.requestFocus()
    }

    override fun showLoading() {
        showProgress(true)
    }

    override fun loggedIn(performedRegister: Boolean) {
        showProgress(false)
        Snackbar.make(mLoginFormView, "complete login with performed register: $performedRegister",
            Snackbar.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun showError(error: Error) {
        showProgress(false)
        Snackbar.make(mLoginFormView, "request loginOrRegister error: ${error.code} with ${error.message}",
            Snackbar.LENGTH_LONG).show()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
            mLoginFormView.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            mProgressView.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mProgressView.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

}


package com.udacity.project4.authentication

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

enum class STATE {
    AUTHENTICATED,
    UNAUTHENTICATED,
}

class AuthenticationViewModel : ViewModel(), LifecycleObserver {

    private val _status = MutableLiveData<STATE>()
    val status: LiveData<STATE>
        get() = _status

    private var _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    private val auth = FirebaseAuth.getInstance()
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _firebaseUser.value = auth.currentUser
        if (_firebaseUser.value != null) {
            _status.value = STATE.AUTHENTICATED
        } else {
            _status.value = STATE.UNAUTHENTICATED
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun addAuthListener() {
        auth.addAuthStateListener(authListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun removeAuthListener() {
        auth.removeAuthStateListener(authListener)
    }
}
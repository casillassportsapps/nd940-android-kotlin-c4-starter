package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 123
    }

    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        viewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        lifecycle.addObserver(viewModel)

        viewModel.status.observe(this, {
            binding.welcomeTextView.setText(if (it == STATE.UNAUTHENTICATED)
                R.string.welcome_to_the_location_reminder_app
            else
                R.string.welcome_back_to_the_location_reminder_app)

            binding.loginButton.setText(if (it == STATE.UNAUTHENTICATED)
                R.string.login_register
            else
                R.string.login)
        })

        binding.loginButton.setOnClickListener {
            if (viewModel.status.value == STATE.UNAUTHENTICATED) {
                showLogin()
            } else {
                showRemindersActivity()
            }
        }
    }

    private fun showLogin() {
        val providers = mutableListOf<AuthUI.IdpConfig>()
        providers.add(AuthUI.IdpConfig.EmailBuilder().build())
        providers.add(AuthUI.IdpConfig.GoogleBuilder().build())

        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                showRemindersActivity()
            }
        }
    }

    private fun showRemindersActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()
    }
}

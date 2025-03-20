package com.example.androidproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.androidproject.databinding.ActivityAuthBinding
import com.example.androidproject.helpers.LoadingState
import com.example.androidproject.helpers.extensions.observeNotNull
import com.example.androidproject.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAuthBinding

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        authViewModel.exceptions.observe(this) {
            Snackbar.make(
                viewBinding.root,
                it.message ?: "Unknown error occurred",
                Snackbar.LENGTH_LONG
            ).show()
        }

        authViewModel.loadingState.observe(this) {
            when (it) {
                is LoadingState.Loaded -> viewBinding.pbAuth.visibility = View.GONE
                is LoadingState.Loading -> viewBinding.pbAuth.visibility = View.VISIBLE
            }
        }

        authViewModel.currentUser.observeNotNull(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            authViewModel.currentUser.removeObservers(this)
        }
    }


}
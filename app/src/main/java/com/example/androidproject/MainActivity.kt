package com.example.androidproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.androidproject.databinding.ActivityMainBinding
import com.example.androidproject.helpers.LoadingState
import com.example.androidproject.helpers.extensions.observeOnlyNull
import com.example.androidproject.viewmodels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        val bnv = viewBinding.bottomNavigation

        val hostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerMain)

        val controller = hostFragment!!.findNavController()

        NavigationUI.setupWithNavController(bnv, controller)


        mainViewModel.currentUser.observeOnlyNull(this) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        mainViewModel.exceptions.observe(this) { exc ->
            exc.message?.let {
                Snackbar.make(viewBinding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        mainViewModel.loadingState.observe(this, ::handleLoader)

    }

     fun handleLoader(it: LoadingState) {
        when (it) {
            is LoadingState.Loaded -> viewBinding.pbMain.visibility = View.GONE
            is LoadingState.Loading -> viewBinding.pbMain.visibility = View.VISIBLE
        }
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        if (!findNavController(R.id.fragmentContainerMain).popBackStack()) {
            super.onBackPressed()
        }
    }

}
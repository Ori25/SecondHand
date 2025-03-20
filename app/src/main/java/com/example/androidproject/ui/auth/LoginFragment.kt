package com.example.androidproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidproject.databinding.FragmentLoginBinding
import com.example.androidproject.dto.UserLoginForm
import com.example.androidproject.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNoAccount.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()

            if (email.isEmpty()) {
                binding.etEmailLayoutLogin.error = "Email must not be empty!"
                Snackbar.make(binding.root, "Email must not be empty!", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPasswordLayoutLogin.error = "Password must not be empty!"
                Snackbar.make(binding.root, "Password must not be empty!", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            authViewModel.signIn(UserLoginForm(email, password))

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
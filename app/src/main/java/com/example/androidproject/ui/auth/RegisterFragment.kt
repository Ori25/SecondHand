package com.example.androidproject.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidproject.databinding.FragmentRegisterBinding
import com.example.androidproject.dto.UserRegistrationForm
import com.example.androidproject.ui.ImagePickerFragment
import com.example.androidproject.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : ImagePickerFragment() {

    override val onImagePicked: (Uri?) -> Unit = {
        binding.ivRegisterUserImage.setImageURI(it)
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding get() = _binding!!

    private val authViewModel by activityViewModels<AuthViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHaveAccount.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivRegisterUserImage.setOnClickListener {
            openGallery()
        }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullNameRegister.text.toString()
            val phone = binding.etPhoneRegister.text.toString()
            val email = binding.etEmailRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()
            val address = binding.etAddressRegister.text.toString()
            binding.etFullNameLayoutRegister.error = null
            binding.etAddressLayoutRegister.error = null
            binding.etPhoneLayoutRegister.error = null
            binding.etEmailLayoutRegister.error = null
            binding.etEmailLayoutRegister.error = null

            if (fullName.isEmpty()) {
                binding.etFullNameLayoutRegister.error = "Full name must not be empty!"
                Snackbar.make(binding.root, "Full name must not be empty!", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                binding.etAddressLayoutRegister.error = "Address must not be empty!"
                Snackbar.make(binding.root, "Address must not be empty!", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                binding.etPhoneLayoutRegister.error =
                    "Phone must not be empty!"
                Snackbar.make(
                    binding.root,
                    "Phone must not be empty!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.etEmailLayoutRegister.error = "Email must not be empty!"
                Snackbar.make(binding.root, "Email must not be empty!", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPasswordLayoutRegister.error = "Password must not be empty!"
                Snackbar.make(binding.root, "Password must not be empty!", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            authViewModel.signUp(
                UserRegistrationForm(
                    fullName = fullName,
                    phone = phone,
                    email = email,
                    password = password,
                    address = address,
                    imageUri = selectedImage
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
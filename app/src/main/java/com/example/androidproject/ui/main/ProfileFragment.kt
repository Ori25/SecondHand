package com.example.androidproject.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidproject.adapters.LibraryPostListener
import com.example.androidproject.adapters.LibraryPostsAdapter
import com.example.androidproject.databinding.FragmentProfileBinding
import com.example.androidproject.helpers.extensions.observeNotNull
import com.example.androidproject.helpers.json
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.OtherUser
import com.example.androidproject.models.User
import com.example.androidproject.ui.ImagePickerFragment
import com.example.androidproject.ui.dialogs.DeletePostDialog
import com.example.androidproject.ui.dialogs.EditProfileDialogFragment
import com.example.androidproject.ui.utils.LibraryDataPopulated
import com.example.androidproject.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString


@AndroidEntryPoint
class ProfileFragment : ImagePickerFragment(), LibraryPostListener {

    override var onImagePicked: (Uri?) -> Unit = { selectedImage = it }

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = mainViewModel.currentUser.value ?: return
        val adapter = LibraryPostsAdapter(LibraryDataPopulated(currentUser = user), this)
        binding.rvMyPosts.adapter = adapter
        mainViewModel.myPostsData.observe(viewLifecycleOwner) {
            it.toPopulated()?.let { populatedFeedData ->
                adapter.setData(populatedFeedData)
            }
        }

        binding.profileSettingsLayout.ivProfileEdit.setOnClickListener(::showEditProfileDialog)
        // start observing changes on the current user
        observeCurrentUser()
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onLike(post: ItemPost) {
        mainViewModel.toggleLike(post)
    }

    override fun onShowMore(post: ItemPost, owner: OtherUser) {
        val action =
            ProfileFragmentDirections.actionProfileFragmentToPostFragment(
                json.encodeToString(post),
                json.encodeToString(owner)
            )
        findNavController().navigate(action)
    }

    override fun onEdit(post: ItemPost) {
        val action = ProfileFragmentDirections.actionProfileFragmentToCreatePostFragment(
            json.encodeToString(post)
        )
        findNavController().navigate(action)
    }

    override fun onDelete(post: ItemPost) {
        DeletePostDialog.showDeleteDialog(
            context = requireContext(),
            post = post
        ) {
            mainViewModel.deletePost(post)
        }
    }


    private fun observeCurrentUser() {
        mainViewModel.currentUser.observeNotNull(viewLifecycleOwner) {
            if (it.image.isNotEmpty()) {

                Picasso.get()
                    .load(it.image)
                    .into(binding.profileSettingsLayout.ivProfile)
            } else
                Picasso.get()
                    .load(User.DEFAULT_IMAGE)
                    .into(binding.profileSettingsLayout.ivProfile)
            binding.profileSettingsLayout.tvProfileAddress.text = it.address
            binding.profileSettingsLayout.tvProfilePhone.text = it.phone

            binding.profileSettingsLayout.tvProfileEmail.text = it.email
            binding.profileSettingsLayout.tvProfileFullName.text = it.fullName
        }
    }


    private fun showEditProfileDialog(v: View) {
        val currentUser = mainViewModel.currentUser.value ?: return
        EditProfileDialogFragment(
            currentUser,
            // when dialog is dismissed make sure
            // selected image is cleaned up
            dismissCallback = { selectedImage = null },
            completeCallback = { editData ->
                val image = selectedImage
                mainViewModel.updateUser(editData, image) {
                    Snackbar.make(binding.root, "Update successfully", Snackbar.LENGTH_SHORT)
                        .show()
                }
            },
            openGalleryCallback = { imageView ->
                // when image pickedn
                // set it as image for the imageView inside dialog
                // after the update, set the callback
                // back to a normal selection callback
                onImagePicked = {
                    selectedImage = it
                    imageView.setImageURI(it)
                    onImagePicked = { uri ->
                        selectedImage = uri
                    }
                }
                openGallery()
            }
        ).show(childFragmentManager, "EditDetailsDialog")
    }
}
package com.example.androidproject.ui.main.posts

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidproject.MyApp
import com.example.androidproject.databinding.FragmentCreatePostBinding
import com.example.androidproject.helpers.json
import com.example.androidproject.models.ItemPost
import com.example.androidproject.ui.ImagePickerFragment
import com.example.androidproject.ui.dialogs.LibraryPostExtrasDialog
import com.example.androidproject.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePostFragment : ImagePickerFragment() {

    override val onImagePicked: (Uri?) -> Unit = {
        binding.ivLibImage.setImageURI(it)
    }

    private var _binding: FragmentCreatePostBinding? = null
    private val binding: FragmentCreatePostBinding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val args by navArgs<CreatePostFragmentArgs>()

    private var extrasDialog: LibraryPostExtrasDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivLibImage.setOnClickListener {
            openGallery()
        }

        val categories = MyApp.getSecondHandCategories()
        binding.CategorySpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        // if we have existing post json from previous screen
        // decode it
        val existingPost = args.existingPost?.let {
            json.decodeFromString<ItemPost>(it)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // initialize existingPost data
        existingPost?.let {
            binding.etProductName.setText(it.title)
            binding.CategorySpinner.setSelection(categories.indexOf(it.category))
            binding.etContent.setText(it.content)
            binding.etProductPrice.setText(it.price.toString())
            Picasso.get()
                .load(it.image)
                .into(binding.ivLibImage)
            binding.btnPublishPost.text = "Save Changes"
        }

        binding.btnPublishPost.setOnClickListener {
            val name = binding.etProductName.text.toString()
            val content = binding.etContent.text.toString()
            val price = binding.etProductPrice.text.toString()

            val category = categories[binding.CategorySpinner.selectedItemPosition]
            if (name.isEmpty()) {
                binding.etProductNameLayout.error =
                    "Product name must not be empty!"
                Snackbar.make(
                    binding.root,
                    "Product name must not be empty!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                binding.etLayoutContent.error = "Library content must not be empty!"
                Snackbar.make(
                    binding.root,
                    "Library content must not be empty!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (price.isEmpty()) {
                binding.etProductPriceLayout.error = "Price must not be empty!"
                Snackbar.make(
                    binding.root,
                    "Price must not be empty!",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (extrasDialog == null) {
                extrasDialog = LibraryPostExtrasDialog(existingPost)
            }

            extrasDialog?.setOnExtras { linksExtras, tagsExtras ->
                binding.btnPublishPost.isEnabled = false
                val links = linksExtras.toMutableList()
                val tags = tagsExtras.toMutableList()
                if (existingPost != null) {
                    if (existingPost.title == name
                        && existingPost.content == content
                        && existingPost.price == price.toDouble()
                        && existingPost.links == links
                        && existingPost.tags == tags
                        && selectedImage == null
                    ) {
                        //  no changes were made
                        finishCreateUpdate()
                        return@setOnExtras
                    }
                    // update post if changes were made
                    existingPost.title = name
                    existingPost.content = content
                    existingPost.price = price.toDouble()
                    existingPost.links = links
                    existingPost.tags = tags
                    existingPost.category = category
                    mainViewModel.updatePost(
                        post = existingPost,
                        imageUri = selectedImage,
                        positiveCallback = ::finishCreateUpdate,
                        negativeCallback = {
                            binding.btnPublishPost.isEnabled = true
                        }
                    )
                } else {
                    val post = ItemPost(
                        title = name,
                        content = content,
                        price = price.toDouble(),
                        links = links,
                        category = category,
                        tags = tags
                    )
                    mainViewModel.createPost(
                        post = post,
                        imageUri = selectedImage,
                        positiveCallback = ::finishCreateUpdate,
                        negativeCallback = {
                            binding.btnPublishPost.isEnabled = true
                        })
                }

            }
            extrasDialog?.show(childFragmentManager, "Upload post dialog")
        }
    }

    fun finishCreateUpdate() {
        // pop backstack
        findNavController().popBackStack()
        Toast.makeText(
            requireContext(),
            "Post created successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
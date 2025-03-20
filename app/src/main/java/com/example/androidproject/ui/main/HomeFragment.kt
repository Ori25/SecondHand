package com.example.androidproject.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidproject.MyApp
import com.example.androidproject.adapters.LibraryPostListener
import com.example.androidproject.adapters.LibraryPostsAdapter
import com.example.androidproject.databinding.FragmentHomeBinding
import com.example.androidproject.helpers.extensions.observeNotNull
import com.example.androidproject.helpers.json
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.OtherUser
import com.example.androidproject.ui.dialogs.DeletePostDialog
import com.example.androidproject.ui.utils.LibraryDataPopulated
import com.example.androidproject.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import java.time.LocalDateTime

@AndroidEntryPoint
class HomeFragment : Fragment(), LibraryPostListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.fetchCurrentWeather()
        mainViewModel.currentUser.observeNotNull(viewLifecycleOwner) {

            val timeNow = LocalDateTime.now()

            val greeting = when (timeNow.hour) {
                in 21..23, in 0..4 -> {
                    "Good night"
                }

                in 17..20 -> {
                    "Good evening"
                }

                in 13..16 -> {
                    "Good afternoon"
                }

                else -> {
                    "Good morning"
                }
            }
            binding.userNameTv.text = String.format("%s %s", greeting, it.fullName)
        }

        val categories = MyApp.getSecondHandCategories().toMutableList()
        categories.add("All")

        binding.CategorySpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)





        val user = mainViewModel.currentUser.value ?: return
        val adapter = LibraryPostsAdapter(LibraryDataPopulated(currentUser = user), this)
        binding.rvLibraryPosts.adapter = adapter
        binding.CategorySpinner.setSelection(categories.size-1)


        mainViewModel.getCurrentWeather().observe(viewLifecycleOwner) {
            binding.tvWeather.text = String.format("Current weather: %s°C", it.current.temp_c)
        }
        mainViewModel.libraryData.observe(viewLifecycleOwner) {
            it.toPopulated()?.let { populatedFeedData ->
                adapter.setData(populatedFeedData)
            }
            it.allPosts?.let  {
                if(it.isNotEmpty()) {
                    binding.noYet.visibility = View.GONE
                }
            }
        }
        binding.CategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val category = categories[p2]
                    adapter.filterCategory(category)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        binding.searchBtnLayout.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNpmRegistryFragment()
            findNavController().navigate(action)
        }
        binding.btnLogout.setOnClickListener {
            mainViewModel.signOut()
        }
        binding.searchLayoutLibraryPosts.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPostSearchFragment()
            findNavController().navigate(action)
        }
    }

    override fun onLike(post: ItemPost) {
        mainViewModel.toggleLike(post)
    }

    override fun onShowMore(post: ItemPost, owner: OtherUser) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToPostFragment(
                json.encodeToString(post),
                json.encodeToString(owner)
            )
        findNavController().navigate(action)
    }

    override fun onEdit(post: ItemPost) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToCreatePostFragment(json.encodeToString(post))
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.example.androidproject.ui.main.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidproject.MainActivity
import com.example.androidproject.adapters.LibraryPostListener
import com.example.androidproject.adapters.LibraryPostsAdapter
import com.example.androidproject.databinding.FragmentPostSearchBinding
import com.example.androidproject.helpers.extensions.observeNotNull
import com.example.androidproject.helpers.json
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.OtherUser
import com.example.androidproject.ui.dialogs.DeletePostDialog
import com.example.androidproject.ui.utils.LibraryDataPopulated
import com.example.androidproject.viewmodels.MainViewModel
import com.example.androidproject.viewmodels.PostSearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString

@AndroidEntryPoint
class PostSearchFragment : Fragment(), LibraryPostListener {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<PostSearchViewModel>()

    private var _binding: FragmentPostSearchBinding? = null
    private val binding: FragmentPostSearchBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.setLibraryData(mainViewModel.libraryData)
        _binding = FragmentPostSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchView.setOnSearchClickListener {
            binding.tvSearchIndicator.visibility = View.GONE
        }
        binding.searchView.setOnCloseListener {
            binding.tvSearchIndicator.visibility = View.VISIBLE
            false
        }

        binding.tvSearchIndicator.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocus()
        }

        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let { query ->
                    Log.d("onQueryTextChange", query)
                    viewModel.search(query)
                }
                return true
            }
        })

        val user = mainViewModel.currentUser.value ?: return
        val adapter = LibraryPostsAdapter(LibraryDataPopulated(currentUser = user), this)
        binding.rvResults.adapter = adapter
        viewModel.loadingState.observe(viewLifecycleOwner) {
            (requireActivity() as? MainActivity)?.handleLoader(it)
        }
        viewModel.searchLiveData.observeNotNull(viewLifecycleOwner) {
            it.toPopulated()?.let { populatedFeedData ->
                adapter.setData(populatedFeedData)
            }
        }

        viewModel.exceptions.observeNotNull(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it.message ?: "Faulted while querying npm registry",
                Snackbar.LENGTH_SHORT
            ).show()
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
            PostSearchFragmentDirections.actionPostSearchFragmentToPostFragment(
                json.encodeToString(post),
                json.encodeToString(owner)
            )
        findNavController().navigate(action)
    }

    override fun onEdit(post: ItemPost) {
        val action =
            PostSearchFragmentDirections.actionPostSearchFragmentToCreatePostFragment(
                json.encodeToString(
                    post
                )
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeLibraryDataSource(mainViewModel.libraryData)
    }

}
package com.example.androidproject.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidproject.adapters.LibraryPostListener
import com.example.androidproject.adapters.LibraryPostsAdapter
import com.example.androidproject.databinding.FragmentFavoritesBinding
import com.example.androidproject.helpers.json
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.OtherUser
import com.example.androidproject.ui.dialogs.DeletePostDialog
import com.example.androidproject.ui.utils.LibraryDataPopulated
import com.example.androidproject.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString


@AndroidEntryPoint
class FavoritesFragment : Fragment(), LibraryPostListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = mainViewModel.currentUser.value ?: return
        val adapter = LibraryPostsAdapter(LibraryDataPopulated(currentUser = user), this)
        binding.rvFavoritePosts.adapter = adapter
        binding.noPosts.visibility = View.VISIBLE
        mainViewModel.favoritesData.observe(viewLifecycleOwner) {
            it.toPopulated()?.let { populatedFeedData ->
                adapter.setData(populatedFeedData)
                binding.noPosts.visibility = if(populatedFeedData.allPosts.isNotEmpty()) {
                    View.GONE
                }
                else {
                    View.VISIBLE
                }
            }
        }

//        binding.btnBack.setOnClickListener {
//            findNavController().popBackStack()
//        }
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
            FavoritesFragmentDirections.actionFavoritesFragmentToPostFragment(
                json.encodeToString(post),
                json.encodeToString(owner)
            )
        findNavController().navigate(action)
    }

    override fun onEdit(post: ItemPost) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToCreatePostFragment(
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
}
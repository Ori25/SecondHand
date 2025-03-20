package com.example.androidproject.ui.main.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidproject.R
import com.example.androidproject.databinding.FragmentPostBinding
import com.example.androidproject.helpers.extensions.setupLinksAdapter
import com.example.androidproject.helpers.json
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.OtherUser
import com.example.androidproject.viewmodels.MainViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostFragment : Fragment() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var _binding: FragmentPostBinding? = null
    private val binding: FragmentPostBinding get() = _binding!!
    private val args: PostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val post = json.decodeFromString<ItemPost>(args.post)
        val owner = json.decodeFromString<OtherUser>(args.owner)


        if (post.image.isNotEmpty()) {
            Picasso.get()
                .load(post.image)
                .into(binding.ivPost)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivFavorite.setOnClickListener {
            mainViewModel.toggleLike(post)
        }

        binding.tvOwnerName.text = String.format("by %s", owner.fullName)
        binding.tvPostVersion.text = String.format("Version: %s", post.version)
        binding.tvPostTitle.text = post.title

        binding.tvPostLinks.setupLinksAdapter(post.links, ::startActivity)

        binding.tvPostTags.text = String.format("Tags: %s", post.tags.joinToString(","))
        binding.tvPostContent.text = post.content

        mainViewModel.currentUser.observe(viewLifecycleOwner) {
            val isLiked = it?.favoritePosts?.contains(post.id) ?: return@observe // O(n)
            binding.ivFavorite.setImageResource(
                if (isLiked) {
                    R.drawable.start_filled
                } else {
                    R.drawable.star_border
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
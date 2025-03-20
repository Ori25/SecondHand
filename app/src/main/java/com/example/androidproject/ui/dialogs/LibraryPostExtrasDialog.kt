package com.example.androidproject.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.androidproject.databinding.LibraryPostExtrasBinding
import com.example.androidproject.models.ItemPost
import com.google.android.material.snackbar.Snackbar

class LibraryPostExtrasDialog(
    private val existingPost: ItemPost? = null,
) : DialogFragment() {
    private lateinit var onExtras: (links: List<String>, tags: List<String>) -> Unit
    private var _binding: LibraryPostExtrasBinding? = null
    private val binding: LibraryPostExtrasBinding get() = _binding!!

    fun setOnExtras(onExtras: (links: List<String>, tags: List<String>) -> Unit) {
        this.onExtras = onExtras
    }

    private val linkSet = if (existingPost != null) {
        HashSet(existingPost.links)
    } else {
        hashSetOf()
    }
    private val tagSet = if (existingPost != null) {
        HashSet(existingPost.tags)
    } else {
        hashSetOf()
    }

    private lateinit var tagListAdapter: ArrayAdapter<String>
    private lateinit var linksListAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LibraryPostExtrasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tagListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            linkSet.toMutableList()
        )
        linksListAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                tagSet.toMutableList()
            )

        binding.tagsListView.adapter = tagListAdapter
        binding.linksListView.adapter = linksListAdapter

        binding.btnAddLink.setOnClickListener {
            val link = binding.etLinks.text.toString()
            if (link.isEmpty()) {
                binding.etLinksLayout.error =
                    "Cannot add empty Link!"
                Snackbar.make(
                    binding.root,
                    "Cannot add empty Link!",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            linkSet.add(link)
            linksListAdapter.clear()
            linksListAdapter.addAll(linkSet.toList())
        }


        binding.btnAddTag.setOnClickListener {
            val link = binding.etTags.text.toString()
            if (link.isEmpty()) {
                binding.etTagsLayout.error =
                    "Cannot add empty tag!"
                Snackbar.make(
                    binding.root,
                    "Cannot add empty tag!",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            tagSet.add(link)
            tagListAdapter.clear()
            tagListAdapter.addAll(tagSet.toList())
        }


        binding.btnDismiss.setOnClickListener {
            dismiss()
        }

        binding.btnSaveChanges.setOnClickListener {
            onExtras.invoke(linkSet.toList(), tagSet.toList())
            dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
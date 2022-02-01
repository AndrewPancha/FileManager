package com.example.filemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.databinding.FragmentSearchFilesListBinding
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val SEARCH = "search"

class SearchFilesListFragment : Fragment() {
    private lateinit var search: String
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: FragmentSearchFilesListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            search = it.getString(SEARCH).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchFilesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onFolderClicked = { file: FileModel ->
            (activity as OnFileClickListener).onClick(file)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_search_files)
        val adapter = FilesAdapter(context, onFolderClicked)

        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = adapter

        viewModel.findFilesAndFolders(search)

        lifecycleScope.launch {
            viewModel.searchFiles.collect {
                adapter.submitList(it)
            }

            viewModel.state.collect {
                when (it) {
                    State.LOADING -> binding.pbLoading.visibility = View.VISIBLE
                    State.FINISHED -> binding.pbLoading.visibility = View.GONE
                }
            }
        }
    }

    companion object {

        fun newInstance(search: String) =
            SearchFilesListFragment().apply {
                arguments = Bundle().apply {
                    putString(SEARCH, search)
                }
            }
    }
}

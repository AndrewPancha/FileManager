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
import com.example.filemanager.model.FileModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val PATH = "path"

class FilesListFragment : Fragment() {
    private lateinit var path: String
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString(PATH).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_files_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onFolderClicked = { file: FileModel ->
            (activity as OnFileClickListener).onClick(file)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_files)
        val adapter = FilesAdapter(context, onFolderClicked)

        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = adapter

        viewModel.getFiles(path)

        lifecycleScope.launch {
            viewModel.files.collect {
                adapter.submitList(it)
            }
        }
    }

    companion object {

        fun newInstance(path: String) =
            FilesListFragment().apply {
                arguments = Bundle().apply {
                    putString(PATH, path)
                }
            }
    }
}

package com.example.filemanager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.bl.FileManager
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val files = MutableStateFlow(emptyList<FileModel>())
    val searchFiles = MutableStateFlow(emptyList<FileModel>())
    val state = MutableStateFlow(State.FINISHED)

    fun getFiles(path: String) {
        viewModelScope.launch {
            files.value = FileManager.getFiles(path)
        }
    }

    fun findFilesAndFolders(name: String) {
        viewModelScope.launch {
            state.value = State.LOADING
            searchFiles.value = FileManager.searchFiles(name)
            state.value = State.FINISHED
        }
    }
}

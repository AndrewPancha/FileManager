package com.example.filemanager.bl

import android.os.Environment.getExternalStorageDirectory
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.FileType
import java.io.File

object FileManager {

    private val absolutePath: String = getExternalStorageDirectory().path

    fun getMimeFileType(name: String): String {
        return if (name.endsWith(".doc") || name.endsWith(".docx")) {
            "application/msword"
        } else if (name.endsWith(".pdf")) {
            "application/pdf"
        } else if (name.endsWith(".jpg")) {
            "image/*"
        } else if (name.endsWith(".txt")) {
            "text/plain"
        } else if (name.endsWith(".3gp") || name.endsWith(".mpg") ||
            name.endsWith(".mpeg") || name.endsWith(".mpe") || name.endsWith(".mp4") || name.endsWith(
                ".avi"
            )
        ) {
            "video/*"
        } else if (name.endsWith(".wav") || name.endsWith(".mp3")) {
            "audio/x-wav"
        } else {
            ""
        }
    }

    fun getFiles(path: String = absolutePath): List<FileModel> {
        val files = ArrayList<FileModel>()

        File(path).listFiles()?.forEach {
            files.add(getFileDirItemFromFile(it))
        }

        return files
    }

    fun searchFiles(
        text: String,
        path: String = absolutePath
    ): List<FileModel> {
        val files = ArrayList<FileModel>()
        File(path).listFiles()?.sortedBy { it.isDirectory }?.forEach {
            if (it.isDirectory) {
                if (it.name.contains(text, true)) {
                    val fileDirItem = getFileDirItemFromFile(it)
                    files.add(fileDirItem)
                }

                files.addAll(searchFiles(text, it.absolutePath))
            } else {
                if (it.name.contains(text, true)) {
                    val fileDirItem = getFileDirItemFromFile(it)
                    files.add(fileDirItem)
                }
            }
        }

        return files
    }

    private fun getFileDirItemFromFile(
        file: File,
    ): FileModel {
        return FileModel(file.path, FileType.getFileType(file), file.name)
    }
}

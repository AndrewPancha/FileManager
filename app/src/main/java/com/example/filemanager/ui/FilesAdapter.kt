package com.example.filemanager.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanager.R
import com.example.filemanager.bl.FileManager.getMimeFileType
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.FileType

class FilesAdapter(var context: Context?, private var onFolderClicked: (file: FileModel) -> Unit) :
    ListAdapter<FileModel, FilesAdapter.FilesViewHolder>(FilesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.files_list_item, parent, false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val selectedFile = getItem(position)

        holder.textView.text = selectedFile.name

        if (selectedFile.fileType == FileType.FOLDER) {
            holder.imageView.setImageResource(R.drawable.folder)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_file)
        }

        holder.itemView.setOnClickListener {
            if (selectedFile.fileType == FileType.FOLDER) {
                onFolderClicked.invoke(selectedFile)
            } else {
                try {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(
                        Uri.parse(selectedFile.path),
                        getMimeFileType(selectedFile.name)
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context?.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context?.applicationContext,
                        context?.getString(R.string.cannot_open_file),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.img_file)
        var textView: TextView = itemView.findViewById(R.id.tv_filename)
    }
}

class FilesDiffCallback : DiffUtil.ItemCallback<FileModel>() {

    override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}

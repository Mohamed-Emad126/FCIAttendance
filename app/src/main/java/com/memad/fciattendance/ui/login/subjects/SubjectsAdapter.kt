package com.memad.fciattendance.ui.login.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.memad.fciattendance.databinding.SubjectItemBinding
import javax.inject.Inject

class SubjectsAdapter @Inject constructor() :
    ListAdapter<String, SubjectsAdapter.SubjectItemViewHolder>(SubjectsDiffCallBack) {

    override fun submitList(list: MutableList<String>?) {
        val oldSize = list?.size ?: 0
        super.submitList(list)
        notifyItemRangeInserted(oldSize, list?.size ?: 0)
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectItemViewHolder {
        return SubjectItemViewHolder(
            SubjectItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: SubjectItemViewHolder, position: Int) {
        holder.itemBinding.subjectName.text = currentList[position].split(" - ")[0]
        holder.itemBinding.subjectDescription.text = currentList[position].split(" - ")[1]
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun addSubject(toString: String) {
        val list = currentList.toMutableList()
        list.add(toString)
        submitList(list)
        notifyItemRangeInserted(currentList.size, 1)
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////ViewHolder//////////////////////////////
    /////////////////////////////////////////////////////////////////
    inner class SubjectItemViewHolder(val itemBinding: SubjectItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}

object SubjectsDiffCallBack : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return newItem == oldItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return newItem == oldItem
    }
}
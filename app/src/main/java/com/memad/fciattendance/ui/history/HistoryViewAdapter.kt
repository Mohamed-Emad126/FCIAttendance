package com.memad.fciattendance.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.memad.fciattendance.R
import com.memad.fciattendance.data.HistoryEntity
import com.memad.fciattendance.databinding.TableItemBinding
import javax.inject.Inject


class HistoryViewAdapter @Inject constructor() :
    RecyclerView.Adapter<HistoryViewAdapter.RowViewHolder>() {

    private var historyList: List<HistoryEntity> = listOf()

    lateinit var onItemClickListener: OnItemClickListener
    fun setHistoryList(historyList: List<HistoryEntity>) {
        this.historyList = historyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = TableItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    private fun setHeaderBg(view: View) {
        view.setBackgroundResource(R.drawable.table_header_cell_bg)
    }

    private fun setContentBg(view: View) {
        view.setBackgroundResource(R.drawable.table_content_cell_bg)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val pos = holder.adapterPosition
        if (position == 0) {
            holder.apply {
                setHeaderBg(txtData)
                setHeaderBg(txtSubject)
                setHeaderBg(txtWeek)
                setHeaderBg(attendOrAssn)
                setHeaderBg(assignNum)
                setHeaderBg(assignGrade)

                txtData.text = "QR Data"
                txtSubject.text = "Subject"
                txtWeek.text = "Week"
                attendOrAssn.text = "Atte/Assi"
                assignNum.text = "Assig Num"
                assignGrade.text = "Grade"
                delete.visibility = View.INVISIBLE

            }
        } else {
            val history = historyList[pos - 1]
            holder.apply {
                setContentBg(txtData)
                setContentBg(txtSubject)
                setContentBg(txtWeek)
                setContentBg(attendOrAssn)
                setContentBg(assignNum)
                setContentBg(assignGrade)

                txtData.text = history.qrData
                txtSubject.text = history.subject
                txtWeek.text = history.week.toInt().inc().toString()
                attendOrAssn.text = history.attendOrAssign
                assignNum.text = history.assignNumber
                assignGrade.text = history.grade
            }
            holder.delete.setOnClickListener {
                onItemClickListener.onItemClickListener(pos, history)
            }
        }

    }

    override fun getItemCount(): Int {
        return historyList.size + 1
    }

    class RowViewHolder(binding: TableItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtData = binding.txtData
        val txtSubject = binding.txtSubject
        val txtWeek = binding.txtWeek
        val attendOrAssn = binding.txtAttendOrAssign
        val assignNum = binding.txtAssignNumber
        val assignGrade = binding.txtGrade
        val delete = binding.btnDelete
    }

    interface OnItemClickListener {
        fun onItemClickListener(position: Int, history: HistoryEntity)
    }
}

package com.memad.fciattendance.ui.history

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.memad.fciattendance.data.HistoryEntity
import com.memad.fciattendance.databinding.FragmentHistoryBinding
import com.memad.fciattendance.databinding.ProgressDialogBinding
import com.memad.fciattendance.utils.createDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.FileOutputStream
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryViewAdapter.OnItemClickListener {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var baseDocumentTreeUri: Uri? = null
    private lateinit var dialogBinding: ProgressDialogBinding
    private lateinit var dialog: Dialog

    @Inject
    lateinit var historyViewAdapter: HistoryViewAdapter
    private val historyViewModel: HistoryViewModel by viewModels()

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        dialogBinding = ProgressDialogBinding.inflate(inflater)
        dialog = requireActivity().createDialog(dialogBinding.root, false)
        setupUI()
        observeLiveData()
        return binding.root
    }

    private fun setupUI() {
        historyViewAdapter.onItemClickListener = this
        binding.recyclerHistory.adapter = historyViewAdapter

        binding.buttonProcessAll.setOnClickListener { processAll() }
        binding.buttonExport.setOnClickListener { exportHistory() }
        binding.buttonClear.setOnClickListener { historyViewModel.deleteAllHistory() }
        binding.historyToolbar.setNavigationOnClickListener {
            binding.root.findNavController().navigateUp()
        }
    }

    private fun observeLiveData() {
        historyViewModel.history.observe(viewLifecycleOwner) {
            historyViewAdapter.setHistoryList(it)
            dialogBinding.progressPercent.text = "0 /${it.size}"
        }
    }

    private fun processAll() {
        dialog.show()
        val history = historyViewModel.history.value
        if (history.isNullOrEmpty()) {
            Snackbar.make(requireView(), "No history to process", Snackbar.LENGTH_SHORT).show()
            dialog.dismiss()
        } else {
            enqueueWorkRequests(history)
        }
    }

    private fun enqueueWorkRequests(history: List<HistoryEntity>) {
        val workManager = WorkManager.getInstance(requireContext())
        var continuation = workManager.beginWith(createWorkRequest(history[0]))
        for (i in 1 until history.size) {
            continuation = continuation.then(createWorkRequest(history[i]))
        }
        continuation.enqueue()
        observeWorkInfos(continuation)
    }

    private fun observeWorkInfos(continuation: WorkContinuation) {
        continuation.workInfosLiveData.observe(viewLifecycleOwner) { workInfos ->
            val progress = workInfos.filter { it.state.isFinished }.size
            dialogBinding.progressPercent.text =
                "$progress /${historyViewModel.history.value?.size}"
            dialogBinding.progressBar.progress = progress
            if (workInfos.all { it.state.isFinished }) dialog.dismiss()
        }
    }

    private fun exportHistory() {
        if (baseDocumentTreeUri != null) {
            val csvContent = historyViewModel.history.value?.let { listToCsvString(it) } ?: "...."
            writeFile("Attendance:${LocalDateTime.now()}.csv", csvContent)
        } else {
            launchBaseDirectoryPicker()
        }
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            baseDocumentTreeUri = result.data?.data
            persistUriPermission()
            val csvContent = historyViewModel.history.value?.let { listToCsvString(it) } ?: "...."
            writeFile("Attendance:${LocalDateTime.now()}.csv", csvContent)
        }
    }

    private fun persistUriPermission() {
        val takeFlags: Int =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        requireActivity().contentResolver.takePersistableUriPermission(
            baseDocumentTreeUri!!,
            takeFlags
        )
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("baseDocumentTreeUri", baseDocumentTreeUri.toString())
            apply()
        }
    }

    private fun createWorkRequest(value: HistoryEntity): OneTimeWorkRequest {
        val data = Data.Builder()
            .putString("history", Gson().toJson(value))
            .build()
        return OneTimeWorkRequestBuilder<ProcessAllWorker>()
            .setInputData(data)
            .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(10))
            .build()
    }

    private fun listToCsvString(list: List<HistoryEntity>): String {
        val headers =
            listOf("QR Data", "Subject", "Week", "Attend/Assign", "Grade", "Assign Number")
                .joinToString(",")
        val csvLines = list.map { entity ->
            listOf(
                entity.qrData,
                entity.subject,
                entity.week,
                entity.attendOrAssign,
                entity.grade,
                entity.assignNumber
            ).joinToString(",")
        }
        return headers + "\n" + (csvLines).joinToString("\n")
    }

    private fun writeFile(fileName: String, content: String) {
        try {
            val directory = DocumentFile.fromTreeUri(requireContext(), baseDocumentTreeUri!!)
            val file = directory!!.createFile("CSV/*", fileName)
            val pfd = requireContext().contentResolver.openFileDescriptor(file!!.uri, "w")
            FileOutputStream(pfd!!.fileDescriptor).use { it.write(content.toByteArray()) }
            pfd.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun launchBaseDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startForResult.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClickListener(position: Int, history: HistoryEntity) {
        historyViewModel.deleteHistory(history)
    }
}
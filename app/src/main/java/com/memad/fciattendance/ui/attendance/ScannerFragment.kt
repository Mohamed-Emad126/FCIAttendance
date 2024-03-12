package com.memad.fciattendance.ui.attendance

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.memad.fciattendance.R
import com.memad.fciattendance.databinding.FragmentScannerBinding
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.ui.login.LoginActivity
import com.memad.fciattendance.ui.login.LoginViewModel
import com.memad.fciattendance.utils.AccessNative
import com.memad.fciattendance.utils.Constants
import com.memad.fciattendance.utils.NetworkStatus
import com.memad.fciattendance.utils.SharedPreferencesHelper
import com.memad.fciattendance.utils.decryptWithAES
import com.memad.fciattendance.utils.weekNum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private val options = createBarcodeScannerOptions()
    private val toastQueue = ArrayDeque<String>()
    private var currentToast: Toast? = null
    private var scanner: GmsBarcodeScanner? = null
    private var credential: GoogleAccountCredential? = null
    private val loginViewModel: LoginViewModel by viewModels()

    @Scopes
    @Inject
    lateinit var scopes: List<String>

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val mainViewModel: ScannerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        scanner = GmsBarcodeScanning.getClient(requireContext(), options)
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        val account = signedInAccount?.account
        val credential = GoogleAccountCredential.usingOAuth2(requireContext(), scopes)
        credential?.setSelectedAccountName(account?.name)
        loginViewModel.checkExistingUser(account?.name!!, credential!!)

        setupUI()
        checkAvailableAccount()
        observeFlow()
        return binding.root
    }

    private fun setupUI() {
        val subjects = sharedPreferencesHelper.readStringSet(Constants.SUBJECTS) ?: emptySet()
        binding.subjectEditText.setSimpleItems(subjects.toTypedArray())
        binding.assignOrAttenText.setSimpleItems(R.array.assign_or_attend_array)
        binding.assignNumText.setSimpleItems(R.array.assign_num_array)
        binding.assignGradeText.setSimpleItems(R.array.assign_num_array)
        setupTextChangeListeners()
        setupItemClickListener()
        setupLoginButton()
        setupHistoryButton()
        setupSelectionLogic()
        if (sharedPreferencesHelper.readBoolean(Constants.IS_VERIFIED)) {
            disableUI()
        }
    }

    private fun disableUI() {
        binding.subjectEditText.isEnabled = false
        binding.assignOrAttenText.isEnabled = false
        binding.assignNumText.isEnabled = false
        binding.assignGradeText.isEnabled = false
        binding.buttonLogin.isEnabled = false
        binding.loopScanning.isEnabled = false
        showToast("You are not authorized to scan yet")
    }

    private fun setupTextChangeListeners() {
        binding.subjectEditText.doOnTextChanged { _, _, _, _ ->
            binding.buttonLogin.isEnabled = true
        }
        binding.buttonLogin.isEnabled = binding.subjectEditText.text.isNotEmpty()

    }

    private fun setupItemClickListener() {
        binding.assignOrAttenText.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val visibility = if (position == 0) View.VISIBLE else View.GONE
                binding.assignNumLayout.visibility = visibility
                binding.assignGradeLayout.visibility = visibility
            }
    }

    private fun setupSelectionLogic() {
        val visibility =
            if (binding.assignOrAttenText.text.toString() == "Assignment")
                View.VISIBLE
            else
                View.GONE
        binding.assignNumLayout.visibility = visibility
        binding.assignGradeLayout.visibility = visibility
    }

    private fun setupLoginButton() {
        binding.buttonLogin.setOnClickListener {
            if (validateUI()) {
                handleLogin()
                startScan()
            }
        }
    }

    private fun setupHistoryButton() {
        binding.buttonHistory.setOnClickListener {
            binding.root.findNavController()
                .navigate(R.id.action_scannerFragment_to_historyFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        toastQueue.clear()
        setupUI()
    }

    private fun handleLogin() {
        checkAvailableAccount()
    }

    private fun checkAvailableAccount() {
        val signedInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (signedInAccount?.account == null) {
            gotoLogin()
        } else {
            val account = signedInAccount.account
            credential = GoogleAccountCredential.usingOAuth2(context, scopes)
            credential?.setSelectedAccountName(account?.name)
            binding.userAccount.text = signedInAccount.email
        }
    }

    private fun gotoLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.resultFlow.collect {
                    when (it) {
                        is Intent -> {
                            gotoLogin()
                        }

                        is Pair<*, *> -> showToast("${it.first}: ${it.second}")
                        is String -> showToast(it)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.networkStatus.collect {
//                Log.e("MainFragment", "Data1111: $it")
            }
        }
    }

    private fun createBarcodeScannerOptions() = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()

    private fun validateUI(): Boolean {
        if (binding.subjectEditText.text.isEmpty()) {
            binding.subjectEditText.error = "Please select a subject"
            return false
        }
        if (binding.assignOrAttenText.text.isEmpty()) {
            binding.assignOrAttenText.error = "Please select an option"
            return false
        }
        if (binding.assignOrAttenText.text.toString() == "Assignment" && binding.assignNumText.text.isEmpty()) {
            binding.assignNumText.error = "Please enter the assignment number"
            return false
        }
        if (sharedPreferencesHelper.readBoolean(Constants.IS_VERIFIED)) {
            disableUI()
        }
        return true
    }

    private fun startScan() {
        scanner?.startScan()
            ?.addOnSuccessListener { barcode ->
                val data = barcode.rawValue?.decryptWithAES(
                    AccessNative.getAESKey(),
                    AccessNative.getAESIV()
                ) ?: return@addOnSuccessListener
                if (validateUI().not()) return@addOnSuccessListener
                checkDataAndSendToSheet(data)
            }
            ?.addOnCanceledListener { showToast("Canceled") }
            ?.addOnFailureListener { showToast("Failed") }
    }

    private fun checkDataAndSendToSheet(data: String) {
        val dataParts = data.split("\n")
        if (isValidData(dataParts).not()) {
            showToast("Invalid QR Code")
            return
        }

        val subject = binding.subjectEditText.text.toString().trim()
        val subjectSheetID = Constants.SPREADSHEET_IDs[Constants.ARRAY_OF_SUBJECTS.indexOf(subject)]
        val subjectSheetName =
            Constants.NAMES_OF_SHEETS[Constants.ARRAY_OF_SUBJECTS.indexOf(subject)]

        if (mainViewModel.networkStatus.asLiveData().value in listOf(
                NetworkStatus.Unknown,
                NetworkStatus.Disconnected
            )
        ) {
            showToast("No Internet Connection\nData saved locally in History")
            mainViewModel.insertHistory(
                dataParts.reversed().joinToString(": "),
                subjectSheetName,
                weekNum().toString(),
                binding.assignOrAttenText.text.toString(),
                binding.assignNumText.text.toString(),
                binding.assignGradeText.text.toString().takeIf { it.isNotEmpty() } ?: "1")
            return
        }
        mainViewModel.sendToSheet(
            credential!!,
            dataParts,
            subjectSheetID,
            binding.assignOrAttenText.text.toString(),
            binding.assignNumText.text.toString(),
            subjectSheetName,
            binding.assignGradeText.text.toString().takeIf { it.isNotEmpty() } ?: "1")
        if (binding.loopScanning.isChecked) {
            startScan()
        }
    }

    private fun isValidData(dataParts: List<String>) =
        dataParts.size == 2 && dataParts[1].isDigitsOnly()

    private fun showToast(message: String) {
        toastQueue.add(message)
        if (currentToast == null || currentToast!!.view?.windowVisibility != View.VISIBLE) {
            displayNextToast()
        }
    }

    private fun displayNextToast() {
        if (toastQueue.isNotEmpty()) {
            currentToast =
                Toast.makeText(requireContext(), toastQueue.removeFirst(), Toast.LENGTH_SHORT)
            currentToast?.show()
            Handler(Looper.getMainLooper()).postDelayed(
                { displayNextToast() }, ((currentToast?.duration
                    ?: (0 + 350))).toLong()
            )
        } else {
            currentToast = null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainViewModel.setFirstTime()
        toastQueue.clear()
    }
}
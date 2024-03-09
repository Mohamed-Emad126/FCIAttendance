package com.memad.fciattendance.ui.subjects

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.memad.fciattendance.databinding.FragmentSubjectsBinding
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.ui.attendance.AttendanceActivity
import com.memad.fciattendance.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SubjectsFragment : Fragment() {
    private var _binding: FragmentSubjectsBinding? = null
    private val binding get() = _binding!!

    @Scopes
    @Inject
    lateinit var scopes: List<String>

    @Inject
    lateinit var httpTransport: NetHttpTransport

    lateinit var subjectsAdapter: SubjectsAdapter

    private var credential: GoogleAccountCredential? = null

    private val subjectsViewModel: SubjectsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        subjectsAdapter = SubjectsAdapter()
        binding.subjectList.adapter = subjectsAdapter
        binding.subjectProgress.visibility = View.VISIBLE
        binding.subjectCard.visibility = View.GONE
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())?.account
        credential = GoogleAccountCredential.usingOAuth2(requireContext(), scopes)
        if (account != null) {
            credential?.selectedAccountName = account.name
        }
        setupObservers()

        binding.startButton.setOnClickListener {
            val intent = Intent(requireContext(), AttendanceActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }


    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                subjectsViewModel.resultFlow
                    .collect { result ->
                        when (result.status) {
                            Status.SUCCESS -> {
                                binding.subjectProgress.visibility = View.GONE
                                binding.subjectCard.visibility = View.VISIBLE
                                subjectsAdapter.submitList(result.data?.toMutableList())
                            }

                            Status.ERROR -> {
                                binding.subjectProgress.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    result.msg ?: "An error occurred",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            Status.LOADING -> {
                                binding.subjectProgress.visibility = View.VISIBLE
                                binding.subjectCard.visibility = View.GONE
                            }
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
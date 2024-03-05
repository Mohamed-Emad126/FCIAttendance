package com.memad.fciattendance.ui.login.loginui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.memad.fciattendance.R
import com.memad.fciattendance.databinding.FragmentLoginBinding
import com.memad.fciattendance.di.annotations.Scopes
import com.memad.fciattendance.utils.NetworkStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var credential: GoogleAccountCredential? = null
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Scopes
    @Inject
    lateinit var scopes: List<String>

    private val loginViewModel: LoginViewModel by viewModels()

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleActivityResult(result)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        observeNetworkStatus()
        binding.loginWithGoogleButton.setOnClickListener {
            lifecycleScope.launch {
                authorize()
            }
        }

        return binding.root
    }

    private fun observeNetworkStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.networkStatus.collect {
                when (it) {
                    NetworkStatus.Connected -> {
                        binding.loginWithGoogleButton.isEnabled = true
                    }
                    NetworkStatus.Disconnected -> {
                        binding.loginWithGoogleButton.isEnabled = false
                        Toast.makeText(requireContext(), "No internet Connection", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        binding.loginWithGoogleButton.isEnabled = false
                    }
                }
            }
        }
    }

    private fun authorize() {
        observeNetworkStatus()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent: Intent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val signedInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
            val account = signedInAccount?.account
//            binding.userAccount.text = signedInAccount?.email
            credential = GoogleAccountCredential.usingOAuth2(context, scopes)
            credential?.setSelectedAccountName(account?.name)
            binding.root.findNavController().navigate(R.id.action_loginFragment_to_subjectsFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
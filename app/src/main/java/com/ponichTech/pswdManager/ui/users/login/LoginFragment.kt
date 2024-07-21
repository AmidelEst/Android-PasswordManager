package com.ponichTech.pswdManager.ui.users.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.repository.firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.databinding.FragmentLoginBinding
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.autoCleared

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding by autoCleared()
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory(AuthRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {

            viewModel.signInUser(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userSignInStatus.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.btnLogin.isEnabled = false
                }

                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_allItemsFragment)
                }

                is Resource.Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.btnLogin.isEnabled = false
                }

                is Resource.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_allItemsFragment)
                }

                is Resource.Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }
}
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
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.databinding.FragmentLoginBinding
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.autoCleared

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding by autoCleared()

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(AuthRepositoryFirebase())
    }

    // 1)CreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    // 2)ViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //login btn press
        binding.btnLogin.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                //login execution
                loginViewModel.loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
        //GOTO - loginFragment -> register
        binding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginViewModel.currentUser.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    //GOTO - login -> AllItems
                    loginViewModel.saveLoginState(requireContext(), true)
                    findNavController().navigate(R.id.action_loginFragment_to_allItemsFragment)
                }
                is Resource.Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.btnLogin.isEnabled = true
                    resource.message?.let {
                        Toast.makeText(requireContext(),resource.message,Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.btnLogin.isEnabled = false
                }
            }
        }
    }

}

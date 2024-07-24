package com.ponichTech.pswdManager.ui.users.register

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
import com.ponichTech.pswdManager.databinding.FragmentRegisterBinding
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.SharedPreferencesUtil
import com.ponichTech.pswdManager.utils.autoCleared

class RegisterFragment : Fragment(){

    private var binding : FragmentRegisterBinding by autoCleared()

    private val viewModel : RegisterViewModel by viewModels {
        RegisterViewModel.Factory(AuthRepositoryFirebase())
    }
    // 1)onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        //registerButton
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val phone =binding.phoneInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if(validateUserInput(email,phone,password)){
                viewModel.registerUser(name,email,phone,password)
            }
        }

        //GOTO (1) - register -> login:
        binding.tvLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }

    //2)ViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.registerStatus.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    binding.registerProgress.isVisible = true
                    binding.registerButton.isEnabled = false
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(),"Registration successful",Toast.LENGTH_SHORT).show()
                    //GOTO (2) - register -> allItems
                    SharedPreferencesUtil.updateLoginState(requireContext(), true)
                    findNavController().navigate(R.id.action_registerFragment_to_allItemsFragment)
                }
                is Resource.Error -> {
                    binding.registerProgress.isVisible = false
                    binding.registerButton.isEnabled = true
                }
            }
        }

    }


    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun validateUserInput(email: String, phone: String, password: String): Boolean {
        var isValid = true
        // Validate email
        val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
        if (!emailRegex.matches(email)) {
            notifyUser("Missing or invalid email. Format should be x@x.com")
            isValid = false
        }
        // Validate phone
        val phoneRegex = Regex("^\\d+$")
        if (!phoneRegex.matches(phone)) {
            notifyUser("Missing or invalid phone number. It should contain only digits.")
            isValid = false
        }
        // Validate password
        if (password.length < 6) {
            notifyUser("Password must be more than 6 characters.")
            isValid = false
        }
        if (isValid) {
            notifyUser("All inputs are valid!")
        }
        return isValid
    }


}


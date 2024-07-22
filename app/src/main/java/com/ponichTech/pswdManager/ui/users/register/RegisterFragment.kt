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
import com.ponichTech.pswdManager.data.repository.firebase.UserRepositoryFirebase
import com.ponichTech.pswdManager.databinding.FragmentRegisterBinding
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.autoCleared

class RegisterFragment : Fragment(){

    private var binding : FragmentRegisterBinding by autoCleared()

    private val viewModel : RegisterViewModel by viewModels {
        RegisterViewModel.RegisterViewModelFactory(UserRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)

        binding.btnRegister.setOnClickListener {
            viewModel.createUser(binding.etName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPhone.text.toString(),
                binding.etPassword.text.toString())
        }

        //Login Screen navigation:
        binding.tvLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userRegistrationStatus.observe(viewLifecycleOwner) {

            when(it) {
                is Resource.Loading -> {
                    binding.registerProgress.isVisible = true
                    binding.btnRegister.isEnabled = false
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(),"Registration successful",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_allItemsFragment)
                }
                is Resource.Error -> {
                    binding.registerProgress.isVisible = false
                    binding.btnRegister.isEnabled = true
                }
            }
        }

    }
}
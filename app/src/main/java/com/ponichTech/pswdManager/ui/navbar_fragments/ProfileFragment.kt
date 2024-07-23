package com.ponichTech.pswdManager.ui.navbar_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository

import com.ponichTech.pswdManager.data.repository.user_repository.UserRepositoryFirebase
import com.ponichTech.pswdManager.databinding.ProfileBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.PasswordsViewModel
import com.ponichTech.pswdManager.utils.autoCleared


class ProfileFragment : Fragment() {

    private var binding: ProfileBinding by autoCleared()

    private val viewModel: PasswordsViewModel by activityViewModels {
        PasswordsViewModel.Factory(
            requireActivity().application,
            UserRepositoryFirebase(),
            PasswordFirebaseRepository()
        )
    }

    //1)CreateView
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding = ProfileBinding.inflate(inflater, container, false)

        //Profile -> login
        binding.logoutButton.setOnClickListener{
            viewModel.logoutUser()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
        //Profile -> editProfile
        binding.editProfileButton.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        return binding.root
    }

}

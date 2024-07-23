package com.ponichTech.pswdManager.ui.navbar_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository

import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.databinding.ProfileBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel
import com.ponichTech.pswdManager.utils.autoCleared

class ProfileFragment : Fragment() {

    private var binding: ProfileBinding by autoCleared()

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            AuthRepositoryFirebase(),
            requireActivity().application,
            PasswordFirebaseRepository()
        )
    }

    //1)CreateView
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding = ProfileBinding.inflate(inflater, container, false)

        val user = viewModel.loggedInUser.value?.data
        binding.profileUserName?.setText(user?.name ?: "")
        binding.email?.setText(user?.email ?: "")
        // Load photo using Glide with circular crop
        Glide.with(requireContext())
            .load(user?.userPhoto)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .override(200,200) // Error image if loading fails
            .into(binding.profileImage)

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

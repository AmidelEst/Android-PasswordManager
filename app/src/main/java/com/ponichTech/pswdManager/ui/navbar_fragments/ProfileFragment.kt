package com.ponichTech.pswdManager.ui.navbar_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.repository.firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.firebase.PasswordItemRepositoryFirebase
import com.ponichTech.pswdManager.databinding.ProfileBinding
import com.ponichTech.pswdManager.ui.items.passwords_view_model.FirebasePasswordItemsViewModel


class ProfileFragment : Fragment() {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirebasePasswordItemsViewModel by viewModels {
        FirebasePasswordItemsViewModel.FirebasePasswordItemsViewModelFactory(
            AuthRepositoryFirebase(),
            PasswordItemRepositoryFirebase()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding
            .inflate(inflater, container, false)

        binding.logoutButton.setOnClickListener{
            viewModel.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.editProfileButton.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

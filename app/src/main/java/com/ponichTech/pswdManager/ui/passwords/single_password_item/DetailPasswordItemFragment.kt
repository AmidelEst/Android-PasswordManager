package com.ponichTech.pswdManager.ui.passwords.single_password_item

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
import com.ponichTech.pswdManager.databinding.FragmentDetailPasswordItemBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel
import com.ponichTech.pswdManager.utils.autoCleared


class DetailPasswordItemFragment : Fragment() {

    private var binding: FragmentDetailPasswordItemBinding by autoCleared()

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            requireActivity().application,
            AuthRepositoryFirebase(),
            PasswordFirebaseRepository()
        )
    }

    // 1) CreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        binding = FragmentDetailPasswordItemBinding.inflate(inflater, container, false)
        //Detail -> EditSingle
        binding.editDetailsBtn.setOnClickListener{
            findNavController().navigate(R.id.action_detailItemFragment_to_editSinglePasswordItemFragment)
        }
        return binding.root
    }
    //2) ViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedPasswordItem.observe(viewLifecycleOwner) {
            it?.let {
                binding.itemServiceName.text = it.serviceName
                binding.itemUserName.text = it.username
                binding.itemNotes.text = it.notes
                binding.itemPassword.text = it.password
                Glide.with(requireContext())
                    .load(it.photo)
                    .error(R.mipmap.ic_launcher)// Error image if loading fails
                    .override(200, 200)
                    .circleCrop() // Set the desired width and height in pixels
                    .into(binding.itemImage)
            }
        }
    }
}
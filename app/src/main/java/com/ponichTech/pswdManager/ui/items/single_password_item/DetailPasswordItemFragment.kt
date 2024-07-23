package com.ponichTech.pswdManager.ui.items.single_password_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.local_db.PasswordItemDatabase
import com.ponichTech.pswdManager.data.repository.firebase.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.firebase.UserRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.local_Repo.PasswordLocalRepository
import com.ponichTech.pswdManager.databinding.FragmentDetailPasswordItemBinding
import com.ponichTech.pswdManager.ui.items.all_password_items.PasswordsViewModel
import com.ponichTech.pswdManager.ui.items.all_password_items.PasswordsViewModelFactory
import com.ponichTech.pswdManager.utils.autoCleared


class DetailPasswordItemFragment : Fragment() {

    private var binding: FragmentDetailPasswordItemBinding by autoCleared()

    private val viewModel: PasswordsViewModel by activityViewModels {
        PasswordsViewModelFactory(
            requireActivity().application,
            UserRepositoryFirebase(),
            PasswordFirebaseRepository()
        )
    }

    // 1) CreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                Glide.with(requireContext()).load(it.photo).circleCrop().into(binding.itemImage)
            }
        }
    }
}
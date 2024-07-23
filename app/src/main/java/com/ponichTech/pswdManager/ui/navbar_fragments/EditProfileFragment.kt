package com.ponichTech.pswdManager.ui.navbar_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.repository.firebase.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.firebase.UserRepositoryFirebase
import com.ponichTech.pswdManager.databinding.EditProfileBinding
import com.ponichTech.pswdManager.ui.items.all_password_items.PasswordsViewModel
import com.ponichTech.pswdManager.ui.items.all_password_items.PasswordsViewModelFactory


class EditProfileFragment : Fragment() {

    private var _binding:EditProfileBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

//    private val viewModel: PasswordsViewModel by activityViewModels {
//        PasswordsViewModelFactory(
//            requireActivity().application,
//            UserRepositoryFirebase(),
//            PasswordFirebaseRepository()
//        )
//    }

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                binding.resultImage.setImageURI(it)
                requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageUri = it
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditProfileBinding
            .inflate(inflater, container, false)

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }
        binding.editProfileSaveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

//        // Observing the selected password item
//        viewModel.selectedPasswordItem.observe(viewLifecycleOwner) { item ->
//            item?.let {
//                binding.serviceName.setText(it.serviceName)
//                binding.userName.setText(it.username)
//                binding.userNote.setText(it.notes)
//                binding.userPassword.setText(it.password)
//
//                // Load photo using Glide with circular crop
//                Glide.with(requireContext())
//                    .load(it.photo)
//                    .error(R.drawable.ic_launcher_foreground) // Error image if loading fails
//                    .circleCrop()
//                    .into(binding.resultImage)
//            }
//        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

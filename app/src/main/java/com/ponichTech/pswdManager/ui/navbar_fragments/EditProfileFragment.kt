package com.ponichTech.pswdManager.ui.navbar_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
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
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.databinding.EditProfileBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel


class EditProfileFragment : Fragment() {

    private var _binding: EditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            AuthRepositoryFirebase(),
            requireActivity().application,
            PasswordFirebaseRepository()
        )
    }

    private var imageUri: Uri? = null

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                binding.resultImage.setImageURI(it)
                Glide.with(requireContext())
                    .load(it)
                    .circleCrop()
                    .into(binding.resultImage)
                requireActivity().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
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

        // Observing the selected password item
        val user = viewModel.loggedInUser.value?.data
        binding.name?.setText(user?.name ?: "")

//        binding.userName.toString().setText(user?.name)
        binding.email?.setText(user?.email ?: "")
//        binding.editPassword?.text=user.


        // Load photo using Glide with circular crop
//        Glide.with(requireContext())
//            .load(it.photo)
//            .error(R.mipmap.ic_launcher) // Error image if loading fails
//            .circleCrop()
//            .into(binding.resultImage)


        // Save changes button
//        binding.btnSaveChanges.setOnClickListener {
//            viewModel.selectedPasswordItem.value?.let { item ->
//                val updatedServiceName = binding.serviceName.text.toString()
//                val updatedUserName = binding.userName.text.toString()
//                val updatedNotes = binding.userNote.text.toString()
//                val updatedPassword = binding.userPassword.text.toString()
//                val updatedImageUri = imageUri?.toString() ?: item.photo
//
//                // Create an updated Password object
//                val updatedPasswordItem = item.copy(
//                    serviceName = updatedServiceName,
//                    username = updatedUserName,
//                    notes = updatedNotes,
//                    password = updatedPassword,
//                    photo = updatedImageUri
//                )
//
//                // Update the password item in Firebase and local repository
//                viewModel.updatePasswordItem(updatedPasswordItem)
//            }
//
//            binding.editProfileSaveBtn.setOnClickListener {
//                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
//            }
//
//        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

package com.ponichTech.pswdManager.ui.passwords.single_password_item

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
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.databinding.EditSinglePasswordItemBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel
import com.ponichTech.pswdManager.utils.autoCleared

class EditSinglePasswordFragment : Fragment() {

    private var binding: EditSinglePasswordItemBinding by autoCleared()

    private var imageUri: Uri? = null

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            requireActivity().application,
            AuthRepositoryFirebase(),
            PasswordFirebaseRepository()
        )
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = EditSinglePasswordItemBinding.inflate(inflater, container, false)

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        // Observing the selected password item
        viewModel.selectedPasswordItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.serviceName.setText(it.serviceName)
                binding.userName.setText(it.username)
                binding.userNote.setText(it.notes)
                binding.userPassword.setText(it.password)

                // Load photo using Glide with circular crop
                Glide.with(requireContext())
                    .load(it.photo)
                    .error(R.mipmap.ic_launcher) // Error image if loading fails
                    .circleCrop()
                    .into(binding.resultImage)
            }
        }

        // Save changes button
        binding.btnSaveChanges.setOnClickListener {
            viewModel.selectedPasswordItem.value?.let { item ->
                val updatedServiceName = binding.serviceName.text.toString()
                val updatedUserName = binding.userName.text.toString()
                val updatedNotes = binding.userNote.text.toString()
                val updatedPassword = binding.userPassword.text.toString()
                val updatedImageUri = imageUri?.toString() ?: item.photo

                // Create an updated Password object
                val updatedPasswordItem = item.copy(
                    serviceName = updatedServiceName,
                    username = updatedUserName,
                    notes = updatedNotes,
                    password = updatedPassword,
                    photo = updatedImageUri
                )

                // Update the password item in Firebase and local repository
                viewModel.updatePasswordItem(updatedPasswordItem)
            }

            findNavController().navigate(R.id.action_editSinglePasswordItemFragment_to_allPasswordsFragment)

        }

        return binding.root
    }
}

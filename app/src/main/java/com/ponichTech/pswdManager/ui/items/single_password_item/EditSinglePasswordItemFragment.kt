package com.ponichTech.pswdManager.ui.items.single_password_item

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
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.data.local_db.PasswordItemDatabase
import com.ponichTech.pswdManager.data.repository.firebase.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.firebase.UserRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.local_Repo.PasswordLocalRepository
import com.ponichTech.pswdManager.databinding.EditSinglePasswordItemBinding
import com.ponichTech.pswdManager.ui.items.all_password_items.PasswordsViewModel
import com.ponichTech.pswdManager.utils.autoCleared

class EditSinglePasswordFragment : Fragment() {

    private var binding: EditSinglePasswordItemBinding by autoCleared()

    private var imageUri: Uri? = null

    private val viewModel: PasswordsViewModel by activityViewModels {
        PasswordsViewModel.PasswordsViewModelFactory(
            UserRepositoryFirebase(),
            PasswordLocalRepository(PasswordItemDatabase.getDatabase(requireContext()).passwordItemDao()),
            PasswordFirebaseRepository()
        )
    }
    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                binding.resultImage.setImageURI(it)
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
        binding = EditSinglePasswordItemBinding
            .inflate(inflater, container, false)

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        viewModel.selectedPasswordItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.serviceName.setText(it.serviceName)
                binding.userName.setText(it.username)
                binding.userNote.setText(it.notes)
                binding.userPassword.setText(it.password)
                // Load photo using Glide with circular crop
                Glide.with(requireContext()).load(it.photo).circleCrop().override(100, 100)
                    .into(binding.resultImage)

            }
        }
        binding.btnSaveChanges.setOnClickListener {
            viewModel.selectedPasswordItem.value?.let { item ->
                val updatedServiceName = binding.serviceName.text.toString()
                val updatedUserName = binding.userName.text.toString()
                val updatedNotes = binding.userNote.text.toString()
                val updatePassword = binding.userPassword.text.toString()
                val updatedImageUri = imageUri.toString()
                // Create an updated Password object
                val updatedPassword = item.copy(
                    serviceName = updatedServiceName,
                    username = updatedUserName,
                    notes = updatedNotes,
                    password = updatePassword,
                    photo = updatedImageUri
                )

                // Update the password in Firebase
                viewModel.updatePasswordItem(updatedPassword)
            }

        }
        return binding.root
    }
}





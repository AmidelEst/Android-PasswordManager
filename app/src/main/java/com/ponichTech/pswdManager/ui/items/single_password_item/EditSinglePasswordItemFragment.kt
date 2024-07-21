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
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.repository.firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.firebase.PasswordItemRepositoryFirebase
import com.ponichTech.pswdManager.databinding.EditSinglePasswordItemBinding
import com.ponichTech.pswdManager.ui.items.all_password_items.SharedViewModel
import com.ponichTech.pswdManager.ui.items.passwords_view_model.FirebasePasswordItemsViewModel
import com.ponichTech.pswdManager.utils.autoCleared

class EditSinglePasswordFragment : Fragment() {

    private var binding: EditSinglePasswordItemBinding by autoCleared()

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var imageUri: Uri? = null

    private val passwordsViewModel: FirebasePasswordItemsViewModel by viewModels {
        FirebasePasswordItemsViewModel.FirebasePasswordItemsViewModelFactory(
            AuthRepositoryFirebase(),
            PasswordItemRepositoryFirebase())
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

        sharedViewModel.selectedPasswordItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.serviceName.setText(it.serviceName)
                binding.userName.setText(it.username)
                binding.userNote.setText(it.notes)
                binding.userPassword.setText(it.password)
                // Load photo using Glide with circular crop
                Glide.with(requireContext()).load(it.photo).circleCrop().override(100, 100).into(binding.resultImage)

            }
        }
        binding.btnSaveChanges.setOnClickListener {
            sharedViewModel.selectedPasswordItem.value?.let { item ->
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
                passwordsViewModel.updatePassword(updatedPassword)
//                passwordsViewModel.deletePassword(item.userId,item.itemId,item.itemPassword)

            }

        }
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        sharedViewModel.selectedItem.observe(viewLifecycleOwner) { item ->
//            item?.let {
//                binding.newSite.setText(it.serviceName)
//                binding.editUsername.setText(it.userName)
//                binding.editNote.setText(it.notes)
//
//            }
//        }
//        binding.saveChanges.setOnClickListener {
//            sharedViewModel.selectedItem.value?.let { item ->
//                val updatedServiceName = binding.newSite.text.toString()
//                val updatedUserName = binding.editUsername.text.toString()
//                val updatedNotes = binding.editNote.text.toString()
//                val updatePassword = binding.editPassword.text.toString()
////                val updatedImageUri = imageUri.toString()
//
//                // Create an updated Password object
//                val updatedPassword = item.copy(
//                    serviceName = updatedServiceName,
//                    userName = updatedUserName,
//                    notes = updatedNotes,
//                    itemPassword = updatePassword,
////                    photo = ""
//                )
//
//                // Update the password in Firebase
//
//                passwordsViewModel.updatePassword(updatedPassword)
//
//            }
//
//        }
//
//
//    }
}





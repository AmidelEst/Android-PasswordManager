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
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.databinding.EditProfileBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel


class EditProfileFragment : Fragment() {

    private var _binding: EditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            requireActivity().application,
            AuthRepositoryFirebase(),
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

        val user = viewModel.currentUser.value?.data
        binding.name?.setText(user?.name ?: "")
        binding.email?.setText(user?.email ?: "")
        binding.password?.setText(user?.password ?: "")
        // Load photo using Glide with circular crop
        Glide.with(requireContext())
            .load(user?.userPhoto)
            .error(R.mipmap.ic_launcher) // Error image if loading fails
            .circleCrop()
            .into(binding.resultImage)

        // Save changes button
        binding.btnSave?.setOnClickListener {
            viewModel.selectedPasswordItem.value?.let { item ->
                val nameUpdated = user?.name
                val emailUpdated = user?.email
                val passwordUpdated = user?.password
                val userPhotoUpdated = user?.userPhoto
                val phoneUpdated =user?.phone
//                imageUri?.toString() ?: item.photo

                // Create an updated Password object
                val updatedUser = user?.copy(
                    name = nameUpdated!!,
                    email = emailUpdated!!,
                    password = passwordUpdated!!,
                    userPhoto = userPhotoUpdated,
                    phone = phoneUpdated,
                )

                // Update the password item in Firebase and local repository
//                viewModel.updateUser(updatedUser!!)
            }

            binding.btnSave?.setOnClickListener {
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

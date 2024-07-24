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
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.databinding.FragmentAddPasswordItemBinding
import com.ponichTech.pswdManager.ui.passwords.all_passwords.AllPasswordsViewModel
import com.ponichTech.pswdManager.utils.autoCleared

class AddPasswordItemFragment : Fragment() {

    private var binding: FragmentAddPasswordItemBinding by autoCleared()

    private var imageUri: Uri? = null
    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                binding.resultImage.setImageURI(it)
                requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageUri = it
            }
        }

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            requireActivity().application,
            AuthRepositoryFirebase(),
            PasswordFirebaseRepository()
        )
    }

    //1) CreateView
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPasswordItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    //2) ViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ChooseImageDialog
        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }
        /////////////////////////////////////////////////////
        //Strength meter
        val meter = binding.passwordInputMeter
        val passwordInput = binding.passwordInput
        val generatePasswordButton = binding.generatePasswordButton
        meter.setEditText(passwordInput)
        //Generate password - check if password input has something in it
        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                generatePasswordButton.visibility = View.VISIBLE
            } else {
                generatePasswordButton.visibility = View.GONE
            }
        }
        generatePasswordButton.setOnClickListener{
            passwordInput.setText(generateStrongPassword(12))
            generatePasswordButton.visibility = View.GONE
        }
        ///////////////////////////////////////////////////
        // FinishBTN addPassword
        binding.finishBtn.setOnClickListener {
            val passwordItem = PasswordItem(
                serviceName = binding.serviceNameInput.text.toString(),
                username = binding.usernameInput.text.toString(),
                password = binding.passwordInput.text.toString(),
                notes = binding.notesInput.text.toString(),
                photo = imageUri.toString(),
                userId =viewModel.currentUser.value?.data?.userId.toString()
            )
            //addingPasswordItem
            viewModel.addPasswordItem(passwordItem)
            //GOTO: addPassword -> allItemsFragment
            findNavController().navigate(R.id.action_addItemFragment_to_allItemsFragment)
        }
    }
}
fun generateStrongPassword(maxLength: Int): String {
    val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
    val numberChars = "0123456789"
    val specialChars = "!@#$%^&*()_-+=<>?/{}~|"
    val allChars = upperCaseChars + lowerCaseChars + numberChars + specialChars

    val sb = StringBuilder(maxLength)
    sb.append(upperCaseChars.random())
        .append(lowerCaseChars.random())
        .append(numberChars.random())
        .append(specialChars.random())

    repeat(maxLength - 4) {
        sb.append(allChars.random())
    }

    return sb.toString().toList().shuffled().joinToString("")
}


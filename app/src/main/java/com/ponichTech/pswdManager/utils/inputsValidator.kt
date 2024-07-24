//package com.ponichTech.pswdManager.utils
//
//import android.widget.Toast
//
//object inputsValidator {
//
//    fun isEmailPassword( email:String?,password:String?) {
//
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            //login execution
//            loginViewModel.loginUser(email, password)
//        } else {
//            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
//    fun validators(){
//        private fun validateUserInput(email: String, phone: String, password: String): Boolean {
//            var isValid = true
//            // Validate email
//            val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
//            if (!emailRegex.matches(email)) {
//                notifyUser("Missing or invalid email. Format should be x@x.com")
//                isValid = false
//            }a
//            // Validate phone
//            val phoneRegex = Regex("^\\d+$")
//            if (!phoneRegex.matches(phone)) {
//                notifyUser("Missing or invalid phone number. It should contain only digits.")
//                isValid = false
//            }
//            // Validate password
//            if (password.length < 6) {
//                notifyUser("Password must be more than 6 characters.")
//                isValid = false
//            }
//            if (isValid) {
//                notifyUser("All inputs are valid!")
//            }
//            return isValid
//        }
//    }
//}
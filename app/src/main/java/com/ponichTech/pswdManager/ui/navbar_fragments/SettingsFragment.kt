package com.ponichTech.pswdManager.ui.navbar_fragments

import android.app.KeyguardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CancellationSignal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.databinding.SettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: SettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    //Biometric
    var isStronglySecured: Boolean = false
    private var cancellationSignal:CancellationSignal? = null
    private  val authenticationCallback: BiometricPrompt.AuthenticationCallback

    get() =
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication Error : $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Authentication Success")
                //This will execute after authentication
                //if succeeded  turn the x icon into a v
                isStronglySecured=true
                if(isStronglySecured){
                    binding.isStronglySecured.setImageResource(R.drawable.v_circle_icon)
                    binding.isStronglySecured.setColorFilter(ContextCompat.getColor(requireContext(),R.color.success),PorterDuff.Mode.SRC_IN)
                }
            }



        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsBinding
            .inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("ThemePrefs", 0)

        // Load saved theme preference and set the switch state
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        binding.darkThemeSwitch.isChecked = isDarkMode
        setNavigationBarColor(isDarkMode)
        // Set listener for the switch
        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(true)
                setNavigationBarColor(true)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(false)
                setNavigationBarColor(false)
            }
            requireActivity().recreate()
        }


        // Biometric
        checkBiometricSupport()
        binding.stronglySecureUserButton.setOnClickListener {
            val executor = ContextCompat.getMainExecutor(requireContext())
            val biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)

            val promptInfo = PromptInfo.Builder()
                .setTitle("Title of Prompt")
                .setSubtitle("Authentication is required")
                .setDescription("This app uses fingerprint protection")
                .setNegativeButtonText("Cancel")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }


        return binding.root
    }


    private fun getCancellationSignal():CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean{
        val keyguardManager = requireContext().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(keyguardManager.isKeyguardSecure){
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }
        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.USE_BIOMETRIC)!=PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        return if (requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true
    }

    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()
    }
    private fun setNavigationBarColor(isDarkMode: Boolean) {
        val color = ContextCompat.getColor(requireContext(), R.color.background)
        requireActivity().window.navigationBarColor = color
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
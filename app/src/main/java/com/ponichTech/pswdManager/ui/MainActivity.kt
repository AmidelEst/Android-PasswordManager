package com.ponichTech.pswdManager.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.databinding.ActivityMainBinding
import com.ponichTech.pswdManager.utils.SharedPreferencesUtil


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    companion object {
        private val TAG = MainActivity::class.java.name
    }

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //Swipe bar background
        val color = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = color


        binding = ActivityMainBinding.inflate(layoutInflater)
        FirebaseApp.initializeApp(this)
        setContentView(binding.root)
        Log.d(TAG, "onCreate called")


        // Setup the NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the BottomNavigationView with NavController
        val bottomNavigationView: BottomNavigationView = binding.navbar
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Set up item selected listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.allPasswordsFragment)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                R.id.settings -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }

        // Add a destination change listener to hide/show BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> bottomNavigationView.visibility = View.GONE
                R.id.registerFragment -> bottomNavigationView.visibility = View.GONE
                else -> bottomNavigationView.visibility = View.VISIBLE
            }
        }
        // Check login state and navigate accordingly
        val isLoggedIn = SharedPreferencesUtil.isLoggedIn(this)
        if (isLoggedIn) {
            navController.navigate(R.id.allPasswordsFragment)
        } else {
            navController.navigate(R.id.loginFragment)
        }
    }
}

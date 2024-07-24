package com.ponichTech.pswdManager.ui.passwords.all_passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.auth_repository_firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.passwords_repository.PasswordFirebaseRepository
import com.ponichTech.pswdManager.databinding.FragmentAllPasswordsItemsBinding
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.autoCleared

class AllPasswordsFragment : Fragment() {

    private var binding: FragmentAllPasswordsItemsBinding by autoCleared()

    private val viewModel: AllPasswordsViewModel by activityViewModels {
        AllPasswordsViewModel.Factory(
            requireActivity().application,
            AuthRepositoryFirebase(),
            PasswordFirebaseRepository()
        )
    }

    private lateinit var adapter: PasswordsAdapter
    //1) CreateView
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllPasswordsItemsBinding.inflate(inflater, container, false)

        viewModel.passwordItems.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    resource.data?.let { adapter.setPasswordItems(it) }
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // allPasswords -> addPasswords
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_allItemsFragment_to_addItemFragment)
        }
        return binding.root
    }
    //2)ViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PasswordsAdapter(object : PasswordsAdapter.PasswordListener {
            override fun onItemClicked(passItems: PasswordItem) {
                Toast.makeText(requireContext(), passItems.serviceName, Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClicked(passItems: PasswordItem) {
                viewModel.selectPasswordItem(passItems)
                findNavController().navigate(R.id.action_allItemsFragment_to_detailItemFragment)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(
                ItemTouchHelper.ACTION_STATE_SWIPE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.itemAt(viewHolder.adapterPosition)
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.confirm_delete))
                    .setMessage(getString(R.string.confirmation_delete_all))
                    .setPositiveButton(getString(R.string.delete_exclamation_mark)) { _, _ ->
                        viewModel.deletePasswordItem(item)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }).attachToRecyclerView(binding.recyclerView)

        viewModel.currentUser.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    val user = resource.data
                    user?.let { viewModel.fetchPasswordItems(user.userId) }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })

    }
}

package com.ponichTech.pswdManager.ui.items.all_password_items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.data.repository.firebase.AuthRepositoryFirebase
import com.ponichTech.pswdManager.data.repository.firebase.PasswordItemRepositoryFirebase
import com.ponichTech.pswdManager.databinding.FragmentAllPasswordsItemsBinding
import com.ponichTech.pswdManager.ui.items.passwords_view_model.FirebasePasswordItemsViewModel
import com.ponichTech.pswdManager.utils.Resource
import com.ponichTech.pswdManager.utils.autoCleared
import kotlinx.coroutines.launch

class AllPasswordItemsFragment : Fragment(){

    private var binding :FragmentAllPasswordsItemsBinding by autoCleared()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: FirebasePasswordItemsViewModel by viewModels {
        FirebasePasswordItemsViewModel.FirebasePasswordItemsViewModelFactory(
            AuthRepositoryFirebase(),
            PasswordItemRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllPasswordsItemsBinding.inflate(inflater,container,false)
        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_allItemsFragment_to_addItemFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the RecyclerView
        // Launch a coroutine to fetch the current user
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUserResource = viewModel.authRepo.getCurrentUser()
            if (currentUserResource is Resource.Success) {
                val userId = currentUserResource.data?.userId
                userId?.let {
                    sharedViewModel.setUserId(it)
                }
            } else if (currentUserResource is Resource.Error) {
                Toast.makeText(requireContext(), currentUserResource.message, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the userId and update the UI accordingly
        sharedViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                // Fetch or update data based on the logged-in userId
                viewModel.fetchPasswordItems()
            } else {
                // Handle user logged out state
            }

        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = PasswordItemAdapter(object : PasswordItemAdapter.PassItemListener {
            override fun onItemClicked(passItems: PasswordItem) {
                Toast.makeText(requireContext(),passItems.serviceName,Toast.LENGTH_SHORT).show()
            }
            //LongClicked
            override fun onItemLongClicked(passItems: PasswordItem) {
                sharedViewModel.selectPasswordItem(passItems)
                findNavController()
                    .navigate(R.id.action_allItemsFragment_to_detailItemFragment)
            }
        })

        sharedViewModel.userId.observe(viewLifecycleOwner, Observer { userId ->
            if (userId != null) {
                // Fetch or update data based on the logged-in userId
                viewModel.fetchPasswordItems()
            } else {
                // Handle user logged out state
            }
        })

        ////////////////////////////////////////////////////////////////////////////////////////

        viewModel.passwordItems.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.fab.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.fab.isEnabled = true
                    (binding.recyclerView.adapter as PasswordItemAdapter).setPasswordItems(it.data!!)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.fab.isEnabled = true
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }

            }
        }

        viewModel.addPassStatus.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.coordinator,"Item Added!",Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }

            }
        }


        viewModel.deletePassStatus.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    Snackbar.make(binding.coordinator,"Item Deleted!",Snackbar.LENGTH_SHORT)
                        .setAction("Undo") {
                            Toast.makeText(requireContext(),"For you to implement",Toast.LENGTH_SHORT).show()
                        }.show()
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }

            }
        }
/*////////////////////////////////////////////////////////////////////////////////////////

//        val menuHost: MenuHost = requireActivity()
//        menuHost.addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.main_menu, menu)
//            }
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId) {
//                    R.id.action_delete -> {
//                        val builder = AlertDialog.Builder(requireContext())
//                        builder.setTitle(getString(R.string.confirm_delete))
//                            .setMessage(getString(R.string.confirmation_delete_all))
//                            .setPositiveButton(getString(R.string.delete_exclamation_mark)) { _, _ ->
////                                viewModel.deleteAllItems()
//                                Toast.makeText(
//                                    requireContext(),
//                                    getString(R.string.all_items_have_been_deleted),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            .setNegativeButton(android.R.string.cancel, null)
//                            .show()
//                        true
//                    }
//                    R.id.log_out -> {
//                        val builder = AlertDialog.Builder(requireContext())
//                        builder.setTitle(getString(R.string.confirm_logout))
//                            .setMessage(getString(R.string.confirm_logout))
//                            .setPositiveButton(getString(R.string.logout)) { _, _ ->
//                                viewModel.signOut()
//                                findNavController().navigate(R.id.action_allItemsFragment_to_loginFragment2)
//                                Toast.makeText(requireContext(),getString(R.string.logout),Toast.LENGTH_SHORT).show()
//                            }
//                            .setNegativeButton(android.R.string.cancel, null)
//                            .show()
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }, viewLifecycleOwner, Lifecycle.State.RESUMED)*/

        ItemTouchHelper(object:ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            )= makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = (binding.recyclerView.adapter as PasswordItemAdapter).itemAt(viewHolder.adapterPosition)
                viewModel.deletePassItem(item.id)
            }
        }).attachToRecyclerView(binding.recyclerView)
    }



}
package com.ponichTech.pswdManager.ui.items.single_password_item

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.databinding.FragmentDetailPasswordItemBinding
import com.ponichTech.pswdManager.ui.items.all_password_items.SharedViewModel
import com.ponichTech.pswdManager.ui.items.passwords_view_model.LocalPasswordItemViewModel
import com.ponichTech.pswdManager.utils.autoCleared


class DetailPasswordItemFragment : Fragment() {

    // Binding property to handle view binding, cleared when the view is destroyed
    private var binding: FragmentDetailPasswordItemBinding by autoCleared()

    // ViewModel property shared between the fragment and the activity
    private val viewModel: LocalPasswordItemViewModel by activityViewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Inflates the fragment's layout and initializes the binding property
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using view binding
        binding = FragmentDetailPasswordItemBinding.inflate(inflater, container, false)

        binding.editDetailsBtn.setOnClickListener{
            findNavController().navigate(R.id.action_detailItemFragment_to_editSinglePasswordItemFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // If the item is not null, populate the UI with its data
        sharedViewModel.selectedPasswordItem.observe(viewLifecycleOwner) {
            it?.let {
                binding.itemServiceName.text = it.serviceName
                binding.itemUserName.text = it.username
                binding.itemNotes.text = it.notes
                Glide.with(requireContext()).load(it.photo).circleCrop().into(binding.itemImage)
            }
        }
    }
}
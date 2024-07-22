package com.ponichTech.pswdManager.ui.items.all_password_items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.databinding.PasswordItemLayoutBinding

// Adapter for displaying PasswordItems in a RecyclerView
class PasswordsAdapter(val callBack: PasswordListener)
    : RecyclerView.Adapter<PasswordsAdapter.PassItemViewHolder>() {

    // List to hold PasswordItems
    private val passItems = ArrayList<PasswordItem>()

    // Sets the PasswordItems to be displayed in the RecyclerView
    fun setPasswordItems(passItems: Collection<PasswordItem>) {
        this.passItems.clear() // Clear the existing items
        this.passItems.addAll(passItems) // Add the new items
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

    // Listener interface for handling item click and long click events
    interface PasswordListener {
        fun onItemClicked(passItems: PasswordItem) // Handle item click
        fun onItemLongClicked(passItems: PasswordItem) // Handle item long click
    }

    // ViewHolder class for PasswordItems
    inner class PassItemViewHolder(private val binding: PasswordItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener,
        View.OnLongClickListener {

        init {
            // Set click listeners for the item view
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        // Binds the data to the view
        fun bind(item: PasswordItem) {
            binding.itemServiceName.text = item.serviceName // Set service name
            binding.itemUserName.text = item.username // Set username
            // Load photo using Glide with circular crop
            Glide.with(binding.root).load(item.photo)
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder while loading
                .error(R.drawable.ic_launcher_foreground).override(200, 200).circleCrop() // Error image if loading fails
                .override(200, 200).circleCrop() // Set the desired width and height in pixels
                .into(binding.itemImage)
        }

        // Handles item click event
        override fun onClick(v: View?) {
            callBack.onItemClicked(passItems[adapterPosition])
        }

        // Handles item long click event
        override fun onLongClick(v: View?): Boolean {
            callBack.onItemLongClicked(passItems[adapterPosition])
            return true // Return true to indicate the event was handled
        }
    }

    // Returns the PasswordItem at the specified position
    fun itemAt(position: Int) = passItems[position]

    // Creates a new ViewHolder when there are no existing ViewHolders that can be reused
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PassItemViewHolder(
            PasswordItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    // Binds data to the ViewHolder
    override fun onBindViewHolder(holder: PassItemViewHolder, position: Int) =
        holder.bind(passItems[position])

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount() = passItems.size
}

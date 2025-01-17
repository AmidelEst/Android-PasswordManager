package com.ponichTech.pswdManager.ui.passwords.all_passwords

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ponichTech.pswdManager.R
import com.ponichTech.pswdManager.data.model.PasswordItem
import com.ponichTech.pswdManager.databinding.PasswordItemLayoutBinding

class PasswordsAdapter(val callBack: PasswordListener)
    : RecyclerView.Adapter<PasswordsAdapter.PassItemViewHolder>() {

    private val passItems = ArrayList<PasswordItem>()

    fun setPasswordItems(passItems: Collection<PasswordItem>) {
        this.passItems.clear()
        this.passItems.addAll(passItems)
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

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

            // Log the image URL for debugging
            Log.d("PasswordsAdapter", "Loading image from URL: ${item.photo}")

            // Load photo using Glide with circular crop
            if (!item.photo.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.photo)
                    .error(R.mipmap.ic_launcher) // Error image if loading fails
                    .override(200, 200) // Set the desired width and height in pixels
                    .circleCrop()
                    .into(binding.itemImage)
            } else {
                // Load a placeholder image if item.photo is null or empty
                Glide.with(binding.root.context)
                    .load(R.mipmap.ic_launcher) // Placeholder image
                    .override(200, 200) // Set the desired width and height in pixels
                    .circleCrop()
                    .into(binding.itemImage)
            }
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PassItemViewHolder(
            PasswordItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    // Binds data to the ViewHolder
    override fun onBindViewHolder(holder: PassItemViewHolder, position: Int) =
        holder.bind(passItems[position])


    override fun getItemCount() = passItems.size

    fun itemAt(position: Int) = passItems[position]

    fun removeItem(position: Int) {
        passItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun insertItem(position: Int, item: PasswordItem) {
        passItems.add(position, item)
        notifyItemInserted(position)
    }
}

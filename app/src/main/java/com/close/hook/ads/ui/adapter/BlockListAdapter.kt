package com.close.hook.ads.ui.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.close.hook.ads.R
import com.close.hook.ads.data.model.Item
import com.close.hook.ads.databinding.ItemBlockListBinding
import com.close.hook.ads.util.dp
import java.util.Locale

class BlockListAdapter(
    private val context: Context,
    private val onRemoveUrl: (Int) -> Unit,
    private val onEditUrl: (Int) -> Unit
) :
    ListAdapter<Item, BlockListAdapter.ViewHolder>(DIFF_CALLBACK) {

    var tracker: SelectionTracker<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBlockListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context, onRemoveUrl, onEditUrl)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tracker?.let {
            holder.bind(getItem(position), it.isSelected(getItem(position).url))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(
        private val binding: ItemBlockListBinding,
        private val context: Context,
        private val onRemoveUrl: (Int) -> Unit,
        private val onEditUrl: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): String = getItem(bindingAdapterPosition).url
            }

        init {
            binding.edit.setOnClickListener {
                onEditUrl(bindingAdapterPosition)
            }
            binding.delete.setOnClickListener {
                onRemoveUrl(bindingAdapterPosition)
            }
            binding.cardView.setOnClickListener {
                copyToClipboard(binding.type.text.toString(), binding.url.text.toString())
            }
        }

        fun bind(item: Item, isSelected: Boolean) {
            with(binding) {
                url.text = item.url
                type.text = item.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                cardView.isChecked = isSelected
                container.setPadding(16.dp, 12.dp, if (isSelected) 35.dp else 16.dp, 12.dp)
            }
        }

        private fun copyToClipboard(type: String, url: String) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipDataText = "$type, $url"
            val clipData = ClipData.newPlainText("request", clipDataText)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(context, "已复制: $clipDataText", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem.url == newItem.url && oldItem.type == newItem.type

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem
        }
    }
}

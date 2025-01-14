package com.close.hook.ads.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.close.hook.ads.R
import com.close.hook.ads.data.model.AppInfo
import com.close.hook.ads.databinding.InstallsItemAppBinding

class AppsAdapter(
    context: Context,
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<AppInfo, AppsAdapter.AppViewHolder>(DIFF_CALLBACK) {

    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .override(context.resources.getDimensionPixelSize(R.dimen.app_icon_size))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = InstallsItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding, onItemClickListener, requestOptions)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: AppViewHolder) {
        super.onViewRecycled(holder)
        holder.clearImage()
    }

    class AppViewHolder(
        private val binding: InstallsItemAppBinding,
        private val onItemClickListener: OnItemClickListener,
        private val requestOptions: RequestOptions
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding.root) {
                setOnClickListener { onItemClickListener.onItemClick(binding.packageName.text.toString()) }
                setOnLongClickListener { 
                    onItemClickListener.onItemLongClick(binding.packageName.text.toString())
                    true
                }
            }
        }

        fun bind(appInfo: AppInfo) {
            with(binding) {
                appName.text = appInfo.appName
                packageName.text = appInfo.packageName
                appVersion.text = "${appInfo.versionName} (${appInfo.versionCode})"
                Glide.with(appIcon.context)
                    .load(appInfo.appIcon)
                    .apply(requestOptions)
                    .into(appIcon)
            }
        }

        fun clearImage() {
            Glide.with(binding.appIcon.context).clear(binding.appIcon)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(packageName: String)
        fun onItemLongClick(packageName: String)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppInfo>() {
            override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean =
                oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean =
                oldItem == newItem
        }
    }
}

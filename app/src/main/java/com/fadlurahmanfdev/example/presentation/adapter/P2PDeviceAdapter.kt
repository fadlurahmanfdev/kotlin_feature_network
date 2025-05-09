package com.fadlurahmanfdev.example.presentation.adapter

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.data.dto.model.FeatureModel

class P2PDeviceAdapter : RecyclerView.Adapter<P2PDeviceAdapter.ViewHolder>() {
    private lateinit var context: Context
    val items: ArrayList<WifiP2pDevice> = arrayListOf()
    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setList(items: List<WifiP2pDevice>) {
        this.items.clear()
        this.items.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvFeatureTitle)
        val desc: TextView = view.findViewById(R.id.tvFeatureDesc)
        val main: LinearLayout = view.findViewById(R.id.llMain)
        val icon: ImageView = view.findViewById(R.id.iv_icon)

        init {
            main.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = items[position]
                    callback.onClicked(clickedItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feature, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.deviceName ?: "-"
        holder.desc.text = item.deviceAddress ?: "-"
        holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_wifi_find_24))
    }

    interface Callback {
        fun onClicked(item: WifiP2pDevice)
    }
}
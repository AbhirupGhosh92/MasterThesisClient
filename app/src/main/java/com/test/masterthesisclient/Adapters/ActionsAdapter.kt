package com.test.masterthesisclient.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.masterthesisclient.R
import com.test.masterthesisclient.databinding.AvailableActionItemsBinding

class ActionsAdapter(var itemList : List<String>) : RecyclerView.Adapter<ActionsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionsViewHolder {
        return ActionsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.available_action_items,parent,false))
    }

    override fun getItemCount(): Int {
       return itemList.size
    }

    override fun onBindViewHolder(holder: ActionsViewHolder, position: Int) {
        holder.availableActionsLayout.tvItem.text = itemList[position]
    }
}
package com.test.masterthesisclient.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.masterthesisclient.Adapters.ActionsAdapter
import com.test.masterthesisclient.R
import com.test.masterthesisclient.config.Constants
import com.test.masterthesisclient.databinding.AvailableActionsLayoutBinding

class ActionsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var dataBinding : AvailableActionsLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.available_actions_layout,container,false)

        dataBinding.rvActions.adapter = ActionsAdapter(Constants.actionList.toList())
        dataBinding.rvActions.layoutManager = LinearLayoutManager(requireContext())
        dataBinding.rvActions.itemAnimator = DefaultItemAnimator()
        return dataBinding.root
    }

}
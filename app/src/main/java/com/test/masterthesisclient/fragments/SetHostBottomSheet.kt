package com.test.masterthesisclient.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.masterthesisclient.R
import com.test.masterthesisclient.databinding.SetHostBottomSheetBinding

class SetHostBottomSheet : BaseDialogFragment() {


    private lateinit var dataBinding : SetHostBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        dataBinding = DataBindingUtil.inflate(inflater, R.layout.set_host_bottom_sheet,container,false)



        return dataBinding.root
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}
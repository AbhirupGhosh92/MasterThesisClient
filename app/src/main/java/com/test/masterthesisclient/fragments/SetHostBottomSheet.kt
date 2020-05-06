package com.test.masterthesisclient.fragments

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.masterthesisclient.R
import com.test.masterthesisclient.databinding.SetHostBottomSheetBinding
import com.test.masterthesisclient.viewmodels.SharedViewModel

class SetHostBottomSheet : BaseDialogFragment() {


    private lateinit var dataBinding : SetHostBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        dataBinding = DataBindingUtil.inflate(inflater, R.layout.set_host_bottom_sheet,container,false)

        var sharedViewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)


        dataBinding.tvOutTest.setOnClickListener {
            if(dataBinding.edtServerUrl.text.startsWith("http://"))
                {
                    sharedViewModel.saveHost(requireContext(),dataBinding.edtServerUrl.text.toString())
                }

            else{
                Toast.makeText(requireContext(),"Error setting Hostname",Toast.LENGTH_SHORT).show()
            }
        }


        return dataBinding.root
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}
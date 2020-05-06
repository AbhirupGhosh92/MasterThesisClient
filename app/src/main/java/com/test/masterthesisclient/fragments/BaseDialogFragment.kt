package com.test.masterthesisclient.fragments

import android.content.DialogInterface
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.masterthesisclient.MainActivity
import com.test.masterthesisclient.R
import kotlinx.android.synthetic.main.activity_main.*

open class BaseDialogFragment : BottomSheetDialogFragment() {


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
    }

}
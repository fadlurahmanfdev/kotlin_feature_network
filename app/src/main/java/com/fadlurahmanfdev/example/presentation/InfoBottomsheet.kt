package com.fadlurahmanfdev.example.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fadlurahmanfdev.example.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val TITLE_BOTTOMSHEET = "title_bottomsheet"
private const val DESC_BOTTOMSHEET = "desc_bottomsheet"

class InfoBottomsheet : BottomSheetDialogFragment() {
    private var title: String? = null
    private var desc: String? = null

    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE_BOTTOMSHEET)
            desc = it.getString(DESC_BOTTOMSHEET)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = requireView().findViewById(R.id.tv_title)
        tvDesc = requireView().findViewById(R.id.tv_desc)
        button = requireView().findViewById(R.id.btn)

        tvTitle.text = title
        tvDesc.text = desc
        button.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, desc: String) =
            InfoBottomsheet().apply {
                arguments = Bundle().apply {
                    putString(TITLE_BOTTOMSHEET, title)
                    putString(DESC_BOTTOMSHEET, desc)
                }
            }
    }
}
package com.guess.hk.smartbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog.*

class BookSheetDialog : BottomSheetDialogFragment(){

    var urls = arrayListOf<String>()
    set(value) {
        urls.addAll(value)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        first_url.text = urls[0]
        second_url.text = urls[1]
        three_url.text = urls[2]

        first_url.setOnClickListener(urlsBtnClicklistener)
        second_url.setOnClickListener(urlsBtnClicklistener)
        three_url.setOnClickListener(urlsBtnClicklistener)

    }


   private val urlsBtnClicklistener = View.OnClickListener {
       val textView = it as TextView
       openLink(it.context, textView.text.toString())
    }
}
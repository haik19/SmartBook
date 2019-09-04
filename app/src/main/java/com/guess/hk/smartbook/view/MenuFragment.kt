package com.guess.hk.smartbook.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.guess.hk.smartbook.R
import com.guess.hk.smartbook.model.BookKey
import com.guess.hk.smartbook.openLink
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog.*

class MenuFragment : Fragment(){

    var bookKey : BookKey? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookKey?.let {
//            first_url.text = it.url1
//            second_url.text = it.url2
//            three_url.text = it.url3
        }
        first_url.setOnClickListener(urlsBtnClicklistener)
        second_url.setOnClickListener(urlsBtnClicklistener)
        three_url.setOnClickListener(urlsBtnClicklistener)
    }

   private val urlsBtnClicklistener = View.OnClickListener {
       val textView = it as TextView
       openLink(it.context, textView.text.toString())
    }
}
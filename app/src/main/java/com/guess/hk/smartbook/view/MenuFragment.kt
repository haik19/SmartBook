package com.guess.hk.smartbook.view

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.guess.hk.smartbook.*
import com.guess.hk.smartbook.model.BookKey

class MenuFragment : DialogFragment(){

    var bookKey : BookKey? = null
    var camera: Camera? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.menu_fragment_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemWidth = getScreenWidthInPx(view.context) - convertDpToPixel(32f)
        val itemHeigth = convertDpToPixel(60f)
        bookKey?.let { it ->
            if (it.links.size == 1) {
                openLink(view.context, it.links.last().url)
                return
            }

            for (link in it.links){
                val linkView =
                    LayoutInflater.from(view.context).inflate(R.layout.menu_item_layout, view as ViewGroup, false)
                linkView.findViewById<TextView>(R.id.title).text = link.title
                linkView.findViewById<ImageView>(R.id.icon)
                    .setImageDrawable(context?.getDrawable(getCorespondingDrawbleId(link.type)))
                linkView.setOnClickListener {
                    openLink(it.context, link.url)
                }
                linkView.layoutParams.height = itemHeigth
                linkView.layoutParams.width = itemWidth
                view.addView(linkView)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.setOnShowListener {
            camera?.continueRecognize = false
            println("test rec listener")
        }
        return dialog
    }

    private fun getCorespondingDrawbleId(type: String) =
        when (type) {
            "image" -> R.drawable.ic_image
            "video" -> R.drawable.ic_video
            "web" -> R.drawable.ic_web
            else -> R.drawable.ic_image
        }

    override fun onCancel(dialog: DialogInterface?) {
        camera?.continueRecognize = true
        camera?.unLock()
        super.onCancel(dialog)
    }

}
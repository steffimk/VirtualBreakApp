package com.example.virtualbreak.controller.adapters

import com.example.virtualbreak.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.virtualbreak.model.Status

/**
 * Status Spinner popup in MyProfileFragment
 */
class StatusSpinnerArrayAdapter(context: Context, resource: Int, objects: Array<out Status>) :
    ArrayAdapter<Status>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = getCustomView(parent)
        setStatusViews(position, view)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = getCustomView(parent)
        setStatusViews(position, view)
        return view
    }

    private fun getCustomView(parent: ViewGroup): View {
        //val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.status_spinner_item, parent, false) //evtl take resource here
    }

    /**
     * adds right status image and status text to dropdown spinner item
     */
    private fun setStatusViews(position: Int, view: View) {
        val status: Status? = getItem(position)

        val statusSymbol : ImageView = view.findViewById(R.id.status_spinner_img)
        context?.let{
            when(status){ //without Status.INBREAK because should be set automatically
                Status.AVAILABLE -> statusSymbol.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_available))
                Status.BUSY -> statusSymbol.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_busy))
                Status.STUDYING -> statusSymbol.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_studying))
                Status.INBREAK -> statusSymbol.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_cup_white))
                Status.ABSENT -> statusSymbol.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
                else -> statusSymbol.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
            }
        }

        val statusTextView: TextView = view.findViewById(R.id.status_spinner_text)
        statusTextView.text = status?.dbStr
    }
}
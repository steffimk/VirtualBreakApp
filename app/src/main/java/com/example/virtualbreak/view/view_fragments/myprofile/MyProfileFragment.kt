package com.example.virtualbreak.view.view_fragments.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.R
import com.example.virtualbreak.model.Status
import kotlinx.android.synthetic.main.fragment_myprofile.*

class MyProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var myProfileViewModel: MyProfileViewModel

    private var status_array = arrayOf(Status.STUDYING.dbStr, Status.BUSY.dbStr, Status.AVAILABLE.dbStr)
    private lateinit var currentStatus: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myProfileViewModel =
            ViewModelProvider(this).get(MyProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_myprofile, container, false)

        /*
        val textView: TextView = root.findViewById(R.id.text_gallery)
        myProfileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        val spinner = root.findViewById<Spinner>(R.id.status_spinner)
        spinner.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and status array
        context?.let {
            val aa = ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                status_array
            )
            // Set layout to use when the list of choices (for different status) appear
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set array Adapter to Spinner
            spinner.setAdapter(aa)
        }

        return root
    }

    /**
     * when new status spinner item is selected, update status
     */
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        //TODO set new status
        currentStatus = status_array[position]
    }

    /**
     * also for status spinner
     */
    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}
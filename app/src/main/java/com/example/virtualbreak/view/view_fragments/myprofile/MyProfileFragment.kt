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
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_myprofile.*

class MyProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var myProfileViewModel: MyProfileViewModel

    private var status_array = arrayOf(Status.STUDYING, Status.BUSY, Status.AVAILABLE)
    private lateinit var currentStatus: Status

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myProfileViewModel =
            ViewModelProvider(this).get(MyProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_myprofile, container, false)

        //set username text of curent user
        PullData.currentUser?.username?.let{
            root.findViewById<TextView>(R.id.username).text = it
        }

        val spinner = root.findViewById<Spinner>(R.id.status_spinner)
        spinner.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and status array
        context?.let {
            val aa = ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                status_array.map{ status -> status.dbStr}
            )
            // Set layout to use when the list of choices (for different status) appear
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set array Adapter to Spinner
            spinner.setAdapter(aa)
            // Set position of spinner to current status
            val spinnerPosition = aa.getPosition(PullData.currentUser?.status?.dbStr)
            spinner.setSelection(spinnerPosition)
        }

        //button to edit own profile picture
        root.findViewById<FloatingActionButton>(R.id.fab_editPic).setOnClickListener {
            //TODO choose and save new profile picture (Firebase Storage)
            Snackbar.make(root, "Neues Profilbild", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        return root
    }

    /**
     * when new status spinner item is selected, update status
     */
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        currentStatus = status_array[position]
        PushData.setStatus(currentStatus)
    }

    /**
     * also for status spinner
     */
    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}
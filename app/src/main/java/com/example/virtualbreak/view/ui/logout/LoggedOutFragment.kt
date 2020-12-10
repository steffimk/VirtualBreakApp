package com.example.virtualbreak.view.ui.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.virtualbreak.R

class LoggedOutFragment : Fragment() {

    private lateinit var loggedOutViewModel: LoggedOutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loggedOutViewModel =
            ViewModelProvider(this).get(LoggedOutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_loggedout, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        loggedOutViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        //TODO really log out user + evtl not as Fragment but new Activity

        return root
    }
}
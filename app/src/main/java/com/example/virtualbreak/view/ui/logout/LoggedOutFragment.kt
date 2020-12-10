package com.example.virtualbreak.view.ui.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

        Firebase.auth.signOut()

        return root
    }
}
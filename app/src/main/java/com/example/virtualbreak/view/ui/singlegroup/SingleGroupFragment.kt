package com.example.virtualbreak.view.ui.singlegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nambimobile.widgets.efab.FabOption

class SingleGroupFragment : Fragment() {

    private lateinit var singleGroupViewModel: SingleGroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        singleGroupViewModel =
            ViewModelProvider(this).get(SingleGroupViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_singlegroup, container, false)

        val textView: TextView = root.findViewById(R.id.text_singlegroup)
        singleGroupViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val fab: FloatingActionButton = root.findViewById(R.id.fab_singlegroup)

        val fabOptionNormal: FabOption = root.findViewById(R.id.fab_singlegroup_option1)
        fabOptionNormal.setOnClickListener {
            //TODO Save Intent of Break: CoffeeBreak
            // (Make Popup, invite friends?), Send notificaitons, open Breakroom, go to Breakroom
        }
        val fabOptionSport: FabOption = root.findViewById(R.id.fab_singlegroup_option2)
        fabOptionSport.setOnClickListener {
            //TODO Save Intent of Break: SportBreak
            // (Make Popup, invite friends?), Send notificaitons, open Breakroom, go to Breakroom
        }
        val fabOptionGame: FabOption = root.findViewById(R.id.fab_singlegroup_option3)
        fabOptionGame.setOnClickListener {
            //TODO Save Intent of Break: GameBreak
            // (Make Popup, invite friends?), Send notificaitons, open Breakroom, go to Breakroom
        }

        return root
    }
}
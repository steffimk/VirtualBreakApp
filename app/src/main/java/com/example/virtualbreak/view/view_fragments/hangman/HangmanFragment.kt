package com.example.virtualbreak.view.view_fragments.hangman

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.ChatAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Game
import com.example.virtualbreak.model.Message
import com.example.virtualbreak.model.Room
import kotlinx.android.synthetic.main.hangman_fragment.*
import kotlinx.android.synthetic.main.textchat_fragment.*

class HangmanFragment : Fragment() {

    private val TAG = "HangmanFragment"

    private var game: Game? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.hangman_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameId = requireArguments().getString(Constants.GAME_ID)


        val viewModel: HangmanViewModel by viewModels {
            HangmanViewModelFactory(
                gameId!!
            )
        }

        var word: String =""

        viewModel.getGame().observe(viewLifecycleOwner, Observer<Game>
        { observedGame ->

            game = observedGame
            //context?.let { viewModel.loadUsersOfRoom(it) }

            if(observedGame.word == null){
                // TODO wait
            } else{
                // game starts
                word = observedGame.word!!
                if (observedGame.letters != null){
                    observedGame.letters!!.forEach{(key,value) ->
                        when (value) {
                            "A" -> a_input.setEnabled(false)
                            "B" -> b_input.setEnabled(false)
                            "C" -> c_input.setEnabled(false)
                            "D" -> d_input.setEnabled(false)
                            "E" -> e_input.setEnabled(false)
                            "F" -> f_input.setEnabled(false)
                            "G" -> g_input.setEnabled(false)
                            "H" -> h_input.setEnabled(false)
                            "I" -> i_input.setEnabled(false)
                            "J" -> j_input.setEnabled(false)
                            "K" -> k_input.setEnabled(false)
                            "L" -> l_input.setEnabled(false)
                            "M" -> m_input.setEnabled(false)
                            "N" -> n_input.setEnabled(false)
                            "O" -> o_input.setEnabled(false)
                            "P" -> p_input.setEnabled(false)
                            "Q" -> q_input.setEnabled(false)
                            "R" -> r_input.setEnabled(false)
                            "S" -> s_input.setEnabled(false)
                            "T" -> t_input.setEnabled(false)
                            "U" -> u_input.setEnabled(false)
                            "V" -> v_input.setEnabled(false)
                            "W" -> w_input.setEnabled(false)
                            "X" -> x_input.setEnabled(false)
                            "Y" -> y_input.setEnabled(false)
                            "Z" -> z_input.setEnabled(false)
                            else -> { // Note the block
                                print("no letter matching")
                            }
                        }
                    }
                }


                val error = observedGame.errors
                a_input.setOnClickListener {
                    //a_input.setEnabled(false)
                    // TODO add a to letter list of game
                    checkContaining(word, error,'a', gameId)
                    PushData.addLetterToGame(gameId!!, "A")
                }
                b_input.setOnClickListener {
                    checkContaining(word, error,'b', gameId)
                    PushData.addLetterToGame(gameId!!, "B")
                }
                c_input.setOnClickListener {
                    checkContaining(word, error,'c', gameId)
                    PushData.addLetterToGame(gameId!!, "C")
                }
                d_input.setOnClickListener {
                    checkContaining(word, error,'d', gameId)
                    PushData.addLetterToGame(gameId!!, "D")
                }
                e_input.setOnClickListener {
                    checkContaining(word, error,'e', gameId)
                    PushData.addLetterToGame(gameId!!, "E")
                }
                f_input.setOnClickListener {
                    checkContaining(word, error,'f', gameId)
                    PushData.addLetterToGame(gameId!!, "F")
                }
                g_input.setOnClickListener {
                    checkContaining(word, error,'g', gameId)
                    PushData.addLetterToGame(gameId!!, "G")
                }
                h_input.setOnClickListener {
                    checkContaining(word, error,'h', gameId)
                    PushData.addLetterToGame(gameId!!, "H")
                }
                i_input.setOnClickListener {
                    checkContaining(word, error,'i', gameId)
                    PushData.addLetterToGame(gameId!!, "I")
                }
                j_input.setOnClickListener {
                    checkContaining(word, error,'j', gameId)
                    PushData.addLetterToGame(gameId!!, "J")
                }
                k_input.setOnClickListener {
                    checkContaining(word, error,'k', gameId)
                    PushData.addLetterToGame(gameId!!, "K")
                }
                l_input.setOnClickListener {
                    checkContaining(word, error,'l', gameId)
                    PushData.addLetterToGame(gameId!!, "L")
                }
                m_input.setOnClickListener {
                    checkContaining(word, error,'m', gameId)
                    PushData.addLetterToGame(gameId!!, "M")
                }
                n_input.setOnClickListener {
                    checkContaining(word, error,'n', gameId)
                    PushData.addLetterToGame(gameId!!, "N")
                }
                o_input.setOnClickListener {
                    checkContaining(word, error,'o', gameId)
                    PushData.addLetterToGame(gameId!!, "O")
                }
                p_input.setOnClickListener {
                    checkContaining(word, error,'p', gameId)
                    PushData.addLetterToGame(gameId!!, "P")
                }
                q_input.setOnClickListener {
                    checkContaining(word, error,'q', gameId)
                    PushData.addLetterToGame(gameId!!, "Q")
                }
                r_input.setOnClickListener {
                    checkContaining(word, error,'r', gameId)
                    PushData.addLetterToGame(gameId!!, "R")
                }
                s_input.setOnClickListener {
                    checkContaining(word, error,'s', gameId)
                    PushData.addLetterToGame(gameId!!, "S")
                }
                t_input.setOnClickListener {
                    checkContaining(word, error,'t', gameId)
                    PushData.addLetterToGame(gameId!!, "T")
                }
                u_input.setOnClickListener {
                    checkContaining(word, error,'u', gameId)
                    PushData.addLetterToGame(gameId!!, "U")
                }
                v_input.setOnClickListener {
                    checkContaining(word, error,'v', gameId)
                    PushData.addLetterToGame(gameId!!, "V")
                }
                w_input.setOnClickListener {
                    checkContaining(word, error,'w', gameId)
                    PushData.addLetterToGame(gameId!!, "W")
                }
                x_input.setOnClickListener {
                    checkContaining(word, error,'x', gameId)
                    PushData.addLetterToGame(gameId!!, "X")
                }
                y_input.setOnClickListener {
                    checkContaining(word, error,'y', gameId)
                    PushData.addLetterToGame(gameId!!, "Y")
                }
                z_input.setOnClickListener {
                    checkContaining(word, error,'z', gameId)
                    PushData.addLetterToGame(gameId!!, "Z")
                }
            }
        })
    }

    private fun checkContaining(
        word: String,
        error: Int,
        char: Char,
        gameId: String?
    ) {
        Log.i(TAG, "word: " + word)
        if (word.contains(char, ignoreCase = true)) {
            Log.i(TAG, "contains " + char)
        } else{
            Log.i(TAG, "contains not " + char)
            val errors = error + 1
            PushData.addError(gameId!!, errors)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
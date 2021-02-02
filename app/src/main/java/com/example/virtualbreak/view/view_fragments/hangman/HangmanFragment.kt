package com.example.virtualbreak.view.view_fragments.hangman

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Game
import kotlinx.android.synthetic.main.hangman_fragment.*


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
        game_content_layout.visibility = View.VISIBLE
        game_ended.visibility = View.GONE

        //expand or close game fragment when click on expand arrow, textchat adapts to height
        expand_game_btn.setOnClickListener {
            if (hangman_content.getVisibility() === View.VISIBLE) {

                // The transition of the hiddenView is carried out
                //  by the TransitionManager class.
                // Here we use an object of the AutoTransition
                // Class to create a default transition.
                TransitionManager.beginDelayedTransition(
                    game_base_cardview,
                    AutoTransition()
                )
                hangman_content.setVisibility(View.GONE)
                expand_game_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(
                    game_base_cardview,
                    AutoTransition()
                )
                hangman_content.setVisibility(View.VISIBLE)
                expand_game_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
            }

        }

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

            observedGame?.let {
                if (observedGame.word == null) {
                    // TODO wait
                } else {
                    // game starts
                    word = observedGame.word!!

                    val error = observedGame.errors
                    if (error < Constants.HANGMAN_MAX_ERRORS) {

                        var wordFound = StringBuilder("")

                    // initialising word found with _
                    for (i in word.indices) {
                        wordFound.insert(i, '_')
                    }

                    if (observedGame.letters != null) {
                        observedGame.letters!!.forEach { (key, value) ->
                            when (value) {
                                "A" -> {
                                    a_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'a' || word.elementAt(i) == 'A') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "B" -> {
                                    b_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'b' || word.elementAt(i) == 'B') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "C" -> {
                                    c_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'c' || word.elementAt(i) == 'C') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "D" -> {
                                    d_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'd' || word.elementAt(i) == 'D') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "E" -> {
                                    e_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'e' || word.elementAt(i) == 'E') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "F" -> {
                                    f_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'f' || word.elementAt(i) == 'F') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "G" -> {
                                    g_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'g' || word.elementAt(i) == 'G') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "H" -> {
                                    h_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'h' || word.elementAt(i) == 'H') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "I" -> {
                                    i_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'i' || word.elementAt(i) == 'I') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "J" -> {
                                    j_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'j' || word.elementAt(i) == 'J') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "K" -> {
                                    k_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'k' || word.elementAt(i) == 'K') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "L" -> {
                                    l_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'l' || word.elementAt(i) == 'L') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "M" -> {
                                    m_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'm' || word.elementAt(i) == 'M') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "N" -> {
                                    n_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'n' || word.elementAt(i) == 'N') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "O" -> {
                                    o_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'o' || word.elementAt(i) == 'O') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "P" -> {
                                    p_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'p' || word.elementAt(i) == 'P') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "Q" -> {
                                    q_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'q' || word.elementAt(i) == 'Q') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "R" -> {
                                    r_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'r' || word.elementAt(i) == 'R') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "S" -> {
                                    s_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 's' || word.elementAt(i) == 'S') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "T" -> {
                                    t_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 't' || word.elementAt(i) == 'T') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "U" -> {
                                    u_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'u' || word.elementAt(i) == 'U') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "V" -> {
                                    v_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'v' || word.elementAt(i) == 'V') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "W" -> {
                                    w_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'w' || word.elementAt(i) == 'W') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "X" -> {
                                    x_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'x' || word.elementAt(i) == 'X') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "Y" -> {
                                    y_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'y' || word.elementAt(i) == 'Y') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                "Z" -> {
                                    z_input.setEnabled(false)
                                    for (i in word.indices) {
                                        if (word.elementAt(i) == 'z' || word.elementAt(i) == 'Z') {
                                            wordFound.setCharAt(i, word.elementAt(i))
                                        }
                                    }
                                }
                                else -> { // Note the block
                                    print("no letter matching")
                                }
                            }
                        }
                    }

                        if (word == wordFound.toString()) {
                            game_content_layout.visibility = View.GONE
                            game_ended.visibility = View.VISIBLE
                            end_result.text = getString(R.string.win_game)
                            var word_result_text = getString(R.string.word_result) + word
                            word_result.text = word_result_text
                            try_again.text = getString(R.string.retry_win)

                            restart_game.setOnClickListener {
                                // TODO remove old game and create new game
                            }
                        }
                        hangman_word.setText(wordFound.toString())


                        buttonClicks(word, error, gameId)
                    } else {
                        game_content_layout.visibility = View.GONE
                        game_ended.visibility = View.VISIBLE
                        end_result.text = getString(R.string.loose_game)
                        var word_result_text = getString(R.string.word_result) + word
                        word_result.text = word_result_text
                        try_again.text = getString(R.string.retry_loose)
                        restart_game.setOnClickListener {
                            // TODO remove old game and create new game
                        }
                    }
                }
            }

        })
    }

    private fun buttonClicks(word: String, error: Int, gameId: String?) {
        a_input.setOnClickListener {
            checkContaining(word, error, 'a', gameId)
            PushData.addLetterToGame(gameId!!, "A")
        }
        b_input.setOnClickListener {
            checkContaining(word, error, 'b', gameId)
            PushData.addLetterToGame(gameId!!, "B")
        }
        c_input.setOnClickListener {
            checkContaining(word, error, 'c', gameId)
            PushData.addLetterToGame(gameId!!, "C")
        }
        d_input.setOnClickListener {
            checkContaining(word, error, 'd', gameId)
            PushData.addLetterToGame(gameId!!, "D")
        }
        e_input.setOnClickListener {
            checkContaining(word, error, 'e', gameId)
            PushData.addLetterToGame(gameId!!, "E")
        }
        f_input.setOnClickListener {
            checkContaining(word, error, 'f', gameId)
            PushData.addLetterToGame(gameId!!, "F")
        }
        g_input.setOnClickListener {
            checkContaining(word, error, 'g', gameId)
            PushData.addLetterToGame(gameId!!, "G")
        }
        h_input.setOnClickListener {
            checkContaining(word, error, 'h', gameId)
            PushData.addLetterToGame(gameId!!, "H")
        }
        i_input.setOnClickListener {
            checkContaining(word, error, 'i', gameId)
            PushData.addLetterToGame(gameId!!, "I")
        }
        j_input.setOnClickListener {
            checkContaining(word, error, 'j', gameId)
            PushData.addLetterToGame(gameId!!, "J")
        }
        k_input.setOnClickListener {
            checkContaining(word, error, 'k', gameId)
            PushData.addLetterToGame(gameId!!, "K")
        }
        l_input.setOnClickListener {
            checkContaining(word, error, 'l', gameId)
            PushData.addLetterToGame(gameId!!, "L")
        }
        m_input.setOnClickListener {
            checkContaining(word, error, 'm', gameId)
            PushData.addLetterToGame(gameId!!, "M")
        }
        n_input.setOnClickListener {
            checkContaining(word, error, 'n', gameId)
            PushData.addLetterToGame(gameId!!, "N")
        }
        o_input.setOnClickListener {
            checkContaining(word, error, 'o', gameId)
            PushData.addLetterToGame(gameId!!, "O")
        }
        p_input.setOnClickListener {
            checkContaining(word, error, 'p', gameId)
            PushData.addLetterToGame(gameId!!, "P")
        }
        q_input.setOnClickListener {
            checkContaining(word, error, 'q', gameId)
            PushData.addLetterToGame(gameId!!, "Q")
        }
        r_input.setOnClickListener {
            checkContaining(word, error, 'r', gameId)
            PushData.addLetterToGame(gameId!!, "R")
        }
        s_input.setOnClickListener {
            checkContaining(word, error, 's', gameId)
            PushData.addLetterToGame(gameId!!, "S")
        }
        t_input.setOnClickListener {
            checkContaining(word, error, 't', gameId)
            PushData.addLetterToGame(gameId!!, "T")
        }
        u_input.setOnClickListener {
            checkContaining(word, error, 'u', gameId)
            PushData.addLetterToGame(gameId!!, "U")
        }
        v_input.setOnClickListener {
            checkContaining(word, error, 'v', gameId)
            PushData.addLetterToGame(gameId!!, "V")
        }
        w_input.setOnClickListener {
            checkContaining(word, error, 'w', gameId)
            PushData.addLetterToGame(gameId!!, "W")
        }
        x_input.setOnClickListener {
            checkContaining(word, error, 'x', gameId)
            PushData.addLetterToGame(gameId!!, "X")
        }
        y_input.setOnClickListener {
            checkContaining(word, error, 'y', gameId)
            PushData.addLetterToGame(gameId!!, "Y")
        }
        z_input.setOnClickListener {
            checkContaining(word, error, 'z', gameId)
            PushData.addLetterToGame(gameId!!, "Z")
        }
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
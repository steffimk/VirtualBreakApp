package com.example.virtualbreak.view.view_fragments.hangman

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
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

        parentFragmentManager.setFragmentResultListener(
            Constants.REQUEST_KEY_GAME_FRAGMENT,
            this,
            FragmentResultListener { requestKey, bundle ->
                val result = bundle.getBoolean(Constants.BUNDLE_KEY_GAME_FRAGMENT)
                handleKeyboardFromChat(result)
            })

        val gameId = requireArguments().getString(Constants.GAME_ID)
        // by default: set content layout visible and end layout invisible
        game_content_layout.visibility = View.VISIBLE
        game_ended.visibility = View.GONE

        //expand or close game fragment
        expand_game_relative_layout.setOnClickListener {
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

        var word: String = ""

        viewModel.getGame().observe(viewLifecycleOwner, Observer<Game>
        { observedGame ->

            game = observedGame

            observedGame?.let {
                if (observedGame.word == null) {
                    // when something went wrong and game has no word
                    game_content_layout.visibility = View.GONE
                    game_ended.visibility = View.VISIBLE

                    end_result.text = getString(R.string.no_game)
                    try_again.text = getString(R.string.retry_no_game)
                    game_ended_image.setImageResource(R.drawable.hangmanwin)

                    show_word.visibility = View.GONE
                    word_result.visibility = View.GONE

                    restart_game.setOnClickListener {
                        restartGame(observedGame)
                    }
                } else {
                    game_content_layout.visibility = View.VISIBLE
                    game_ended.visibility = View.GONE

                    // game starts
                    word = observedGame.word!!

                    val error = observedGame.errors

                    // game not failed
                    if (error < Constants.HANGMAN_MAX_ERRORS) {
                        // setting images depending of error state of game
                        when (error) {
                            0 -> fault_indicator.setImageResource(R.drawable.hangman0)
                            1 -> fault_indicator.setImageResource(R.drawable.hangman1)
                            2 -> fault_indicator.setImageResource(R.drawable.hangman2)
                            3 -> fault_indicator.setImageResource(R.drawable.hangman3)
                            4 -> fault_indicator.setImageResource(R.drawable.hangman4)
                            5 -> fault_indicator.setImageResource(R.drawable.hangman5)
                            6 -> fault_indicator.setImageResource(R.drawable.hangman6)
                            7 -> fault_indicator.setImageResource(R.drawable.hangman7)
                            else -> fault_indicator.setImageResource(R.drawable.hangman0)
                        }

                        val wordFound = StringBuilder("")

                        // initialising word found with _
                        for (i in word.indices) {
                            wordFound.insert(i, '_')
                        }

                        // when already some letters guessed
                        if (observedGame.letters != null) {
                            // depending on letters disable buttons and set parts of wordFound
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
                        } else {
                            enableAllButtons()
                        }

                        // all letters of word were found
                        if (word == wordFound.toString()) {
                            gameEnded(true, word, observedGame)
                        }
                        hangman_word.setText(wordFound.toString())

                        alphabetButtonClicks(word, error, gameId)
                    } else {
                        gameEnded(false, word, observedGame)
                    }
                }
            }

        })
    }

    fun handleKeyboardFromChat(focus: Boolean) {
        val viewBefore = hangman_content.getVisibility()
        if (focus) {
            // when edit text is clicked, hide game fragment
            if (viewBefore === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    game_base_cardview,
                    AutoTransition()
                )
                hangman_content.setVisibility(View.GONE)
                expand_game_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
            }
        } else {
            if (viewBefore === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    game_base_cardview,
                    AutoTransition()
                )
                hangman_content.setVisibility(viewBefore)
                expand_game_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
            }
        }
    }



    /**
     * When game ended show other view
     *
     * @param winning If word was found
     * @param word The word of the game
     * @param observedGame Object of current game
     */
    private fun gameEnded(winning: Boolean, word: String, observedGame: Game) {
        game_content_layout.visibility = View.GONE
        game_ended.visibility = View.VISIBLE

        if (winning) {
            end_result.text = getString(R.string.win_game)
            try_again.text = getString(R.string.retry_win)
            game_ended_image.setImageResource(R.drawable.hangmanwin)
        } else {
            end_result.text = getString(R.string.loose_game)
            try_again.text = getString(R.string.retry_loose)
            game_ended_image.setImageResource(R.drawable.hangman7)
        }
        show_word.text = word
        restart_game.setOnClickListener {
            restartGame(observedGame)
        }
    }

    /**
     * Restarting game changes visible view
     * Have to change the game object
     *
     * @param observedGame Object of current game
     */
    private fun restartGame(observedGame: Game) {
        PushData.updateGame(observedGame.uid, observedGame.roomId)
        game_content_layout.visibility = View.VISIBLE
        game_ended.visibility = View.GONE
    }

    /**
     * Reacts on button clicks
     *
     * @param word The current word of the game
     * @param error Amount of already false guessed letters
     * @param gameId Id of the current game
     */
    private fun alphabetButtonClicks(word: String, error: Int, gameId: String?) {
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

    /**
     * Check if the word contains letter
     *
     * @param word The current word of the game
     * @param error Amount of already false guessed letters
     * @param char The char that has to be checked
     * @param gameId Id of the current game
     */
    private fun checkContaining(
        word: String,
        error: Int,
        char: Char,
        gameId: String?
    ) {
        Log.i(TAG, "word: " + word)
        if (word.contains(char, ignoreCase = true)) {
            Log.i(TAG, "contains " + char)
        } else {
            Log.i(TAG, "contains not " + char)
            val errors = error + 1
            PushData.addError(gameId!!, errors)
        }
    }

    /**
     * Enables all alphabet buttons
     */
    private fun enableAllButtons() {
        a_input.setEnabled(true)
        b_input.setEnabled(true)
        c_input.setEnabled(true)
        d_input.setEnabled(true)
        e_input.setEnabled(true)
        f_input.setEnabled(true)
        g_input.setEnabled(true)
        h_input.setEnabled(true)
        i_input.setEnabled(true)
        j_input.setEnabled(true)
        k_input.setEnabled(true)
        l_input.setEnabled(true)
        m_input.setEnabled(true)
        n_input.setEnabled(true)
        o_input.setEnabled(true)
        p_input.setEnabled(true)
        q_input.setEnabled(true)
        r_input.setEnabled(true)
        s_input.setEnabled(true)
        t_input.setEnabled(true)
        u_input.setEnabled(true)
        v_input.setEnabled(true)
        w_input.setEnabled(true)
        x_input.setEnabled(true)
        y_input.setEnabled(true)
        z_input.setEnabled(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
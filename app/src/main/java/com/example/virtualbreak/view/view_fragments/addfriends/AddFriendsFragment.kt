package com.example.virtualbreak.view.view_fragments.addfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.databinding.FragmentAddFriendsBinding
import com.example.virtualbreak.model.User


/**
 * A simple [Fragment] subclass.
 */
class AddFriendsFragment : Fragment() {

    private val TAG = "AddFriendsFragment"

    private val viewModel: AddFriendsViewModel by viewModels()

    private var _binding: FragmentAddFriendsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)

        binding.searchFriendBtn.setOnClickListener {
            viewModel.searchForUserWithFullEmail(binding.friendEmail.text.toString())
            binding.tvWasSearchSuccessful.visibility = View.VISIBLE
        }

        return binding.root

        //TODO implement logic to search for friends (by email?) and sending a friend request
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSearchedUser().observe(viewLifecycleOwner, Observer<User?>{ searchedUser ->
            if (searchedUser != null) {
                binding.tvWasSearchSuccessful.text = getString(R.string.search_friend_successful)
                binding.foundfriendCardview.visibility = View.VISIBLE
                binding.foundfriendUsername.text = searchedUser.username
                binding.foundfriendEmail.text = searchedUser.email
                // binding.foundfriendImg.setImageDrawable(searchedUser.profilePicture) TODO: Show profile picture
                Log.d(TAG, "Found user " + searchedUser.username)
            }
            if (searchedUser == null) {
                binding.tvWasSearchSuccessful.text = getString(R.string.search_friend_not_successful)
                binding.foundfriendCardview.visibility = View.INVISIBLE
                Log.d(TAG, "Found no user with that mail")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
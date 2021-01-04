package com.example.virtualbreak.view.view_fragments.addfriends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.databinding.FragmentAddFriendsBinding
import com.example.virtualbreak.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


/**
 * A simple [Fragment] subclass.
 */
class AddFriendsFragment : Fragment() {

    private val TAG = "AddFriendsFragment"

    private val viewModel: AddFriendsViewModel by viewModels()

    private var _binding: FragmentAddFriendsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)

        binding.searchFriendBtn.setOnClickListener {
            viewModel.searchForUserWithFullEmail(binding.friendEmail.text.toString())
            binding.tvWasSearchSuccessful.text = getString(R.string.searching_for_friend)
            binding.tvWasSearchSuccessful.visibility = View.VISIBLE
        }

        binding.btnSendfriendrequest.setOnClickListener {
            sendFriendRequestToUser(viewModel.getSearchedUser().value)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCurrentUser() // Get once to trigger "by lazy" instantiation

        viewModel.getSearchedUser().observe(viewLifecycleOwner, Observer<User?>{ searchedUser ->
            if (searchedUser != null) {
                binding.tvWasSearchSuccessful.text = getString(R.string.search_friend_successful)
                binding.foundfriendCardview.visibility = View.VISIBLE
                binding.foundfriendUsername.text = searchedUser.username
                binding.foundfriendEmail.text = searchedUser.email

                loadProfilePicture(binding.foundfriendImg, searchedUser.uid)
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

    private fun loadProfilePicture(imageView: ImageView, userId: String) {
        val mStorageRef = FirebaseStorage.getInstance().getReference()
        mStorageRef.child("img/profilePics/$userId").downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Picasso.get().load(task.result).into(imageView)
            } else {
                Log.w(TAG, "getProfilePictureURI unsuccessful")
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendFriendRequestToUser(user : User?) {
        val currentUser = viewModel.getCurrentUser().value
        var snackbarText = ""
        var isValidFriendRequest = false

        if (user == null || currentUser == null) {
            snackbarText = "Kein User gefunden"
        }
        // User tries to send himself a friend request
        else if (user.uid == currentUser?.uid) {
            snackbarText = "Du kannst dir selbst keine Freundschaftsanfrage schicken."
        }
        // User is already friends with found user
        else if (currentUser?.friends?.get(user.uid) != null) {
            snackbarText = "Du bist bereits mit "+ user.username + " befreundet."
        }
        // Check whether user has outgoing or incoming friend requests (if not: send friend request)
        else if (currentUser?.friendRequests == null) {
            snackbarText = "Die Freundschaftsanfrage wird verschickt"
            isValidFriendRequest = true
        }
        else {
            // Checking status of found user in friend requests
            when(currentUser?.friendRequests?.get(user.uid)){
                // friend request of found user isIncoming
                true -> snackbarText = "Du hast bereits eine Freundschaftsanfrage von " +
                        user.username + " erhalten."
                // friend request of found user is outgoing
                false -> snackbarText = "Du hast " +
                        user.username + " bereits eine Freundschaftsanfrage gesendet."
                // user not found in friend requests -> send friend request
                else  -> {
                    snackbarText = "Die Freundschaftsanfrage wird verschickt"
                    isValidFriendRequest = true
                }
            }
        }

        if (isValidFriendRequest) {
            PushData.sendFriendRequest(user!!.uid)
        }

        view?.let {
            Snackbar.make(it, snackbarText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

}
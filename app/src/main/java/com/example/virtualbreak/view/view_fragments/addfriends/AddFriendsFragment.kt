package com.example.virtualbreak.view.view_fragments.addfriends

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.FCMService
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.databinding.FragmentAddFriendsBinding
import com.example.virtualbreak.model.NotificationBody
import com.example.virtualbreak.model.NotificationData
import com.example.virtualbreak.model.PushNotification
import com.example.virtualbreak.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_friends.*


/**
 * Fragment shows a view to search for users and add them as friends
 */
class AddFriendsFragment : Fragment() {

    private val TAG = "AddFriendsFragment"

    private val viewModel: AddFriendsViewModel by viewModels()

    private var _binding: FragmentAddFriendsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    /**
     * Set on click listeners
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentAddFriendsBinding.inflate(inflater, container, false)

        binding.searchFriendBtn.setOnClickListener {
            viewModel.searchForUserWithFullEmail(binding.friendEmail.text.toString())
            // let user know that mail is being searched
            binding.tvWasSearchSuccessful.text = getString(R.string.searching_for_friend)
            binding.tvWasSearchSuccessful.visibility = View.VISIBLE

            hideSoftKeyboard(binding.friendEmail)
        }

        binding.btnSendfriendrequest.setOnClickListener {
            sendFriendRequestToUser(viewModel.getSearchedUser().value)
        }

        return binding.root
    }

    /**
     * Observe LiveData of ViewModel
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTransition()

        viewModel.getCurrentUser() // Get once to trigger "by lazy" instantiation

        viewModel.getSearchedUser().observe(viewLifecycleOwner, Observer<User?> { searchedUser ->
            if (searchedUser != null) {

                TransitionManager.beginDelayedTransition(searchfriendsresults)
                binding.tvWasSearchSuccessful.text = getString(R.string.search_friend_successful)
                binding.foundfriendCardview.visibility = View.VISIBLE
                binding.foundfriendUsername.text = searchedUser.username
                binding.foundfriendEmail.text = searchedUser.email
                binding.friendEmail.text?.clear() //clear text field id user was found

                loadProfilePicture(binding.foundfriendImg, searchedUser.uid)

                Log.d(TAG, "Found user " + searchedUser.username)
            }
            if (searchedUser == null) {
                binding.tvWasSearchSuccessful.text =
                    getString(R.string.search_friend_not_successful)
                binding.foundfriendCardview.visibility = View.INVISIBLE
                Log.d(TAG, "Found no user with that mail")
            }
        })
    }

    /**
     * Load profile picture of found user
     */
    private fun loadProfilePicture(imageView: ImageView, userId: String) {
        val mStorageRef = FirebaseStorage.getInstance().getReference()
        mStorageRef.child("img/profilePics/$userId").downloadUrl
            .addOnSuccessListener { result ->
                Picasso.get().load(result).into(imageView)
            }
            .addOnFailureListener {
                //Log.w(TAG, it) // exception is already printed in StorageException class
                Log.d(TAG, "This user does not have a profile picture!")
            }
    }

    fun hideSoftKeyboard(editText: EditText) {
        val imm: InputMethodManager? = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }

    /**
     * Call when 'send friend request'-button is clicked. Checks whether user is allowed to do so.
     * @param user The user that would be the receiver of the friend request
     */
    private fun sendFriendRequestToUser(user: User?) {
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
            // Send notification to user
            val title = "Neue Freundschaftsanfrage"
            val message = "${currentUser!!.username} möchte dich als Freund hinzufügen."
            PushNotification(
                NotificationData(title, message),
                NotificationBody(title, message),
                user.fcmToken
            ).also {
                Log.d(TAG, "Sending notification: $it")
                FCMService.sendNotification(it)
            }
        }

        view?.let {
            Snackbar.make(it, snackbarText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    /**
     * adds return transition on friends items in friends list fragment
     */
    private fun addTransition() {
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.motion_duration_small).toLong()
            addTarget(R.id.friends_recyler_list_view)
        }

        //transition for when this fragment appears
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
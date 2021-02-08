package com.example.virtualbreak.view.view_fragments.groupsfriends

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.adapters.FriendListAdapter
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_friend_requests.*
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.*
import kotlinx.android.synthetic.main.fragment_groups_friendlist_fragment.account_status_text_view
import kotlinx.android.synthetic.main.fragment_singlegroup.*

/**
 * Friend list fragment in main fragment
 */
class Groups_friendlist_fragment : Fragment() {

    companion object {
        fun newInstance() = Groups_friendlist_fragment()
    }

    private val TAG = "Groups_Friendlist_Fragment"

    private val viewModel: GroupsViewModel by viewModels()

    var friendsListAdapter: FriendListAdapter? = null

    // Handle closing an expanded recipient card when on back is pressed
    private val closePopupCardOnBackPressed = object : OnBackPressedCallback(false) {
        var expandedChip: View? = null
        override fun handleOnBackPressed() {
            expandedChip?.let { collapseChip(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_friendlist_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friends_recyler_list_view.layoutManager = LinearLayoutManager(activity)

        //for faster image loadig
        friends_recyler_list_view.setHasFixedSize(true)
        friends_recyler_list_view.setItemViewCacheSize(20)

        viewModel.getFriends().observe(viewLifecycleOwner, Observer<HashMap<String,User>>{ friends ->

            if(friends.size == 0){
                no_friends_yet_linearlayout.visibility = View.VISIBLE //display text that no friends were added yet
            } else{
                no_friends_yet_linearlayout.visibility = View.GONE
                friendsListAdapter = FriendListAdapter(ArrayList(friends.values), context, this)
                friends_recyler_list_view.adapter = friendsListAdapter
            }

            Log.d(TAG, "Observed friends: $friends")
        })

        friends_add_friends_button.setOnClickListener{
            //navigate to add friends fragment
            view.findNavController().navigate(R.id.action_nav_home_to_addFriendsFragment)

        }
    }

    /**
     * Expand the friend [chip] into a popup with a profile picture and email and other info.
     */
    fun expandChip(chip: View, user: User) {
        // Configure the analogous collapse transform back to the recipient chip. This should
        // happen when the card is clicked, any region outside of the card (the card's transparent
        // scrim) is clicked, or when the back button is pressed.
        account_card_view.setOnClickListener { collapseChip(chip) }
        account_card_scrim.visibility = View.VISIBLE
        account_card_scrim.setOnClickListener { collapseChip(chip) }
        closePopupCardOnBackPressed.expandedChip = chip
        closePopupCardOnBackPressed.isEnabled = true

        // Set up MaterialContainerTransform beginDelayedTransition.
        val transform = MaterialContainerTransform().apply {
            startView = chip
            endView = account_card_view
            scrimColor = Color.TRANSPARENT
            endElevation = requireContext().resources.getDimension(
                R.dimen.card_popup_elevation_compat
            )
            addTarget(account_card_view)
        }

        TransitionManager.beginDelayedTransition(friends_recyler_list_view, transform)


        account_card_view.visibility = View.VISIBLE
        account_name_text_view.text = user.username
        account_email_text_view.text = user.email
        account_status_text_view.text = user.status?.dbStr

        context?.let{
            when(user.status){
                Status.AVAILABLE -> account_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_available))
                Status.BUSY -> account_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_busy))
                Status.STUDYING -> account_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_studying))
                Status.INBREAK -> account_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_cup_black))
                Status.ABSENT -> account_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
                else -> account_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
            }
        }
        loadProfilePicture(account_profile_image_view, user.uid)

    }

    /**
     * Collapse the friend popup card back into its [chip] form.
     */
    private fun collapseChip(chip: View) {
        // Remove the scrim view and on back pressed callbacks
        account_card_scrim.visibility = View.GONE
        closePopupCardOnBackPressed.expandedChip = null
        closePopupCardOnBackPressed.isEnabled = false

        TransitionManager.beginDelayedTransition(account_card_view)


        account_card_view.visibility = View.INVISIBLE
    }

    private fun loadProfilePicture(imgView: ImageView, userId: String) {

        val mStorageRef = FirebaseStorage.getInstance().getReference()
        mStorageRef.child("img/profilePics/$userId").downloadUrl
            .addOnSuccessListener { result ->
                Picasso.get()
                    .load(result)
                    .fit()
                    .centerCrop()
                    .into(imgView)
            }
            .addOnFailureListener {
                //Log.w(TAG, it) // exception is already printed in StorageException class
                Log.d(TAG, "This user does not have a profile picture!")
            }
    }


}
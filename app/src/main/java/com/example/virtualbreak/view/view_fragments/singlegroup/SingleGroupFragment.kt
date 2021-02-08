package com.example.virtualbreak.view.view_fragments.singlegroup

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_fragments.hangman.HangmanFragment
import com.example.virtualbreak.view.view_fragments.textchat.TextchatFragment
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_singlegroup.*

class SingleGroupFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    //Navigation argument to pass selected group from GroupsFriendsFragment (GroupsListAdapter) to SingleGroupFragment
    val args: SingleGroupFragmentArgs by navArgs()
    private lateinit var groupId: String

    private val singleGroupViewModel: SingleGroupViewModel by viewModels {
        SingleGroupViewModelFactory(
            args.groupId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_singlegroup, container, false)

        setHasOptionsMenu(true)

        groupId = args.groupId

        singleGroupViewModel.getGroupUsers() // to trigger start init by lazy

        Log.d(TAG, "Gruppe DESCRIPTION \t" + singleGroupViewModel.getCurrentGroup().value?.description)

        singleGroupViewModel.getCurrentGroup()
            .observe(viewLifecycleOwner, Observer<Group?> { currentGroup ->
                if (currentGroup != null) {
                    activity?.toolbar?.title = currentGroup.description
                    Log.d(TAG, "Found group name " + currentGroup.description)
                }
            })

        val singleGroupMembersFragment = SingleGroupMembersFragment.newInstance(groupId, this)
        //singleGroupMembersFragment.setParentFragment(this) // do this so child fragment has reference to this parent fragment, to show friend popup view ove this view
        //instantiate member list fragment and room grid fragment
        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.replace(
                R.id.singlegroup_containerview_members,
                singleGroupMembersFragment
            )
            //it.addToBackStack(null)
            it.commit()
        }

        //check if user is in room and widget is open, when yes disabled rooms

        var addMember = false

        var manager = activity?.supportFragmentManager

        manager?.beginTransaction()?.let {
            it.replace(
                R.id.singlegroup_containerview_rooms,
                SingleGroupRoomsFragment.newInstance(groupId)
            )
            //it.addToBackStack(null)
            it.commit()
        }

        manager?.setFragmentResultListener(
            Constants.REQUEST_KEY_ADD_MEMBER,
            this,
            FragmentResultListener { requestKey, bundle ->
                addMember = bundle.getBoolean(Constants.BUNDLE_KEY_ADD_MEMBER)
                Log.i(TAG, "received add member click " + addMember)

                if (addMember) {

                    manager?.beginTransaction()?.let {
                        it.replace(
                            R.id.singlegroup_containerview_rooms,
                            AddMemberToGroupFragment.newInstance(groupId)
                        )
                        it.commit()
                    }
                } else {
                    Log.i(TAG, "else add member")
                    // display rooms
                    manager?.beginTransaction()?.let {
                        it.replace(
                            R.id.singlegroup_containerview_rooms,
                            SingleGroupRoomsFragment.newInstance(groupId)
                        )
                        //it.addToBackStack(null)
                        it.commit()
                    }
                }
            })

        var userName: String? = SharedPrefManager.instance.getUserName()


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTransition()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.singlegroup_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        R.id.action_leave_group -> {
            showLeaveDialog()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showLeaveDialog() {
        context?.let{
            val dialog = Dialog(it)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.leavegroup_alert_dialog)
            val yesBtn = dialog.findViewById(R.id.leavegroup_confirm_button) as Button
            val noBtn = dialog.findViewById(R.id.leavegroup_dismiss_button) as Button

            yesBtn.setOnClickListener {
                dialog.dismiss()
                singleGroupViewModel.getCurrentGroup().value?.let {
                    singleGroupViewModel.getCurrentGroup().removeObservers(this)
                    singleGroupViewModel.getGroupUsers().removeObservers(this)
                    singleGroupViewModel.getRooms().removeObservers(this)
                    PushData.leaveGroup(it)
                }
                view?.findNavController()?.navigate(R.id.action_singleGroupFragment_to_home)
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }


    }

    /**
     * Expand the friend [chip] into a popup with a profile picture and email and other info.
     */
    fun expandChip(chip: View, user: User) {
        // Configure the analogous collapse transform back to the recipient chip. This should
        // happen when the card is clicked, any region outside of the card (the card's transparent
        // scrim) is clicked, or when the back button is pressed.
        member_card_view.setOnClickListener { collapseChip(chip) }
        member_card_scrim.visibility = View.VISIBLE
        member_card_scrim.setOnClickListener { collapseChip(chip) }

        // Set up MaterialContainerTransform beginDelayedTransition.
        val transform = MaterialContainerTransform().apply {
            startView = chip
            endView = member_card_view
            scrimColor = Color.TRANSPARENT
            endElevation = requireContext().resources.getDimension(
                R.dimen.card_popup_elevation_compat
            )
            addTarget(member_card_view)
        }

        TransitionManager.beginDelayedTransition(singlegroup_fragment_linearlayout, transform)


        member_card_view.visibility = View.VISIBLE
        member_name_text_view.text = user.username
        member_email_text_view.text = user.email
        member_status_text_view.text = user.status?.dbStr

        context?.let{
            when(user.status){
                Status.AVAILABLE -> member_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_available))
                Status.BUSY -> member_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_busy))
                Status.STUDYING -> member_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_studying))
                Status.INBREAK -> member_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_cup_black))
                Status.ABSENT -> member_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
                else -> member_status_img.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.status_circle_unknown))
            }
        }
        loadProfilePicture(member_profile_image_view, user.uid)

    }

    /**
     * Collapse the friend popup card back into its [chip] form.
     */
    private fun collapseChip(chip: View) {
        // Remove the scrim view and on back pressed callbacks
        member_card_scrim.visibility = View.GONE

        TransitionManager.beginDelayedTransition(member_card_view)

        member_card_view.visibility = View.INVISIBLE
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
                context?.let{
                    imgView.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_person_24))
                }
            }
    }

    /**
     * adds return transition when return to groupslist fragment to animate group list items
     */
    private fun addTransition() {
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.motion_duration_small).toLong()
            addTarget(R.id.groups_recyler_list_view)
        }

    }

}
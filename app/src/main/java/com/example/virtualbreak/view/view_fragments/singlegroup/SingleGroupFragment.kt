package com.example.virtualbreak.view.view_fragments.singlegroup

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.*
import com.example.virtualbreak.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_singlegroup.*

/**
 * Fragment that contains SingleGroupMembersFragment (show group members) and SingleGroupRoomsFragment (show open rooms in group)
 */
class SingleGroupFragment : Fragment() {

    private val TAG: String = "SingleGroupFragment"

    //Navigation argument to pass selected group from GroupsFriendsFragment (GroupsListAdapter) to SingleGroupFragment
    val args: SingleGroupFragmentArgs by navArgs()
    private lateinit var groupId: String
    private lateinit var root: View
    private var mToolbar: Toolbar? = null
    private var renameGroupEditText: EditText? = null

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
        root = inflater.inflate(R.layout.fragment_singlegroup, container, false)

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

        // display rooms
        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.replace(
                R.id.singlegroup_containerview_rooms,
                SingleGroupRoomsFragment.newInstance(groupId)
            )
            //it.addToBackStack(null)
            it.commit()
        }

        mToolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        renameGroupEditText = activity?.findViewById<EditText>(R.id.changeGroupName_editText)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        //edit group name
        R.id.action_edit_group_name -> {
            //User wants to edit the room name
            renameGroupEditText?.let{
                val editText = it
                mToolbar?.let{
                    editText.visibility = View.VISIBLE
                    //Show the keyboard
                    val imm: InputMethodManager =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    editText.requestFocus()

                    it.menu.findItem(R.id.action_enter_group_name).isVisible = true
                    it.menu.findItem(R.id.action_edit_group_name).isVisible = false
                    true
                }
            }
            true

        }
        //Confirm the Group Name
        R.id.action_enter_group_name -> {
            renameGroupEditText?.let {
                val editText = it
                mToolbar?.let {
                    val newTitle = editText.text.toString()
                    editText.visibility = View.GONE
                    editText.text.clear()
                    it.menu.findItem(R.id.action_enter_group_name).isVisible = false
                    it.menu.findItem(R.id.action_edit_group_name).isVisible = true

                    //save the new title in firebase
                    if (groupId != null) {
                        if ("".equals(newTitle)) {
                            Snackbar.make(
                                editText,
                                "Du hast keinen neuen Namen eingegeben!",
                                Snackbar.LENGTH_LONG
                            )
                                .setAction("Action", null).show()
                        } else {
                            PushData.setGroupDescription(groupId, newTitle)
                            it.title = newTitle
                        }
                    }

                    hideSoftKeyboard(editText)
                    true
                }
            }
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
    fun expandFriendPopUp(clickedView: View, user: User) {
        // Configure the analogous collapse transform back to the recipient chip. This should
        // happen when the card is clicked, any region outside of the card (the card's transparent
        // scrim) is clicked, or when the back button is pressed.
        member_card_view.setOnClickListener { collapseFriendPopUp() }
        member_card_scrim.visibility = View.VISIBLE
        member_card_scrim.setOnClickListener { collapseFriendPopUp() }

        // Set up MaterialContainerTransform beginDelayedTransition.
        val transform = MaterialContainerTransform().apply {
            startView = clickedView
            endView = member_card_view
            scrimColor = Color.TRANSPARENT
            endElevation = requireContext().resources.getDimension(
                R.dimen.card_popup_elevation_compat
            )
            addTarget(member_card_view)
        }

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

        TransitionManager.beginDelayedTransition(singlegroup_fragment_linearlayout, transform)

    }

    /**
     * Collapse the friend popup card and fade away
     */
    private fun collapseFriendPopUp() {
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
     * closes soft keyboard
     * editText: View
     */
    private fun hideSoftKeyboard(editText: EditText) {
        val imm: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }

    /**
     * onPause make sure that toolbar is reset, when tried to rename group before, because same toolbar also used in other fragments
     */
    override fun onPause() {
        super.onPause()

        //reset rename group editText to be invisible and reset all other toolbar action icons
        renameGroupEditText?.let{
            it.visibility = View.GONE
            it.text.clear()
        }
        mToolbar?.let{
            it.menu.findItem(R.id.action_enter_group_name).isVisible = false
            it.menu.findItem(R.id.action_edit_group_name).isVisible = true
        }
    }

}
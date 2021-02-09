package com.example.virtualbreak.view.view_fragments.myprofile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.StatusSpinnerArrayAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Status
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_activitys.MainActivity
import com.example.virtualbreak.view.view_activitys.breakroom.BreakroomWidgetService
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_myprofile.*

/**
 * Fragment that shows profile picture, which can be edited
 * also shows Status spinner to show current status which can be changed
 * with logout button
 */
class MyProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val myProfileViewModel: MyProfileViewModel by viewModels()
    private val mStorageRef = FirebaseStorage.getInstance().getReference()
    private val TAG = "MyProfileFragment"

    private var status_array = arrayOf(Status.STUDYING, Status.BUSY, Status.AVAILABLE, Status.ABSENT, Status.INBREAK)
    private lateinit var currentStatus: Status

    private var currentUserID: String? = null

    private lateinit var userNameTextView: TextView
    private lateinit var userNameEditText: EditText
    private lateinit var userNameButton: ImageButton

    private val PICK_FROM_GALLERY = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_myprofile, container, false)

        val spinner = root.findViewById<Spinner>(R.id.status_spinner)
        currentUserID = SharedPrefManager.instance.getUserId()

        userNameTextView = root.findViewById<TextView>(R.id.username)
        userNameEditText = root.findViewById<EditText>(R.id.username_textEdit)
        userNameButton = root.findViewById<ImageButton>(R.id.fab_editUsername)

        initProfilePicture()

        // Create an ArrayAdapter using a simple spinner layout and status array
        initStatusSpinner(spinner)

        // Connect with and observe LiveData
        myProfileViewModel.getUser().observe(viewLifecycleOwner, Observer<User> { observedUser ->
            if (spinner.onItemSelectedListener == null) {
                // Set only now - otherwise default value gets saved in database
                spinner.onItemSelectedListener = this
            }
            Log.d(TAG, "Observed User: $observedUser")
            if (observedUser != null) {
                //set username text of current user
                userNameTextView.text = observedUser.username
                profile_email.text = observedUser.email

                observedUser.status?.let{
                    // Set position of spinner to current status
                    spinner.setSelection(status_array.indexOf(observedUser.status))
                }
            }
        })


        userNameButton.setOnClickListener {
            editOrSaveUsername()
        }

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//disable rotate because sometimes bug then
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //button to edit own profile picture
        fab_editPic.setOnClickListener {
            //Snackbar.make(root, "Neues Profilbild", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            chooseProfileImg()

        }

        profile_logout_btn.setOnClickListener {
            PushData.setStatus(Status.ABSENT)
            //close Widget
            activity?.stopService(Intent(activity, BreakroomWidgetService::class.java))
            //Sign out
            Firebase.auth.signOut()
            //Unregister from fcm push notifications
            Firebase.messaging.isAutoInitEnabled = false
            Firebase.messaging.deleteToken()
            FirebaseInstallations.getInstance().delete()
            startActivity(Intent(activity, MainActivity::class.java)) //go back to sign up/login activity
        }
    }


    private fun initStatusSpinner(spinner: Spinner) {
        context?.let {
            val aa = StatusSpinnerArrayAdapter(it, R.layout.status_spinner_item, status_array)
            // Set layout to use when the list of choices (for different status) appear
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set array Adapter to Spinner
            spinner.setAdapter(aa)
        }
    }

    /**
     * asynchronous loading of profile picture from FB storage to view using Picasso
     */
    private fun initProfilePicture() {
        Log.d(TAG, "initProfilePicture")

        Log.d(TAG, "current user "+currentUserID)
        currentUserID?.let {

            val mStorageRef = FirebaseStorage.getInstance().getReference()
            mStorageRef.child("img/profilePics/$currentUserID").downloadUrl
                .addOnSuccessListener { result ->
                    Picasso.get()
                        .load(result)
                        .fit()
                        .centerCrop().into(profileImg)
                }
                .addOnFailureListener {
                    //Log.w(TAG, it) // exception is already printed in StorageException class
                    Log.d(TAG, "This user does not have a profile picture!")
                }

        }
    }

    private fun editOrSaveUsername() {
        if (userNameTextView.visibility == View.VISIBLE) {
            userNameTextView.visibility = View.GONE
            userNameEditText.setText(userNameTextView.text)
            userNameEditText.visibility = View.VISIBLE
            userNameButton.setImageResource(R.drawable.ic_action_check)
        } else {
            if (userNameEditText.text.isEmpty()) {
                Toast.makeText(context, "Der Benutzername kann nicht leer sein.", Toast.LENGTH_SHORT).show()
                return
            }
            userNameTextView.visibility = View.VISIBLE
            userNameEditText.visibility = View.GONE
            userNameButton.setImageResource(R.drawable.ic_action_edit)
            PushData.saveUserName(userNameEditText.text.toString())
            this.hideSoftKeyboard(userNameEditText)
        }
    }

    /**
     * when new status spinner item is selected, update status
     */
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        if(arg1 != null && position != null){
            currentStatus = status_array[position]
            PushData.setStatus(currentStatus)
            SharedPrefManager.instance.saveCurrentStatus(currentStatus)
        }
    }

    /**
     * function to pick a profile img
     * first checks if required permission to access storage is granted, then opens gallery and saves image to firebase storage
     */
    fun chooseProfileImg() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PICK_FROM_GALLERY
                )
            } else {
                openGallery()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PICK_FROM_GALLERY ->                 // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    showWarningPermissionNotGranted()
                }
        }
    }

    fun openGallery(){
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        this.startActivityForResult(galleryIntent, PICK_FROM_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data
            // Do work with photo saved at fullPhotoUri
            Log.d(TAG, "onActivityResult")
            Picasso.get().load(fullPhotoUri).into(profileImg)
            //profileImg.setImageURI(fullPhotoUri)
            uploadProfilePicture(fullPhotoUri, context)

        }
    }


    fun showWarningPermissionNotGranted(){
        //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
        AlertDialog.Builder(context)
            .setMessage(
                "Um ein Profilbild auswählen zu können, benötigen die App den Zugriff auf Medien auf deinem Gerät. " +
                        "Bitte gewähre der App den Zugriff auf Medien, um die Profilbildfunktion nutzen zu können."
            )
            .setCancelable(true)
            .show()
    }

    //evtl move this method to PushData
    fun uploadProfilePicture(fullPhotoUri: Uri?, context: Context?) {
        Log.d(TAG, "uploadProfilePicture")
        fullPhotoUri?.let{

            val currentUserID: String?= SharedPrefManager.instance.getUserId()
            currentUserID?.let{
                val ref: StorageReference = mStorageRef.child("img/profilePics/$currentUserID")

                ref.putFile(fullPhotoUri)
                    .addOnSuccessListener(OnSuccessListener<Any?> {Log.d(TAG, "uploadProfilePicture success") })
                    .addOnFailureListener(OnFailureListener {
                        Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }


    /**
     * also for status spinner
     */
    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    fun hideSoftKeyboard(editText: EditText) {
        val imm: InputMethodManager? = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }
}
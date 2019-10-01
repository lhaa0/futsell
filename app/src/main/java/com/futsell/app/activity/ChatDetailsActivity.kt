package com.futsell.app.activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.futsell.app.R
import com.futsell.app.adapter.ChatDetailListAdapter
import com.futsell.app.model.Friend
import com.futsell.app.util.*
import com.futsell.app.util.Const.Companion.NODE_IS_READ
import com.futsell.app.util.Const.Companion.NODE_RECEIVER_ID
import com.futsell.app.util.Const.Companion.NODE_RECEIVER_NAME
import com.futsell.app.util.Const.Companion.NODE_RECEIVER_PHOTO
import com.futsell.app.util.Const.Companion.NODE_SENDER_ID
import com.futsell.app.util.Const.Companion.NODE_SENDER_NAME
import com.futsell.app.util.Const.Companion.NODE_SENDER_PHOTO
import com.futsell.app.util.Const.Companion.NODE_TEXT
import com.futsell.app.util.Const.Companion.NODE_TIMESTAMP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class ChatDetailsActivity : AppCompatActivity() {
    companion object {
        var KEY_FRIEND = "FRIEND"

        fun navigate(activity: AppCompatActivity, transitionImage: View, obj: Friend) {
            val intent = Intent(activity, ChatDetailsActivity::class.java)
            intent.putExtra(KEY_FRIEND, obj)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage,
                KEY_FRIEND
            )
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }

    // give preparation animation activity transition


    private var btn_send: Button? = null
    private var et_content: EditText? = null
    lateinit var mAdapter: ChatDetailListAdapter
    lateinit var fAuth: FirebaseAuth

    private var listview: ListView? = null
    private var actionBar: ActionBar? = null
    private var friend: Friend? = null
    private val items = ArrayList<ChatMessage>()
    private var parent_view: View? = null
    internal lateinit var pfbd: ParseFirebaseData
    internal lateinit var set: SettingApi

    internal lateinit var chatNode: String
    internal lateinit var chatNode_1:String
    internal lateinit var chatNode_2:String

    internal lateinit var ref: DatabaseReference
    internal lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_details)
        parent_view = findViewById(android.R.id.content)
        pfbd = ParseFirebaseData(this)
        set = SettingApi(this)
        fAuth = FirebaseAuth.getInstance()

        ViewCompat.setTransitionName(parent_view!!,
            KEY_FRIEND
        )

        val intent = intent
        friend = intent.extras!!.getSerializable(KEY_FRIEND) as Friend
        initToolbar()

        iniComponen()
        chatNode_1 = fAuth.currentUser!!.uid + "-" + friend!!.id
        chatNode_2 = friend!!.id + "-" + fAuth.currentUser!!.uid

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Const.LOG_TAG, "Data changed from activity")
                if (dataSnapshot.hasChild(chatNode_1)) {
                    chatNode = chatNode_1
                } else if (dataSnapshot.hasChild(chatNode_2)) {
                    chatNode = chatNode_2
                } else {
                    chatNode = chatNode_1
                }
                items.clear()
                items.addAll(pfbd.getMessagesForSingleUser(dataSnapshot.child(chatNode)))


                for (data in dataSnapshot.child(chatNode).children) {
                    if (data.child(NODE_RECEIVER_ID).value!!.toString() == fAuth.currentUser!!.uid) {
                        data.child(NODE_IS_READ).ref.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                                mutableData.value = true
                                return Transaction.success(mutableData)
                            }

                            override fun onComplete(
                                databaseError: DatabaseError?,
                                b: Boolean,
                                dataSnapshot: DataSnapshot?
                            ) {

                            }
                        })
                    }
                }

                // TODO: 12/09/18 Change it to recyclerview
                mAdapter = ChatDetailListAdapter(
                    this@ChatDetailsActivity,
                    items,
                    fAuth.currentUser!!.uid
                )
                listview!!.adapter = mAdapter
                listview!!.requestFocus()
                registerForContextMenu(listview)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomToast(this@ChatDetailsActivity).showError(getString(R.string.error_could_not_connect))
            }
        }

        ref = FirebaseDatabase.getInstance().getReference(Const.MESSAGE_CHILD)
        ref.addValueEventListener(valueEventListener)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Tools.systemBarLolipop(this)
//        }
    }

    fun initToolbar() {
        actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setTitle(friend!!.name)
    }

    fun iniComponen() {
        listview = findViewById(R.id.listview) as ListView
        btn_send = findViewById(R.id.btn_send) as Button
        et_content = findViewById(R.id.text_content) as EditText
        btn_send!!.setOnClickListener {

            val hm = HashMap<String, Any>()
            hm.put(NODE_TEXT, et_content!!.text.toString())
            hm.put(NODE_TIMESTAMP, System.currentTimeMillis().toString())
            hm.put(NODE_RECEIVER_ID, friend!!.id)
            hm.put(NODE_RECEIVER_NAME, friend!!.name)
            hm.put(NODE_RECEIVER_PHOTO, friend!!.photo)
            hm.put(NODE_SENDER_ID, fAuth.currentUser!!.uid)
            hm.put(NODE_SENDER_NAME, fAuth.currentUser!!.email!!)
            hm.put(NODE_SENDER_PHOTO, "aaa")
            hm.put(NODE_IS_READ, false)

            ref.child(chatNode).push().setValue(hm)
            et_content!!.setText("")
            hideKeyboard()
        }
        et_content!!.addTextChangedListener(contentWatcher)
        if (et_content!!.length() == 0) {
            btn_send!!.visibility = View.GONE
        }
        hideKeyboard()
    }


    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val contentWatcher = object : TextWatcher {
        override fun afterTextChanged(etd: Editable) {
            if (etd.toString().trim { it <= ' ' }.length == 0) {
                btn_send!!.visibility = View.GONE
            } else {
                btn_send!!.visibility = View.VISIBLE
            }
            //draft.setContent(etd.toString());
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        //Remove the listener, otherwise it will continue listening in the background
        //We have service to run in the background
        ref.removeEventListener(valueEventListener)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
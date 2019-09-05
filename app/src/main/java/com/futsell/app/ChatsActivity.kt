package com.futsell.app

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.futsell.app.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.toolbar.*


class ChatsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView

    private var mLayoutManager: LinearLayoutManager? = null
    var mAdapter: ChatListAdapter? = null
    private var progressBar: ProgressBar? = null

    internal lateinit var valueEventListener: ValueEventListener
    internal lateinit var ref: DatabaseReference


    lateinit var fAuth : FirebaseAuth

    internal lateinit var pfbd: ParseFirebaseData
    internal lateinit var set: SettingApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        pfbd = ParseFirebaseData(this)
        set = SettingApi(this)
        fAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Cashier itchop"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // for system bar in lollipop
//            Tools.systemBarLolipop(this)
            //Create the scheduler
            val mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val builder = JobInfo.Builder(1, ComponentName(packageName, NotificationService::class.java!!.getName()))
            builder.setPeriodic(900000)
            //If it needs to continue even after boot, persisted needs to be true
            //builder.setPersisted(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            mJobScheduler.schedule(builder.build())
        }

        // activate fragment menu
//        setHasOptionsMenu(true)

        recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        progressBar = findViewById(R.id.progressBar) as ProgressBar

        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL_LIST
            )
        )


        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Const.LOG_TAG, "Data changed from fragment")
                if (dataSnapshot.value != null)
                // TODO: 25-05-2017 if number of items is 0 then show something else
                    mAdapter = ChatListAdapter(this@ChatsActivity, pfbd.getAllLastMessages(dataSnapshot, fAuth.currentUser!!.uid), fAuth.currentUser!!.uid)
                recyclerView.adapter = mAdapter

                mAdapter?.setOnItemClickListener(object : ChatListAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, obj: ChatMessage, position: Int) {
                        if (obj.receiver.id.equals(fAuth.currentUser!!.uid))
                            ChatDetailsActivity.navigate(
                                this@ChatsActivity,
                                view.findViewById(R.id.lyt_parent),
                                obj.sender
                            )
                        else if (obj.sender.id.equals(fAuth.currentUser!!.uid))
                            ChatDetailsActivity.navigate(
                                this@ChatsActivity,
                                view.findViewById(R.id.lyt_parent),
                                obj.receiver
                            )
                    }
                })

                bindView()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        ref = FirebaseDatabase.getInstance().getReference(Const.MESSAGE_CHILD)
        ref.addValueEventListener(valueEventListener)

    }

    fun bindView() {
        try {
            mAdapter!!.notifyDataSetChanged()
            progressBar!!.visibility = View.GONE
        } catch (e: Exception) {
        }

    }

    override fun onDestroy() {
        //Remove the listener, otherwise it will continue listening in the background
        //We have service to run in the background
        ref.removeEventListener(valueEventListener)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.logout -> {
//                PrefsHelper(this@MainActivity).setLogin(false)
                fAuth.signOut()
                startActivity(Intent(this@ChatsActivity, LoginActivity::class.java))
                finish()
            }
        }
        return true
    }
}
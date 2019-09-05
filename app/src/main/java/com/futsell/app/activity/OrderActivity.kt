package com.futsell.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsell.app.ChatDetailsActivity
import com.futsell.app.Friend
import com.futsell.app.R
import com.futsell.app.adapter.OrderAdapter
import com.futsell.app.model.ModelClock
import com.futsell.app.model.ModelFutsal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderActivity : AppCompatActivity() {

    val clocks = ArrayList<ModelClock>()
    var date = Calendar.getInstance().time
    lateinit var data : ModelFutsal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        rcOrder.layoutManager = LinearLayoutManager(this)

        data = intent.getSerializableExtra("data") as ModelFutsal

        title = data.nama_futsal
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        txtTgl.text = SimpleDateFormat("dd/MM/yyyy").format(date)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            txtTgl.text = "$dayOfMonth/${month+1}/$year"
        }

        txtLayout.setOnClickListener {
            if (expandLayout.isExpanded) {
                expandLayout.collapse()
//                calendarView.visibility = GONE
            }
            else {
                expandLayout.expand()
//                calendarView.visibility = VISIBLE
            }
        }

        val now = System.currentTimeMillis() - 1000;
        calendarView.minDate = now
        calendarView.maxDate = now+(1000*60*60*24*14) //After 7 Days from Now



        for (i in data.open_at!! .. data.close_at!!) {
            clocks.add(ModelClock(i, false))
        }

        rcOrder.adapter = OrderAdapter(clocks, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_order, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.goOrder -> {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Go Order!!!")
                    .setMessage("Mau cari musuh juga?")
                    .setNegativeButton("Tidak") { dialog, i ->
                        goOrder(false)
                    }
                    .setPositiveButton("Sparing") { dialog, i->
                        goOrder(true)
                    }
                    .setNeutralButton("Batal") { dialog, which ->

                    }
                dialog.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun goOrder(spar : Boolean){
        val jam = ArrayList<Int>()
        for (j in clocks){
            if (j.checked)
                jam.add(j.clock)
        }
        val fAuth = FirebaseAuth.getInstance()
//        val dbRef = FirebaseDatabase.getInstance().getReference("dataOrder/${fAuth.currentUser!!.uid}|${data.uid_admin}")
        val dbRef = FirebaseDatabase.getInstance().getReference("dataOrder/${fAuth.currentUser!!.uid}|${data.id_futsal}/")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    var key = 1
                    if (p0.exists()){
                        p0.children.indexOfLast {
                            key = it.key!!.toInt() + 1
                            true
                        }
                    }
                    val insert = FirebaseDatabase.getInstance().getReference("dataOrder/${fAuth.currentUser!!.uid}|${data.id_futsal}/$key")
                    insert.child("sparing").setValue(spar)
                    insert.child("jam").setValue(jam)
                    insert.child("tanggal").setValue(txtTgl.text.toString())
                    insert.child("idAdmin").setValue(data.uid_admin)
                    insert.push()

                    ChatDetailsActivity.navigate(this@OrderActivity, findViewById(R.id.rcOrder),
                        Friend(data.uid_admin.toString(), data.nama_futsal.toString() , "hhhhh")
                    )
                    finish()
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
    }
}

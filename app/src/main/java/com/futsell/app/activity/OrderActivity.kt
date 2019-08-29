package com.futsell.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsell.app.R
import com.futsell.app.adapter.OrderAdapter
import com.futsell.app.model.ModelClock
import com.futsell.app.model.ModelFutsal
import kotlinx.android.synthetic.main.activity_order.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderActivity : AppCompatActivity() {

    val clocks = ArrayList<ModelClock>()
    var date = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        rcOrder.layoutManager = LinearLayoutManager(this)

        val data = intent.getSerializableExtra("data") as ModelFutsal

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



        for (i in data.open_at .. data.close_at) {
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
        }
        return super.onOptionsItemSelected(item)
    }
}

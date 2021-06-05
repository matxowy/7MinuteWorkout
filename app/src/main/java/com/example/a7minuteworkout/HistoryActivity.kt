package com.example.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_bmi.*
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(toolbar_history_activity)

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true) //ustawia to nam back button
            actionbar.title = "Historia"
        }

        toolbar_history_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        getAllCompletedDates()
    }

    private fun getAllCompletedDates(){
        val dbHandler = SqliteOpenHelper(this, null) //tworzymy dbHandlera przez nas stworzonego
        val allCompletedDatesList = dbHandler.getAllCompletedDatesList() //zapisujemy do zmiennej wszystkie dane z bazy danych

        if(allCompletedDatesList.size > 0){ //jeżeli mamy jakieś wartości w liście to wtedy dajemy na visible tvHistory i rvHistory
            tvHistory.visibility = View.VISIBLE
            rvHistory.visibility = View.VISIBLE
            tvNoDataAvailable.visibility = View.GONE

            rvHistory.layoutManager = LinearLayoutManager(this)
            val historyAdapter = HistoryAdapter(this, allCompletedDatesList) //tworzymy zmienną na adapter z naszego stworzonego HistoryAdaptera
            rvHistory.adapter = historyAdapter //ustawiamy recylerView na nasz adapter czyli wpisujemy do niego dane itp
        }else{
            tvHistory.visibility = View.GONE
            rvHistory.visibility = View.GONE
            tvNoDataAvailable.visibility = View.VISIBLE
        }
    }
}
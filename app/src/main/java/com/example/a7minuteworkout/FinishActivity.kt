package com.example.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_finish.*
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        setSupportActionBar(toolbar_finish_activity) //wsparcie dla toolbara żeby się wyświetlał
        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true) //ustawiamy back button
        }

        toolbar_finish_activity.setNavigationOnClickListener {
            onBackPressed() //cofa nas tak jakbyśmy kliknęli przycisk cofnięcia w telefonie
        }

        btFinishActivity.setOnClickListener {
            finish()
        }

        addDateToDatabase()

    }

    private fun addDateToDatabase(){
        val calendar = Calendar.getInstance() //pobieramy aktualną datę i godzinę
        val dateTime = calendar.time //zapisujemy aktualną datę i godzinę do zmiennej dateTime
        Log.i("DATE: ", "" + dateTime) //wyrzucamy do logów tą datę

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()) //tworzymy pattern na datę
        val date = sdf.format(dateTime) //formatujemy dateTime do naszego formatu i wrzucamy ją do zmiennej date

        val dbHandler = SqliteOpenHelper(this, null) //tworzymy dbHandlera przez nas stworzonego
        dbHandler.addDate(date) //wywołujemy metodę addDate ze zmienną date
        Log.i("DATE: ","Added")
    }

}
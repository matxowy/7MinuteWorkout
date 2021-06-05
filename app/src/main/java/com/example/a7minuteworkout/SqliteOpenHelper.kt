package com.example.a7minuteworkout

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteOpenHelper(context: Context, factory: SQLiteDatabase.CursorFactory?): SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private val DATABASE_VERSION = 1 //wersja bazy danych
        private val DATABASE_NAME = "SevenMinutesWorkout.db" //nazwa bazy danych
        private val TABLE_HISTORY = "history" //nazwa tabeli
        private val COLUMN_ID = "_id" //kolumna id
        private val COLUMN_COMPLETED_DATE = "completed_date" //kolumna dla daty odbycia treningu
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_HISTORY + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_COMPLETED_DATE + " TEXT)")
        db?.execSQL(CREATE_EXERCISE_TABLE) //jeżeli istnieje baza danych (db?.) to wykonaj kod sql
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE " + TABLE_HISTORY) //usuwamy dotychczasową tabele
        onCreate(db) //i zastępujemy najnowszą wersją tej tabeli
    }

    fun addDate(date: String){
        val values = ContentValues() //przygotowujemy zmienną na wiele wartości
        values.put(COLUMN_COMPLETED_DATE, date) //przechowujemy każdą przekazaną wartość w naszej kolumnie completed_date
        val db = this.writableDatabase //otwieramy możliwość wpisywania do bazy danych
        db.insert(TABLE_HISTORY, null, values) //wrzucamy zapisane wartości w values do bazy danych
        db.close()
    }

    fun getAllCompletedDatesList() : ArrayList<String>{
        val list = ArrayList<String>()
        val db = this.readableDatabase //otwieramy bazę danych by z niej czytać
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY",null) //tworzymy kursor by przechodzić po każdym wyniku z bazy

        while (cursor.moveToNext()){
            val dateValue = (cursor.getString(cursor.getColumnIndex(COLUMN_COMPLETED_DATE))) //bierzemy wartość z kolumny completed_date na pozycji w której jest aktualnie kursor i zmieniamy ją na stringa i zapisujemy do zmiennej
            list.add(dateValue) //wrzucamy do listy poszczególne wartości
        }

        cursor.close() //zamykamy kursor

        return list

    }
}
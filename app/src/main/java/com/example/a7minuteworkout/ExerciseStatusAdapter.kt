package com.example.a7minuteworkout

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_exercise_status.view.*

class ExerciseStatusAdapter(val items: ArrayList<ExerciseModel>, val context: Context) : RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val tvItem = view.tvItem //przypisujemy do tvItem nasz wygląd jednego kółka
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exercise_status, parent, false)) //tą funkcją wyświetlamy to wszystko na ekranie
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //w tej nadpisanej metodzie patrzymy na każdy indywidualny element listy i możemy na nim działać
        val model: ExerciseModel = items[position] //zmienna przechowująca aktualny item z exercisemodel

        holder.tvItem.text = model.getId().toString() //ustawiamy przez id dobrą liczbę na naszym recyclerview

        //ustawiamy odpowiednie kolory tekstu i tła dla wybranego, ukończonego i jeszcze nie wybranego ćwiczenia z recyclerView
        if(model.getIsSelected()){
            holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_thin_color_accent_border)
            holder.tvItem.setTextColor(Color.parseColor("#212121"))
        }else if(model.getIsCompleted()){
            holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_color_accent_background)
            holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
        }else{
            holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_color_gray_background)
            holder.tvItem.setTextColor(Color.parseColor("#212121"))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
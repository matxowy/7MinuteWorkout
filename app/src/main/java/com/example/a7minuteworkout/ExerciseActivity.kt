package com.example.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null //zmienna na timer
    private var restProgress = 0 //zmienna określająca progress
    private var restTimerDuration: Long = 10 //zmienna do stopera

    private var exerciseTimer: CountDownTimer? = null //zmienna na timer
    private var exerciseProgress = 0 //zmienna określająca progress
    private var exerciseTimerDuration: Long = 30 //zmienna do stopera

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null //zmienna do czytania nazwy ćwiczeń
    private var player: MediaPlayer? = null //zmienna do odtwarzania dzwięku przy starcie odpoczynku

    private var exerciseAdapter: ExerciseStatusAdapter? = null //zmienna by ustawić cały recyclerview i wyswietlić na ekranie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        //ustawianie toolbara górnego którym będziemy mogli się cofać do poprzedniego activity
        setSupportActionBar(toolbar_exercise_activity) //ustawiamy wsparcie toolbara jako actionbar żeby się wyświetlał
        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
        }

        toolbar_exercise_activity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        tts = TextToSpeech(this, this)
        exerciseList = Constants.defaultExerciseList()//inicjalizujemy listę ćwiczeń poprzez funkcję z constants która zwróci nam listę podstawowych ćwiczeń
        setupRestView()

        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer != null){ //by sprawdzić czy można uzyć metody cancel bez problemu
            restTimer!!.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player != null){
            player!!.stop()
        }
        super.onDestroy()
    }

    private fun setRestProgressBar(){
        progressBar.progress = restProgress
        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000){ //ustawiamy timer, zmienna restTimerDuration * 1000 bo trzeba podac w milisekundach
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress = 10 - restProgress //ustawia dobry progress na progressbarze, jak będzie 9 sekunda to ustawia na 9 progressbar
                tvTimer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true) //zmieniamy pole isselected na true
                exerciseAdapter!!.notifyDataSetChanged() //przekazujemy informacje adapterowi, że zmieniły się dane i powinnien przekalkulować swój view


                setupExerciseView()
            }

        }.start()
    }

    private fun setupRestView(){
        try {
            player = MediaPlayer.create(applicationContext, R.raw.press_start) //tworzymy mediaplayer z wrzuconym dzwiękiem naszym do folderu raw
            player!!.isLooping = false //upewnienie się żeby dzwięk odtwarzał się tylko raz
            player!!.start() //odtworzenie dzwięku
        }catch (e: Exception){
            e.printStackTrace()
        }

        llRestView.visibility = View.VISIBLE //zmieniamy widoczność timera do przygotowania się na widoczny
        llExerciseView.visibility = View.GONE //zmieniamy timer ćwiczenia na na gone czyli niewidoczny

        if(restTimer != null){ //jeżeli jest coś w restTimer to musimy to wyczyścić żeby zacząć od nowa
            restTimer!!.cancel()
            restProgress = 0
        }

        tvUpcomingExercise.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar() //wywołujemy metodę która odpowiada za timer
    }

    private fun setExerciseProgressBar(){
        progressBarExercise.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000){ //ustawiamy timer, zmienna exerciseTimerDuration * 1000 bo trzeba podac w milisekundach
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBarExercise.progress = 30 - exerciseProgress //ustawia dobry progress na progressbarze, jak będzie 9 sekunda to ustawia na 9 progressbar
                tvTimerExercise.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1){ //jeżeli to nie ostatnie ćwiczenie to ustaw restView
                    exerciseList!![currentExercisePosition].setIsSelected(false) //zmieniamy pole isselected na false
                    exerciseList!![currentExercisePosition].setIsCompleted(true) //ustawiamy pole iscompleted na true
                    exerciseAdapter!!.notifyDataSetChanged() //przekazujemy informacje adapterowi, że zmieniły się dane i powinnien przekalkulować swój view
                    setupRestView() //ustawienie ponownie widoku odpoczynku
                }else{
                    finish() //kończymy exerciseActivity żeby nie móc wrócić do niego
                    startActivity(Intent(this@ExerciseActivity, FinishActivity::class.java))
                }
            }

        }.start()
    }

    private fun setupExerciseView(){

        llRestView.visibility = View.GONE //zmieniamy widoczność timera do przygotowania się na gone czyli niewidoczny
        llExerciseView.visibility = View.VISIBLE //zmieniamy timer ćwiczenia na widoczny

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        setExerciseProgressBar()

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage()) //pobieramy z listy aktualny obrazek do ćwiczenia i go ustawiamy
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName() //ustawiamy dobrą nazwę ćwiczenia
        speakOutExercise(exerciseList!![currentExercisePosition].getName()) //wywołujemy funkcję do mówienia nazwy ćwiczenia, w parametrze przekazujemy mu aktualne ćwiczenie do wypowiedzenia

    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US) //ustawiamy język speakera na angielski

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){ //sprawdzamy czy język nie jest być może nie wspierany bądź nie ma go w telefonie
                Log.e("TTS", "Jezyk nie jest wspierany")
            }
        }else{
            Log.e("TTS", "Initialization failed")
        }
    }

    //funkcja do mówienia ćwiczeń
    private fun speakOutExercise(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "") // zmiennej tts wywołujemy funckję speak i przekazujemy do niej to co mamy powiedzieć,
                                                                                    // parametr Queue_flush ucina w razie wypadku to co jest aktualnie mówione i zaczyna mówić text przekazany
    }

    private fun setupExerciseStatusRecyclerView(){
        rvExerciseStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) // przygotowujemy nasz recyclerView by wyglądał jak linearlayout w wersji horizontal
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!,this) //ustawiamy zmienna adapter dzięki naszej klasie którą napisaliśmy i przekazujemy tam listę ćwiczeń
        rvExerciseStatus.adapter = exerciseAdapter //ustawiamy adapter recyclerview na nasz stworzony
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)

        customDialog.setContentView(R.layout.dialog_custom_back_confirmation) //ustawiamy wygląd dialogu na wczesniej przygotowany w xml

        customDialog.tvYes.setOnClickListener {
            finish() //kończymy aktualną aktywność by cofnąć do głównej
            customDialog.dismiss() //usuwamy dialog
        }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }
}
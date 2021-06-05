package com.example.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_bmi.*
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    val METRIC_UNITS_VIEW = "METRIC_UNITS_VIEW"
    val US_UNITS_VIEW = "US_UNITS_VIEW"

    var currentVisibleView: String = METRIC_UNITS_VIEW


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        setSupportActionBar(toolbar_bmi_activity)

        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true) //ustawia to nam back button
            actionbar.title = "OBLICZ SWOJE BMI"
        }

        toolbar_bmi_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        btnCalculateUnits.setOnClickListener {
            if(currentVisibleView.equals(METRIC_UNITS_VIEW)){
                calculateBmiForMetricUnits()
            }else{
                calculateBmiForUsUnits()
            }
        }

        makeVisibleMetricUnitsView() //podstawowo ustawiamy widok metryczny
        rgUnits.setOnCheckedChangeListener { group, checkedId -> //w zależności od tego co jest zaznaczone czyli co jest w checkedId ustawiamy widoczność elementów metodami
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUsUnitsView()
            }
        }
    }

    private fun calculateBmiForMetricUnits(){
        if (validateMetricUnits()){
            val heightValue: Float = etMetricUnitHeight.text.toString().toFloat() / 100 //zapisujemy w zmiennej wysokość wprowadzoną przez użytkownika i konwertujemy to na float i dzielimy przez 100 żeby wyszły metry
            val weightValue: Float = etMetricUnitWeight.text.toString().toFloat()

            val bmi = weightValue / (heightValue * heightValue) //obliczamy bmi
            displayBMIResult(bmi)
        }else{
            Toast.makeText(this@BMIActivity, "Proszę wprowadzić poprawne wartości",Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateBmiForUsUnits(){
        if(validateUsUnits()){
            val usUnitHeightValueFeet: Float = etUsUnitHeightFeet.text.toString().toFloat()
            val usUnitHeightValueInch: Float = etUsUnitHeightInch.text.toString().toFloat()
            val usUnitWeightValue: Float = etUsUnitWeight.text.toString().toFloat()

            val heightValue = usUnitHeightValueInch + usUnitHeightValueFeet * 12

            val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue)) //formuła na obliczenie bmi z amerykańskich wartości
            displayBMIResult(bmi)
        }else{
            Toast.makeText(this@BMIActivity, "Proszę wprowadzić poprawne wartości",Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        tilMetricUnitHeight.visibility = View.VISIBLE
        tilMetricUnitWeight.visibility = View.VISIBLE

        etMetricUnitHeight.text!!.clear()
        etMetricUnitWeight.text!!.clear()

        tilUsUnitWeight.visibility = View.GONE
        llUsUnitsHeight.visibility = View.GONE

        llDisplayBMIResult.visibility = View.GONE
    }

    private fun makeVisibleUsUnitsView(){
        currentVisibleView = US_UNITS_VIEW
        tilMetricUnitHeight.visibility = View.GONE
        tilMetricUnitWeight.visibility = View.GONE

        //usuwamy wszelki tekst który był w edittextach przed zmianą
        etUsUnitHeightFeet.text!!.clear()
        etUsUnitHeightInch.text!!.clear()
        etUsUnitWeight.text!!.clear()

        tilUsUnitWeight.visibility = View.VISIBLE
        llUsUnitsHeight.visibility = View.VISIBLE

        llDisplayBMIResult.visibility = View.GONE
    }

    private fun displayBMIResult(bmi: Float){
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Bardzo poważna niedowaga!"
            bmiDescription = "Powinieneś bardziej zadbać o siebie. Jedz więcej!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = "Poważna niedowaga"
            bmiDescription = "Powinieneś bardziej zadbać o siebie. Jedz więcej!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Niedowaga"
            bmiDescription = "Powinieneś bardziej zadbać o siebie. Jedz więcej!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = "Waga prawidłowa"
            bmiDescription = "Gratulacje! Jesteś w dobrej kondycji!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = "Nadwaga"
            bmiDescription = "Powinieneś bardziej zadbać o siebie!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Nadwaga klasy I (umiarkowanie otyły)"
            bmiDescription = "Powinieneś bardziej zadbać o siebie!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Nadwaga klasy II (poważnie otyły)"
            bmiDescription = "Jesteś w bardzo niebezpiecznej kondycji. Zacznij działać teraz!"
        } else {
            bmiLabel = "Nadwaga klasy III (bardzo poważnie otyły)"
            bmiDescription = "Jesteś w bardzo niebezpiecznej kondycji. Zacznij działać teraz!"
        }

        llDisplayBMIResult.visibility = View.VISIBLE


        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString() //zaokrąglamy wartość bmi do dwóch miejsc po przecinku

        tvBMIValue.text = bmiValue
        tvBMIType.text = bmiLabel
        tvBMIDescription.text = bmiDescription
    }

    private fun validateMetricUnits(): Boolean{
        var isValid = true

        if (etMetricUnitWeight.text.toString().isEmpty()){
            isValid = false
        }else if (etMetricUnitHeight.text.toString().isEmpty()){
            isValid = false
        }

        return isValid
    }

    private fun validateUsUnits(): Boolean{
        var isValid = true

        if (etUsUnitWeight.text.toString().isEmpty()){
            isValid = false
        }else if (etUsUnitHeightFeet.text.toString().isEmpty()){
            isValid = false
        }else if (etUsUnitHeightInch.text.toString().isEmpty()){
            isValid = false
        }

        return isValid
    }
}
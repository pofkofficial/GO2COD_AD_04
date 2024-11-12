package com.example.unitconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var inputUnitSpinner: Spinner
    private lateinit var outputUnitSpinner: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultText: TextView
    private lateinit var historyRecyclerView: RecyclerView

    private val historyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        categorySpinner = findViewById(R.id.categorySpinner)
        inputUnitSpinner = findViewById(R.id.inputUnitSpinner)
        outputUnitSpinner = findViewById(R.id.outputUnitSpinner)
        inputValue = findViewById(R.id.inputValue)
        resultText = findViewById(R.id.resultText)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        setupCategorySpinner()
        setupHistoryRecyclerView()

        // Trigger conversion as user types
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = convertUnits()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Length", "Weight", "Temperature")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                populateUnitSpinners(categories[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun populateUnitSpinners(category: String) {
        val units = when (category) {
            "Length" -> arrayOf("Meter", "Kilometer", "Mile", "Yard")
            "Weight" -> arrayOf("Gram", "Kilogram", "Pound", "Ounce")
            "Temperature" -> arrayOf("Celsius", "Fahrenheit", "Kelvin")
            else -> arrayOf()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        inputUnitSpinner.adapter = adapter
        outputUnitSpinner.adapter = adapter
    }

    private fun convertUnits() {
        val category = categorySpinner.selectedItem.toString()
        val inputUnit = inputUnitSpinner.selectedItem.toString()
        val outputUnit = outputUnitSpinner.selectedItem.toString()
        val input = inputValue.text.toString().toDoubleOrNull() ?: return

        val result = when (category) {
            "Length" -> convertLength(input, inputUnit, outputUnit)
            "Weight" -> convertWeight(input, inputUnit, outputUnit)
            "Temperature" -> convertTemperature(input, inputUnit, outputUnit)
            else -> null
        }

        result?.let {
            val resultTextStr = "$input $inputUnit = $it $outputUnit"
            resultText.text = resultTextStr

            historyList.add(resultTextStr)
            historyRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun convertLength(value: Double, fromUnit: String, toUnit: String): Double? {
        val meters = when (fromUnit) {
            "Meter" -> value
            "Kilometer" -> value * 1000
            "Mile" -> value * 1609.34
            "Yard" -> value * 0.9144
            else -> return null
        }
        return when (toUnit) {
            "Meter" -> meters
            "Kilometer" -> meters / 1000
            "Mile" -> meters / 1609.34
            "Yard" -> meters / 0.9144
            else -> null
        }
    }

    private fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double? {
        val grams = when (fromUnit) {
            "Gram" -> value
            "Kilogram" -> value * 1000
            "Pound" -> value * 453.592
            "Ounce" -> value * 28.3495
            else -> return null
        }
        return when (toUnit) {
            "Gram" -> grams
            "Kilogram" -> grams / 1000
            "Pound" -> grams / 453.592
            "Ounce" -> grams / 28.3495
            else -> null
        }
    }

    private fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double? {
        return when (fromUnit to toUnit) {
            "Celsius" to "Fahrenheit" -> value * 9/5 + 32
            "Fahrenheit" to "Celsius" -> (value - 32) * 5/9
            "Celsius" to "Kelvin" -> value + 273.15
            "Kelvin" to "Celsius" -> value - 273.15
            "Fahrenheit" to "Kelvin" -> (value - 32) * 5/9 + 273.15
            "Kelvin" to "Fahrenheit" -> (value - 273.15) * 9/5 + 32
            else -> null
        }
    }

    private fun setupHistoryRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = HistoryAdapter(historyList)
    }
}

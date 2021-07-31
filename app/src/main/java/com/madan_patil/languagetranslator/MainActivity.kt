package com.madan_patil.languagetranslator

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    companion object{
        private const val REQUEST_PERMISSION_CODE = 1
    }

    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var sourceEdt: TextInputEditText
    private lateinit var micIV: ImageView
    private lateinit var translateBtn: MaterialButton
    private lateinit var translatedTV: TextView

    private var fromLanguages = listOf("From", "English", "Kannada", "Hindi", "Urdu", "Telugu", "Tamil", "Arabic",
            "Belorussian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh")

    private var toLanguages = listOf("To", "English", "Kannada", "Hindi", "Urdu", "Telugu", "Tamil", "Arabic",
            "Belorussian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh")

    var fromLanguageCode = 0
    var toLanguageCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fromSpinner = findViewById(R.id.idFromSpinner)
        toSpinner = findViewById(R.id.idToSpinner)
        sourceEdt = findViewById(R.id.idEditSource)
        micIV = findViewById(R.id.idIVMic)
        translateBtn = findViewById(R.id.idBtnTranslate)
        translatedTV = findViewById(R.id.idTranslatedTV)

        val fromAdapter = ArrayAdapter(this, R.layout.spinner_item, fromLanguages)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.adapter = fromAdapter

        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                fromLanguageCode = getLanguageCode(fromLanguages[position])

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        val toAdapter = ArrayAdapter(this, R.layout.spinner_item, toLanguages)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner.adapter = toAdapter


        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                toLanguageCode = getLanguageCode(toLanguages[position])

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        translateBtn.setOnClickListener {
            translatedTV.text = ""
            when {
                sourceEdt.text.toString().isEmpty() -> {
                    Toast.makeText(this@MainActivity, "Please enter a text to translate", Toast.LENGTH_SHORT).show()
                }
                fromLanguageCode == 0 -> {
                    Toast.makeText(this@MainActivity, "Please select a source language", Toast.LENGTH_SHORT).show()
                }
                toLanguageCode == 0 -> {
                    Toast.makeText(this@MainActivity, "Please select a translation language", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    translateText(fromLanguageCode, toLanguageCode, sourceEdt.text.toString())
                }
            }
        }

        micIV.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text")
            try {
                startActivityForResult(intent, REQUEST_PERMISSION_CODE)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(resultCode == RESULT_OK && data != null){
                val result: ArrayList<String> ?= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null) {
                    sourceEdt.setText(result[0])
                }
            }
        }
    }

    private fun translateText(fromLanguageCode: Int, toLanguageCode: Int, source: String) {

        translatedTV.text = getString(R.string.download_model)

        val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build()

        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        val conditions = FirebaseModelDownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            translatedTV.text = getString(R.string.translating)
            translator.translate(source).addOnSuccessListener { s -> translatedTV.text = s }.addOnFailureListener { e -> Toast.makeText(this@MainActivity, "Failed to translate: " + e.message, Toast.LENGTH_SHORT).show() }
        }.addOnFailureListener { e -> Toast.makeText(this@MainActivity, "Failed to download language model: " + e.message, Toast.LENGTH_SHORT).show() }

    }

    private fun getLanguageCode(language: String): Int {
        val languageCode: Int
        when(language){

            "English" -> languageCode = FirebaseTranslateLanguage.EN

            "Kannada" -> languageCode = FirebaseTranslateLanguage.KN

            "Hindi" -> languageCode = FirebaseTranslateLanguage.HI

            "Urdu" -> languageCode = FirebaseTranslateLanguage.UR

            "Telugu" -> languageCode = FirebaseTranslateLanguage.TE

            "Tamil" -> languageCode = FirebaseTranslateLanguage.TA

            "Arabic" -> languageCode = FirebaseTranslateLanguage.AR

            "Belorussian" -> languageCode = FirebaseTranslateLanguage.BE

            "Bulgarian" -> languageCode = FirebaseTranslateLanguage.BG

            "Bengali" -> languageCode = FirebaseTranslateLanguage.BN

            "Catalan"-> languageCode = FirebaseTranslateLanguage.CA

            "Czech" -> languageCode = FirebaseTranslateLanguage.CS

            "Welsh" -> languageCode = FirebaseTranslateLanguage.CY

            else -> languageCode = 0

        }

        return languageCode
    }
}
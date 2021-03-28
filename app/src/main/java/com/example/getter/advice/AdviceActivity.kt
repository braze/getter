package com.example.getter.advice

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.getter.R
import com.example.getter.advice.api.AdviceRetriever
import kotlinx.android.synthetic.main.activity_advice.*
import kotlinx.android.synthetic.main.activity_flip.toolbar
import kotlinx.android.synthetic.main.search_advice_dialog.view.*
import kotlinx.coroutines.*


class AdviceActivity : AppCompatActivity() {

    private lateinit var mNumber: TextView
    private lateinit var mAdvice: TextView
    val TAG: String = "AdviceActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to AppTheme for displaying the activity
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice)

        setSupportActionBar(toolbar)
        mNumber = findViewById(R.id.number_tv)
        mAdvice = findViewById(R.id.advice_body_tv)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (isNetworkConnected()) {
            retrieve(null)
        } else {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setIcon(R.drawable.ic_report_problem).show()
        }

        // new Advice button functionality
        new_advice_btn.setOnClickListener {
            if (isNetworkConnected()) {
                retrieve(null)
            } else {
                AlertDialog.Builder(this).setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again")
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_report_problem).show()
            }
        }

        // toolBar back button implementation
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.advice_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.action_advice_search) {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.search_advice_dialog, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Looking for advice")
            //show dialog
            val mAlertDialog = mBuilder.show()

            //login button click of custom layout
            mDialogView.dialog_search_btn.setOnClickListener {
                //dismiss dialog
//                mAlertDialog.dismiss()
                //get text from EditTexts of custom layout
                val number: String = mDialogView.dialog_advice_number.text.toString()

                //validate the number
                if (number.isNotEmpty()) {
                    val num = Integer.parseInt(number)
                    if (num in 1..250) {
                        //if OK than search advice
                        mAlertDialog.dismiss()
                        if (isNetworkConnected()) {
                            retrieve(number)
                        } else {
                            AlertDialog.Builder(this).setTitle("No Internet Connection")
                                .setMessage("Please check your internet connection and try again")
                                .setPositiveButton(R.string.ok) { _, _ -> }
                                .setIcon(R.drawable.ic_report_problem).show()
                        }
                    } else {
                        Toast.makeText(this, "No advice with such number", Toast.LENGTH_LONG).show()
                    }
                }
            }
            //cancel button click of custom layout
            mDialogView.dialog_cancel_btn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun retrieve(id: String?) {
        // Create a Coroutine scope using a job to be able to cancel when needed
        val adviceActivityJob = Job()

        // Handle exceptions if any
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            AlertDialog.Builder(this).setTitle("Error")
                .setMessage(exception.message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }

        // the Coroutine runs using the Main (UI) dispatcher
        val coroutineScope = CoroutineScope(adviceActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            val result = AdviceRetriever().getAdvice()
            // set text
            if (id != null) {
                mNumber.text = id
            } else {
                mNumber.text = result.slip.id.toString()
            }
            mAdvice.text = result.slip.advice
        }
    }


    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}
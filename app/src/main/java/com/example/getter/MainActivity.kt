package com.example.getter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getter.advice.AdviceActivity
import com.example.getter.flip.FlipActivity
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = readFromAsset()

        val adapter = CustomAdapter(model, this)
        val rcv = findViewById<RecyclerView>(R.id.rcv)
        rcv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rcv.adapter = adapter

        adapter.setOnClickListener(object : CustomAdapter.ClickListener{
            override fun onClick(pos: Int, aView: View) {
//                Toast.makeText(this@MainActivity, model.get(pos).name, Toast.LENGTH_LONG).show()
                //open new activity
                if (model[pos].name == "Want to make a decision?") {
                    val intent = Intent(applicationContext, FlipActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                } else if (model[pos].name == "Advice pocket") {
                    val intent = Intent(applicationContext, AdviceActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    private fun readFromAsset(): List<Model> {

        val modeList = mutableListOf<Model>()

        val bufferReader = application.assets.open("mini_apps.json").bufferedReader()
        val jsonString = bufferReader.use {
            it.readText()
        }
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val model = Model(
                    jsonObject.getString("name"),
                    jsonObject.getString("description")
            )
            modeList.add(model)
        }
        return modeList
    }
}
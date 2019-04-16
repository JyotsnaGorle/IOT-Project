package com.example.jol.spulenbee

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import com.google.gson.Gson
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.grid_data.view.*
import kotlinx.android.synthetic.main.prev_data.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ItemGridAdapter
    private lateinit var prevGridAdapter: PrevDataItemAdapter
    lateinit var errorMsg : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hasConnection = InternetConnectivityManager.hasConnection(this)
        if (hasConnection) {
            errorMsg = findViewById<TextView>(R.id.error_msg)
            errorMsg.visibility = View.VISIBLE
            getCurrentState()
        } else {
            AlertDialogManager.showAlert(this, "NO INTERNET!", "Please check your connection and try again")
        }
    }

    private fun getCurrentState() {
        val params = RequestParams()

        RestService.get("", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                try {
                    errorMsg.visibility = View.GONE
                    val gson = Gson()
                    val combinationData = gson.fromJson(response.toString(), CombinationData::class.java)
                    setView(combinationData.currentCombo.last())
                    setPrevDataView(combinationData.currentCombo)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseString: String?,
                throwable: Throwable
            ) {
                Log.e("response failure", statusCode.toString())
            }
        })
    }

    private fun setPrevDataView(currentCombo: ArrayList<ArrayList<String>>) {
        val grid = findViewById<GridView>(R.id.lastData)
        prevGridAdapter = PrevDataItemAdapter(this, currentCombo)
        grid.adapter = prevGridAdapter
    }

    private fun setView(latestState: java.util.ArrayList<String>) {
        val listWithoutDate = latestState.dropLast(1)
        val resources = this.getResources()
        val grid = findViewById<GridView>(R.id.grid)
        val gridList = ArrayList<GridData>()
        val icons = arrayListOf<String>("ic_dishwashing", "ic_mop", "ic_toilet", "ic_trash_bin")

        listWithoutDate.forEachIndexed { index, it ->
            val gridData = GridData(resources.getIdentifier(icons[index], "drawable", packageName), it);
            gridList.add(gridData)
        }
        adapter = ItemGridAdapter(this, gridList as ArrayList<GridData>)
        grid.adapter = adapter
    }

    class ItemGridAdapter(context: Context, var itemList: ArrayList<GridData>) : BaseAdapter() {
        var context: Context? = context

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val item = this.itemList[position]
            val inflator = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val itemView = inflator.inflate(R.layout.grid_data, null)
            itemView.itemName.text = item.name
            itemView.image_item.setImageResource(item.icon)
            return itemView
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return itemList.size
        }

        override fun getItem(position: Int): Any {
            return itemList[position]
        }
    }

    class PrevDataItemAdapter(context: Context, var itemList: ArrayList<ArrayList<String>>) : BaseAdapter() {
        var context: Context? = context

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val item = this.itemList[position]
            val inflator = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val itemView = inflator.inflate(R.layout.prev_data, null)
            if(item[4] != "Date") {
                val sdf = SimpleDateFormat("dd-MM-yyyy")
                val date = Date(item[4].toLong() * 1000)
                itemView.date.text = sdf.format(date)
            }
            else {
                itemView.date.text = item[4]
            }
            itemView.trash.text = item[0]
            itemView.kitchen.text = item[1]
            itemView.floor.text = item[2]
            itemView.toilet.text = item[3]
            return itemView
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return itemList.size
        }

        override fun getItem(position: Int): Any {
            return itemList[position]
        }
    }
}

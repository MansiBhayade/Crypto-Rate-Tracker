package com.example.crypto_rate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.crypto_rate.databinding.ActivityMainBinding
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
        private lateinit var rvAdapter: RvAdapter
        lateinit var data: ArrayList<Modal>
         private val handler = Handler(Looper.getMainLooper())
         var lastRefreshTime: String? = null


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            data = ArrayList<Modal>()
            apiData
            rvAdapter = RvAdapter(this, data)
            binding.RecyclerView.layoutManager = LinearLayoutManager(this)
            binding.RecyclerView.adapter = rvAdapter

            setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
            val searchView:SearchView = findViewById(R.id.search_bar);

            // To search based on typing by user
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    filter(query)
                    return true
                }
            })


            // Initialize SwipeRefreshLayout
            binding.swipeRefreshLayout.setOnRefreshListener {
                // Call the API data fetch function
                fetchData()
            }
            val retryButton: Button = findViewById(R.id.retryButton)
            retryButton.setOnClickListener {
                // When the retry button is clicked, attempt to fetch data again
                fetchData()
            }

            // Schedule data refresh every 3 minutes
            handler.postDelayed(object : Runnable {
                override fun run() {
                    fetchData()
                    handler.postDelayed(this, 3 * 60 * 1000) // 3 minutes in milliseconds
//                    Toast.makeText(applicationContext, "Data refreshed", Toast.LENGTH_SHORT).show()
                }
            }, 3 * 60 * 1000) // 3 minutes in milliseconds

        }

        val apiData: Unit
            get(){
                val url="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest" // To fetch latest data

                val queue = Volley.newRequestQueue(this)
                val jsonObjectRequest:JsonObjectRequest=

                    object:JsonObjectRequest(Method.GET,url,null, Response.Listener {
                        response ->
                        try{
                            val dataArray=response.getJSONArray("data")
                            for(i in 0 until dataArray.length())
                            {
                               val dataObject=dataArray.getJSONObject(i)
                                val symbol = dataObject.getString("symbol")
                                val name = dataObject.getString("name")
                                val quote = dataObject.getJSONObject("quote")
                                val USD = quote.getJSONObject("USD")

                                val price=USD.getDouble("price")
                                val imageID = dataObject.getInt("id")
                                val iconUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/$imageID.png"
                                data.add(Modal(name,symbol, price.toString(),iconUrl))
                            }
                            rvAdapter.notifyDataSetChanged()
                            // Update the last refresh time
                            updateLastRefreshTime()

                        } catch(e:Exception){
                          Toast.makeText(this,"error 1",Toast.LENGTH_LONG).show();
                            // Show the retry button
                            binding.retryButton.visibility = View.VISIBLE
                        }finally {

                            // Hide the refreshing animation
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this,"Error : Please Check your Internet Connection ",Toast.LENGTH_LONG).show();
                        // Hide the refreshing animation
                        binding.swipeRefreshLayout.isRefreshing = false
                        // Show the retry button
                        binding.retryButton.visibility = View.VISIBLE

                    })
                    {
                        override fun getHeaders(): Map<String, String> {
                            val headers=HashMap<String,String>();
                            headers["X-CMC_PRO_API_KEY"]="your key"
                            return headers
                        }
                    }

                queue.add(jsonObjectRequest)
            }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sort, menu)
        return true
    }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.sort_name -> {
                    // Perform sorting by name
                    Toast.makeText(this, "Sorting by name", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.sort_rate -> {
                    // Perform sorting by exchange rate
                    Toast.makeText(this, "Sorting by exchange rate", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }


        // Function to fetch data, invoked when SwipeRefreshLayout is triggered
        private fun fetchData() {
            // Call the API data fetch function
            apiData
            // Hide the retry button
            binding.retryButton.visibility = View.GONE
            // Display data refreshed message
            Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show()
        }
    // After fetching data in apiData method
    fun updateLastRefreshTime() {
        // Update last refresh time
        lastRefreshTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        binding.lastRefreshtext.text = "Last Refresh: $lastRefreshTime"
    }

    //Function to Filter Adapter based on SearchView input string
    private fun filter(query:String?){
        val filteredlist = ArrayList<Modal>();

        if (query.isNullOrBlank()) {
            rvAdapter.setadapterData(data)
        }else {
            for (item in data) {
                if (item.name.lowercase().contains(query)) {
                    filteredlist.add(item)
                }
            }
            rvAdapter.setadapterData(filteredlist)
        }
    }
    }

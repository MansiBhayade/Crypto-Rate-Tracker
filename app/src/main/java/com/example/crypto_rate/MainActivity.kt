package com.example.crypto_rate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.crypto_rate.databinding.ActivityMainBinding
import com.example.crypto_rate.databinding.RvItemBinding

    class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
        private lateinit var rvAdapter: RvAdapter
        private lateinit var data: ArrayList<Modal>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            data = ArrayList<Modal>()
            apiData
            rvAdapter = RvAdapter(this, data)
            binding.RecyclerView.layoutManager = LinearLayoutManager(this)
            binding.RecyclerView.adapter = rvAdapter
        }

        val apiData: Unit
            get(){
                val url="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"

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
                        } catch(e:Exception){
                          Toast.makeText(this,"error 1",Toast.LENGTH_LONG).show();
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this,"error",Toast.LENGTH_LONG).show();

                    })
                    {
                        override fun getHeaders(): Map<String, String> {
                            val headers=HashMap<String,String>();
                            headers["X-CMC_PRO_API_KEY"]="insert your key here"
                            return headers
                        }
                    }

                queue.add(jsonObjectRequest)
            }
    }

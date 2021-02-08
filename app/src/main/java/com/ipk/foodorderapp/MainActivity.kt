package com.ipk.foodorderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var foodList: ArrayList<Foods>
    private lateinit var adapter: FoodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar_main.title="Food App"
        setSupportActionBar(toolbar_main)

        rv_main.setHasFixedSize(true)
        rv_main.layoutManager=LinearLayoutManager(this@MainActivity)

        allFoods()

        fab_main.setOnClickListener{
            startActivity(Intent(this@MainActivity,BasketActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_search_menu,menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(p0: String): Boolean {
        Log.d("takip gönderilen arama",p0)
        searcedFoods(p0)
        return true
    }

    override fun onQueryTextChange(p0: String): Boolean {
        Log.d("takip harf girdikce",p0)
        searcedFoods(p0)
        return true
    }

    fun allFoods(){
        val url="http://kasimadalan.pe.hu/yemekler/tum_yemekler.php"

        val req = StringRequest(Request.Method.GET, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParse(res)
        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") })

        Volley.newRequestQueue(this@MainActivity).add(req)
    }

    fun searcedFoods(src:String){
        val url="http://kasimadalan.pe.hu/yemekler/tum_yemekler_arama.php"

        val req = object: StringRequest(Request.Method.POST, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParse(res)
        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") }){
            override fun getParams(): MutableMap<String, String> {
                val params=HashMap<String, String>()
                params["yemek_adi"]=src
                return params
            }
        }

        Volley.newRequestQueue(this@MainActivity).add(req)
    }

    fun jsonParse(res:String){
        try {
            foodList= ArrayList()

            val jsonObj=JSONObject(res)
            val foods =jsonObj.getJSONArray("yemekler")

            for(i in 0 until foods.length()){
                val f=foods.getJSONObject(i)

                val yemek_id=f.getInt("yemek_id")
                val yemek_adi=f.getString("yemek_adi")
                val yemek_resim_adi = f.getString("yemek_resim_adi")
                val yemek_fiyat=f.getInt("yemek_fiyat")

                Log.d(" takip yemek id: ", yemek_id.toString())
                Log.d(" takip yemek adi: ", yemek_adi)
                Log.d(" takip yemek resim adi: ", yemek_resim_adi)
                Log.d(" takip yemek fiyat: ", yemek_fiyat.toString())
                Log.d("takip","**************************************\n")

                val food=Foods(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat)
                foodList.add(food)
            }

            adapter= FoodsAdapter(this@MainActivity, foodList)
            rv_main.adapter=adapter

        }catch (e:JSONException){
            Log.d("takip hata:","parse hatası")
            e.printStackTrace()
        }
    }
}
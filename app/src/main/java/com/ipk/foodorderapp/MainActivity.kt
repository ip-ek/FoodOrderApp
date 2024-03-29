package com.ipk.foodorderapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_basket.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.log

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var foodList: ArrayList<Foods>
    private lateinit var adapter: FoodsAdapter
    private lateinit var sp:SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var basketList: ArrayList<BasketFoods>
    var amount=0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences("FoodAppSh", Context.MODE_PRIVATE)
        editor=sp.edit()

        toolbar_main.title=this.getString(R.string.app_name)
        setSupportActionBar(toolbar_main)

        rv_main.setHasFixedSize(true)
        rv_main.layoutManager=LinearLayoutManager(this@MainActivity)

        allFoods()
        updateFabText()

        fab_main.setOnClickListener{
            startActivity(Intent(this@MainActivity,BasketActivity::class.java))
        }
    } //onCreate

    override fun onResume() {
        updateFabText()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_search_menu,menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    } //onCreateOptionsMenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search -> {
                Log.d("takip", "search seçildi")
            }
            R.id.action_list -> {
                Log.d("takip", "filtre seçildi")
                showAlert()
            }
            else -> {
                Log.e("eroor", "Menu item hatası")
            }
        }
        return super.onOptionsItemSelected(item)
    } //onOptionsItemSelected

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } //onBackPressed

    override fun onQueryTextSubmit(p0: String): Boolean {
        Log.d("takip gönderilen arama",p0)
        searcedFoods(p0)
        return true
    } //onQueryTextSubmit

    override fun onQueryTextChange(p0: String): Boolean {
        Log.d("takip harf girdikce",p0)
        searcedFoods(p0)
        return true
    } //onQueryTextChange

    fun allFoods(){
        val url=this.getString(R.string.getAllFoods)

        val req = StringRequest(Request.Method.GET, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParse(res)
        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") })

        Volley.newRequestQueue(this@MainActivity).add(req)
    } //allFoods

    fun searcedFoods(src:String){
        val url=this.getString(R.string.searchFood)

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
    } //searcedFoods

    fun jsonParse(res:String){
        foodList= ArrayList()
        try {

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
                Log.d(" takip yemek resim: ", yemek_resim_adi)
                Log.d(" takip yemek fiyat: ", yemek_fiyat.toString())
                Log.d("takip","**************************************\n")

                val food=Foods(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat)
                foodList.add(food)
            }

        }catch (e:JSONException){
            Log.d("takip hata:","parse hatası")
            e.printStackTrace()
        }

        updateAdapter()
    } //jsonParse

    fun showAlert(){
        val ad=AlertDialog.Builder(this@MainActivity)
        ad.setTitle("Sırala:")
        //ad.setIcon(R.drawable.filter_list)
        var items = arrayOf(this.getString(R.string.def),this.getString(R.string.fromCheaper),this.getString(R.string.toCheaper),
            this.getString(R.string.A_to_Z),this.getString(R.string.Z_to_A))
        var checkedItem = sp.getInt("listItem",0)

        ad.setSingleChoiceItems(items, checkedItem,
                DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                    when(i){
                        0-> editor.putInt("listItem",0)
                        1-> editor.putInt("listItem",1)
                        2-> editor.putInt("listItem",2)
                        3-> editor.putInt("listItem",3)
                        4-> editor.putInt("listItem",4)
                    }
        })
        ad.setPositiveButton(this.getString(R.string.apply)){ d,i ->
            //Snackbar.make(toolbar_main, "Filtre Ayarlandı!", Snackbar.LENGTH_LONG).show()
            editor.commit()
            if (sp.getInt("listItem",0)==0) allFoods()
            else updateAdapter()
        }
        ad.setNegativeButton(this.getString(R.string.cancel)){ d,i ->
            //Snackbar.make(toolbar_main, "Filtre Ayarlanmadı!", Snackbar.LENGTH_LONG).show()
        }
        ad.create().show()
    } //showAlert

    fun updateAdapter(){
        var checkedItem = sp.getInt("listItem",0)

        when(checkedItem){
            0-> {//default
                //pass
            }
            1-> foodList.sortBy { it.yemek_fiyat } //cheap to expensive
            2-> foodList.sortByDescending { it.yemek_fiyat } //expensive to cheap
            3-> foodList.sortBy { it.yemek_adi } //A to Z
            4-> foodList.sortByDescending { it.yemek_adi } //Z to A
        }

        adapter= FoodsAdapter(this@MainActivity, foodList)
        rv_main.adapter=adapter
    } //updateAdapter

    fun updateFabText(){ //all orders
        val url=this.getString(R.string.getAllBasket)

        val req = StringRequest(Request.Method.GET, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParseBasket(res)
            amount=calculatePrice()
            update()
            Log.d("takip list:", basketList.size.toString())
            Log.d("amount",amount.toString())
        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") })

        Volley.newRequestQueue(this@MainActivity).add(req)

    } //allOrders

    fun jsonParseBasket(res:String){
        basketList= ArrayList()

        try {
            val jsonObj= JSONObject(res)
            val foods =jsonObj.getJSONArray("sepet_yemekler")

            for(i in 0 until foods.length()){
                val f=foods.getJSONObject(i)

                val yemek_id=f.getInt("yemek_id")
                val yemek_adi=f.getString("yemek_adi")
                val yemek_resim_adi = f.getString("yemek_resim_adi")
                val yemek_fiyat=f.getInt("yemek_fiyat")
                val yemek_siparis_adet=f.getInt("yemek_siparis_adet")

                val food=BasketFoods(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat,yemek_siparis_adet)
                basketList.add(food)
            }

        }catch (e: JSONException){
            Log.d("takip hata:","parse hatası")
            e.printStackTrace()
        }

    } //jsonParseBasket

    fun calculatePrice():Int{
        var price=0
        for(i in 0 until basketList.size){
            price+=basketList[i].yemek_fiyat*basketList[i].yemek_siparis_adet
        }
        return price
    } //calculatePrice

    fun update(){
        Log.d("amount fonk",amount.toString())

        if (amount==0){
            fab_main.shrink()
        }else{
            fab_main.text=amount.toString()+"${this.getString(R.string.TL)}"
            fab_main.extend()
        }
    } //update

}
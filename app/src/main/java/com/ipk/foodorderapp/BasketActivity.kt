package com.ipk.foodorderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_basket.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class BasketActivity : AppCompatActivity() {
    private lateinit var foodList: ArrayList<BasketFoods>
    private lateinit var adapter: BasketFoodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        toolbar_basket.title=this.getString(R.string.basket)
        setSupportActionBar(toolbar_basket)

        rv_basket.setHasFixedSize(true)
        rv_basket.layoutManager= LinearLayoutManager(this@BasketActivity)

        allOrders()

    } //onCreate

    fun allOrders(){
        val url=this.getString(R.string.getAllBasket)

        val req = StringRequest(Request.Method.GET, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParse(res)
            Log.d("takip list:", foodList.size.toString())
            if (foodList.size>0){
                btn_basket.text = "${calculatePrice()} ${"\u20BA"}"
            }else{
                btn_basket.text = this.getString(R.string.empty_basket)
            }

        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") })

        Volley.newRequestQueue(this@BasketActivity).add(req)

    } //allOrders

    fun jsonParse(res:String){
        foodList= ArrayList()

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

                Log.d(" takip yemek id: ", yemek_id.toString())
                Log.d(" takip yemek adi: ", yemek_adi)
                Log.d(" takip yemek resim adi: ", yemek_resim_adi)
                Log.d(" takip yemek fiyat: ", yemek_fiyat.toString())
                Log.d("takip","**************************************\n")

                val food=BasketFoods(yemek_id, yemek_adi, yemek_resim_adi, yemek_fiyat,yemek_siparis_adet)
                foodList.add(food)
            }

        }catch (e: JSONException){
            Log.d("takip hata:","parse hatası")
            e.printStackTrace()
        }
        listConcat()

        adapter= BasketFoodsAdapter(this@BasketActivity, foodList)
        rv_basket.adapter=adapter
    } //jsonParse

    fun calculatePrice():Int{
        var price=0
        for(i in 0 until foodList.size){
            price+=foodList[i].yemek_fiyat*foodList[i].yemek_siparis_adet
        }
        return price
    } //calculatePrice

    fun listConcat(){
        var tmpList:ArrayList<BasketFoods>
        tmpList= ArrayList()
        var flag:Boolean
        for(i in 0 until foodList.size){
            flag=false
            tmpList.forEach { each->
                if (each.yemek_id.equals(foodList[i].yemek_id)){
                    each.yemek_siparis_adet+=foodList[i].yemek_siparis_adet
                    flag=true
                }
            }
            if (!flag){
                tmpList.add(foodList[i])
            }
        }
        foodList= tmpList.clone() as ArrayList<BasketFoods>
    } //listConcat

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.basket_delete_menu,menu)
        return super.onCreateOptionsMenu(menu)
    } //onCreateOptionsMenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.del_basket -> {
                Log.d("takip", "sepetin silinmesi seçildi")
                deleteBasket()
            }
            else -> {
                Log.e("eroor", "Menu item hatası")
            }
        }
        return super.onOptionsItemSelected(item)
    } //onOptionsItemSelected

    fun deleteBasket(){
        foodList.forEach { each->
            deleteFromBasket(each)
            allOrders()
        }
    } //deleteBasket

    fun deleteFromBasket(food:BasketFoods){
        val url=this.getString(R.string.deleteFromBasket)
        val req= object : StringRequest(Request.Method.POST,url, Response.Listener { res ->
            Log.d("takip sil cevap", res)

        }, Response.ErrorListener { Log.d("Takip sil","hata") }){
            override fun getParams(): MutableMap<String, String> {
                val params=HashMap<String,String>()
                params["yemek_id"]=food.yemek_id.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(req)

    } //deleteFromBasket
}
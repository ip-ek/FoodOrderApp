package com.ipk.foodorderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        toolbar_basket.title="Sepet"
        setSupportActionBar(toolbar_basket)

        rv_basket.setHasFixedSize(true)
        rv_basket.layoutManager= LinearLayoutManager(this@BasketActivity)

        allOrders()



    }

    fun allOrders(){
        val url="http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val req = StringRequest(Request.Method.GET, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParse(res)
            Log.d("takip list:", foodList.size.toString())
            btn_basket.text = "${calculatePrice()} ${"\u20BA"}"
        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") })

        Volley.newRequestQueue(this@BasketActivity).add(req)
    }

    fun jsonParse(res:String){
        try {
            foodList= ArrayList()

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

            adapter= BasketFoodsAdapter(this@BasketActivity, foodList)
            rv_basket.adapter=adapter

        }catch (e: JSONException){
            Log.d("takip hata:","parse hatası")
            e.printStackTrace()
        }
    }

    fun calculatePrice():Int{
        var price=0
        for(i in 0 until foodList.size){
            price+=foodList[i].yemek_fiyat*foodList[i].yemek_siparis_adet
        }
        return price
    }
}
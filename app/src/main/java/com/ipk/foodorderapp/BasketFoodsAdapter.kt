package com.ipk.foodorderapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_basket.*
import org.json.JSONException
import org.json.JSONObject

class BasketFoodsAdapter(var mContext: Context, var foodList:ArrayList<BasketFoods>): RecyclerView.Adapter<BasketFoodsAdapter.CardHolder>() {

    inner class CardHolder(view: View):RecyclerView.ViewHolder(view){
        var card_view: CardView
        var card_name: TextView
        var card_detail:TextView
        var card_price: TextView
        var card_img: ImageView
        var card_delete: ImageView

        init {
            card_view=view.findViewById(R.id.basket_card)
            card_name=view.findViewById(R.id.card_basket_name)
            card_detail=view.findViewById(R.id.card_basket_det_price)
            card_price=view.findViewById(R.id.basket_food_price)
            card_img=view.findViewById(R.id.basket_food_img)
            card_delete=view.findViewById(R.id.basket_delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(LayoutInflater.from(mContext).inflate(R.layout.card_basket,parent,false))
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        var food =foodList.get(position)
        //holder.card_img
        holder.card_price.text="${food.yemek_fiyat*food.yemek_siparis_adet} ${"\u20BA"}"
        holder.card_name.text="${food.yemek_adi}"
        holder.card_detail.text= "${food.yemek_siparis_adet} x ${food.yemek_fiyat} ${"\u20BA"}"

        val url2 = "http://kasimadalan.pe.hu/yemekler/resimler/${food.yemek_resim_adi}"
        Picasso.get().load(url2).into(holder.card_img)

        holder.card_delete.setOnClickListener {
            deleteFromBasket(food)
        }
    }

    fun deleteFromBasket(food:BasketFoods){
        val url="http://kasimadalan.pe.hu/yemekler/delete_sepet_yemek.php"
        val req= object : StringRequest(Request.Method.POST,url, Response.Listener { res ->
            Log.d("Takip ekle cevap", res)
            //notifyDataSetChanged()
            allOrders()
        }, Response.ErrorListener { Log.d("Takip ekle","hata") }){
            override fun getParams(): MutableMap<String, String> {
                val params=HashMap<String,String>()
                params["yemek_id"]=food.yemek_id.toString()
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(req)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun allOrders(){
        val url="http://kasimadalan.pe.hu/yemekler/tum_sepet_yemekler.php"

        val req = StringRequest(Request.Method.GET, url, Response.Listener { res->
            Log.d("takip veri okuma: ", res)
            jsonParse(res)
        }, Response.ErrorListener { Log.d("takip hata: ", "Veri okuma") })

        Volley.newRequestQueue(mContext).add(req)
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

            notifyDataSetChanged()

        }catch (e: JSONException){
            Log.d("takip hata:","parse hatası")
            e.printStackTrace()
        }
    }

}
package com.ipk.foodorderapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso

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
    } //CardHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(LayoutInflater.from(mContext).inflate(R.layout.card_basket,parent,false))
    } //onCreateViewHolder

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        var food =foodList.get(position)
        //holder.card_img
        holder.card_price.text="${food.yemek_fiyat*food.yemek_siparis_adet} ${mContext.getString(R.string.TL)}"
        holder.card_name.text="${food.yemek_adi}"
        holder.card_detail.text= "${food.yemek_siparis_adet} x ${food.yemek_fiyat} ${mContext.getString(R.string.TL)}"

        val url2 = "${mContext.getString(R.string.getPics)}${food.yemek_resim_adi}"
        Picasso.get().load(url2).into(holder.card_img)

        holder.card_delete.setOnClickListener {
            deleteFromBasket(food)

        }
    } //onBindViewHolder

    fun deleteFromBasket(food:BasketFoods){
        val url=mContext.getString(R.string.deleteFromBasket)
        val req= object : StringRequest(Request.Method.POST,url, Response.Listener { res ->
            Log.d("takip sil cevap", res)
            (mContext as BasketActivity).allOrders() //the best solution ever
        }, Response.ErrorListener { Log.d("Takip sil","hata") }){
            override fun getParams(): MutableMap<String, String> {
                val params=HashMap<String,String>()
                params["yemek_id"]=food.yemek_id.toString()
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(req)
        //notifyDataSetChanged()
    } //deleteFromBasket

    override fun getItemCount(): Int {
        return foodList.size
    } //getItemCount

}
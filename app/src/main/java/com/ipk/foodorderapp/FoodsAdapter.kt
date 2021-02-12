package com.ipk.foodorderapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detailed_food.*

class FoodsAdapter(var mContext: Context, var foodList:ArrayList<Foods>): RecyclerView.Adapter<FoodsAdapter.CardHolder>() {

    inner class CardHolder(view: View):RecyclerView.ViewHolder(view){
        var card_view:CardView
        var card_name:TextView
        var card_price:TextView
        var card_img:ImageView
        var card_detail:ConstraintLayout
        var card_more:ImageView
        var btn_add:Button
        var btn_plus:Button
        var btn_min:Button
        var tw_count:TextView

        init {
            card_view=view.findViewById(R.id.foods_card)
            card_name=view.findViewById(R.id.card_food_name)
            card_price=view.findViewById(R.id.card_food_price)
            card_img=view.findViewById(R.id.card_food_img)
            card_detail=view.findViewById(R.id.card_detail)
            card_more=view.findViewById(R.id.btn_more)
            btn_add=view.findViewById(R.id.btn_add_open)
            btn_plus=view.findViewById(R.id.btn_plus)
            btn_min=view.findViewById(R.id.btn_min)
            tw_count=view.findViewById(R.id.tw_count)

        }
    } //CardHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(LayoutInflater.from(mContext).inflate(R.layout.card_design,parent,false))
    } //onCreateViewHolder

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        var food =foodList.get(position)

        holder.card_price.text="${food.yemek_fiyat} ${mContext.getString(R.string.TL)}"
        holder.card_name.text="${food.yemek_adi}"

        val url2 = "${mContext.getString(R.string.getPics)}${food.yemek_resim_adi}"
        Picasso.get().load(url2).into(holder.card_img)

        holder.card_view.setOnClickListener {
            if(holder.card_detail.visibility==View.GONE){
                holder.tw_count.text="1"
                holder.card_detail.visibility=View.VISIBLE
            }else{
                holder.card_detail.visibility=View.GONE
            }
        }

        openners(holder, food)
    } //onBindViewHolder

    override fun getItemCount(): Int {
        return foodList.size
    } //getItemCount

    fun openners(holder:CardHolder, food: Foods){
        holder.btn_min.setOnClickListener {
            if(holder.tw_count.text!="1"){
                holder.tw_count.text=(holder.tw_count.text.toString().toInt()-1).toString()
            }
        }

        holder.btn_plus.setOnClickListener {
            holder.tw_count.text=(holder.tw_count.text.toString().toInt()+1).toString()
        }

        holder.btn_add.setOnClickListener {
            addToBasket(holder, food,holder.tw_count.text.toString())
        }

        holder.card_more.setOnClickListener {
            val intent=Intent(mContext, DetailedFoodActivity::class.java)
            intent.putExtra("food", food)
            mContext.startActivity(intent)
            holder.card_detail.visibility=View.GONE
        }
    } //openners

    fun addToBasket(holder:CardHolder, food:Foods, count:String){
        val url=mContext.getString(R.string.addToBasket)
        val req= object : StringRequest(Request.Method.POST,url, Response.Listener { res ->
            Log.d("Takip ekle cevap", res)
            holder.card_detail.visibility=View.GONE
        }, Response.ErrorListener { Log.d("Takip ekle","hata") }){
            override fun getParams(): MutableMap<String, String> {
                val params=HashMap<String,String>()
                params["yemek_id"]=food.yemek_id.toString()
                params["yemek_adi"]=food.yemek_adi
                params["yemek_resim_adi"]=food.yemek_resim_adi
                params["yemek_fiyat"]=food.yemek_fiyat.toString()
                params["yemek_siparis_adet"]=count
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(req)
    }// addToBasket

}
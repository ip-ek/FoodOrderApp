package com.ipk.foodorderapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FoodsAdapter(var mContext: Context, var foodList:ArrayList<Foods>): RecyclerView.Adapter<FoodsAdapter.CardHolder>() {

    inner class CardHolder(view: View):RecyclerView.ViewHolder(view){
        var card_view:CardView
        var card_name:TextView
        var card_price:TextView
        var card_img:ImageView

        init {
            card_view=view.findViewById(R.id.foods_card)
            card_name=view.findViewById(R.id.card_food_name)
            card_price=view.findViewById(R.id.card_food_price)
            card_img=view.findViewById(R.id.card_food_img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return CardHolder(LayoutInflater.from(mContext).inflate(R.layout.card_design,parent,false))
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        var food =foodList.get(position)
        //holder.card_img
        holder.card_price.text="${food.yemek_fiyat} ${"\u20BA"}"
        holder.card_name.text="${food.yemek_adi}"

        val url2 = "http://kasimadalan.pe.hu/yemekler/resimler/${food.yemek_resim_adi}"
        Picasso.get().load(url2).into(holder.card_img)

        holder.card_view.setOnClickListener {
            val intent=Intent(mContext, DetailedFoodActivity::class.java)
            intent.putExtra("food", food)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}
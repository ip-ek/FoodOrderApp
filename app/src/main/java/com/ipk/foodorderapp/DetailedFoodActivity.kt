package com.ipk.foodorderapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detailed_food.*
import kotlinx.android.synthetic.main.activity_main.*

class DetailedFoodActivity : AppCompatActivity() {

    private lateinit var food:Foods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_food)

        food=intent.getSerializableExtra("food") as Foods

        toolbar_title.setText(food.yemek_adi)
        setSupportActionBar(toolbar_main)

        detailed_price.text="${food.yemek_fiyat} ${this.getString(R.string.TL)}"
        val url2 = "http://kasimadalan.pe.hu/yemekler/resimler/${food.yemek_resim_adi}"
        Picasso.get().load(url2).into(detailed_image)
        totalUpdate()

        btn_up.setOnClickListener {
            detailed_count.text=(detailed_count.text.toString().toInt()+1).toString()
            totalUpdate()
        }

        btn_down.setOnClickListener {
            if(detailed_count.text!="1"){
                detailed_count.text=(detailed_count.text.toString().toInt()-1).toString()
                totalUpdate()
            }
        }

        btn_add.setOnClickListener {
            addToBasket(food, detailed_count.text.toString())
        }

    } //onCreate

    fun totalUpdate(){
        detailed_price_count.text="${detailed_count.text} x ${food.yemek_fiyat} ${this.getString(R.string.TL)}"
        var total=detailed_count.text.toString().toInt()*food.yemek_fiyat
        detailed_total_price.text="${this.getString(R.string.total)}: ${total} ${this.getString(R.string.TL)}"
    } //totalUpdate

    fun addToBasket(food:Foods, count:String){
        val url=this.getString(R.string.addToBasket)
        val req= object : StringRequest(Request.Method.POST,url, Response.Listener { res ->
            Log.d("Takip ekle cevap", res)
            //Snackbar.make(btn_add, "${count} adet ${food.yemek_adi} eklendi.", Snackbar.LENGTH_SHORT).show()
            startActivity(Intent(this@DetailedFoodActivity,MainActivity::class.java))
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

        Volley.newRequestQueue(this@DetailedFoodActivity).add(req)
    } //addToBasket
}
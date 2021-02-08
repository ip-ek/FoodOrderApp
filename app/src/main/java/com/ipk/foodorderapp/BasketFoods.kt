package com.ipk.foodorderapp

import java.io.Serializable

data class BasketFoods(var yemek_id:Int, var yemek_adi:String, var yemek_resim_adi:String, var yemek_fiyat:Int, var yemek_siparis_adet:Int):Serializable {
}
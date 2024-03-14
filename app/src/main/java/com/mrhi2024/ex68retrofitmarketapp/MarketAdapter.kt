package com.mrhi2024.ex68retrofitmarketapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.mrhi2024.ex68retrofitmarketapp.databinding.RecyclerItemBinding

class MarketAdapter(val context: Context, val itemList: List<MarketItem>) :
    Adapter<MarketAdapter.VH>() {

    inner class VH(val binding: RecyclerItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutInflater = LayoutInflater.from(context)
        val binding = RecyclerItemBinding.inflate(layoutInflater)
        return VH(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item: MarketItem = itemList[position]

        // 텍스트 지정
        holder.binding.tvTitle.text = item.title
        holder.binding.tvMsg.text = item.msg
        holder.binding.tvPrice.text = item.price + "원"

        // 이미지 보여주기 [ DB에는 이미지경로가 "./upload/IMG_xxxx.jpg" 이기때문에 ]
        // 안드로이드 에서는 서버의 전체 주소가 필요함
        val imgUrl = "http://nameskdlxm.dothome.co.kr/05Retrofit/${item.file}"
        //주소가 올바른지 확인하기
        Log.d("imgUrl",imgUrl)

        Glide.with(context).load(imgUrl).into(holder.binding.iv)

    }
}
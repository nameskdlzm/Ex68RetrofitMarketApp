package com.mrhi2024.ex68retrofitmarketapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mrhi2024.ex68retrofitmarketapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val binding  by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // 서버에서 가져올 대량의 데이터
    var itemList:MutableList<MarketItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fabEdit.setOnClickListener { startActivity(Intent(this,EditActivity::class.java)) }

        binding.recyclerView.adapter = MarketAdapter(this,itemList)

    }

    //액티비티가 화면에 완전히 보여질 때 자동호출 되는 라이프 사이클 메소드
    override fun onResume() {
        super.onResume()

        loadDB()
    }

    // 서버에서 데이터를 불러오는 기능 메소드
    private fun loadDB(){

        val retrofit = RetrofitHelper.getRetrofitInstance()
        val retrofitService = retrofit.create(RetrofitService::class.java)
        retrofitService.loadDataFromServer().enqueue(object : Callback<List<MarketItem>>{
            override fun onResponse(call: Call<List<MarketItem>>, response: Response<List<MarketItem>>) {
                // 기존 데이터들을 모두 제거
                itemList.clear()
                binding.recyclerView.adapter!!.notifyDataSetChanged()

                // 결과로 받아온 List<MarketItem> 를 리사이클러뷰에 보여주기
                val items:List<MarketItem>? = response.body()
                items?.forEach {
                    itemList.add(0,it) // 추가되는 아이템을 가장 앞순서로 배치
                    binding.recyclerView.adapter!!.notifyItemInserted(0)
                }
            }

            override fun onFailure(call: Call<List<MarketItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "헤에?:${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

}
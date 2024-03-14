package com.mrhi2024.ex68retrofitmarketapp

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.mrhi2024.ex68retrofitmarketapp.databinding.ActivityEditBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class EditActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditBinding.inflate(layoutInflater) }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri: Uri? = it.data?.data
            uri?.let {
                Glide.with(this).load(it).into(binding.iv)

                // 이미지를 서버에 업로드 하려면 uri가 아니라 실제 절대경로가 필요함
                // uri --> 절대경로
                imgPath = getRealPathFromUri(uri)
            }
        }

    var imgPath: String? = null

    //Uri를 전달받아 실제 파일 경로를 리턴해주는 기능 메소드 구현하기
    private fun getRealPathFromUri(uri: Uri): String? {

        //android 10 버전 부터는 Uri를 통해 파일의 실제 경로를 얻을 수 있는 방법이 없어졌음
        //그래서 uri에 해당하는 파일을 복사하여 임시로 파일을 만들고 그 파일의 경로를 이용하여 서버에 전송

        // Uri[ 미디어저장소의 DB주소 ] 로 부터 파일의 이름을 얻어오기 - DB SELECT 쿼리작업을 해주는 기능을 가진 객체를 이용
        val cursorLoader: CursorLoader = CursorLoader(this, uri, null, null, null, null)
        val cursor: Cursor? = cursorLoader.loadInBackground()
        val fileName: String? = cursor?.run {
            moveToFirst()
            getString(getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
        }//-------------------------------------------------------------------------------------


        //복사본이 저장될 파일의 경로와 파일명 확장자
        val file: File = File(externalCacheDir, fileName)


        // 복사작업 수행
        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null
        val outputStream: OutputStream = FileOutputStream(file)

        // 파일복사
        while (true) {
            val buf: ByteArray = ByteArray(1024) // 빈 바이트 배열 [ 길이:1KB ]
            val len: Int = inputStream.read(buf) // 스트림을 통해 읽어들인 바이트들을 buf 배열에 넣어줌 -- 읽어들인 바이트 수를 리턴해 줌
            if (len <= 0) break


            outputStream.write(buf, 0, len)
        }

        // 반복문이 끝났으면 복사가 완료된것임
        inputStream.close()
        outputStream.close()

//        AlertDialog.Builder(this).setMessage(file.absolutePath).create().show()

        return file.absolutePath
    }

    // 선택한 이미지의 절대경로를 저장할 변수


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSelect.setOnClickListener { clickSelect() }
        binding.btnComplete.setOnClickListener { clickComplete() }

    }

    private fun clickSelect() {
        val intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Intent(MediaStore.ACTION_PICK_IMAGES) else Intent(
                Intent.ACTION_OPEN_DOCUMENT
            ).setType("{image/*")
        resultLauncher.launch(intent)

    }

    private fun clickComplete() {

        //작성한 데이터들 업로드 하기

        // 전송할 데이터들 [ name, title , msg , price, imgPath ]
        var name = binding.inputName.editText!!.text.toString()
        var title = binding.inputTitle.editText!!.text.toString()
        var msg = binding.inputMsg.editText!!.text.toString()
        var price = binding.inputPrice.editText!!.text.toString()

        // 레트로핏 작업 5단계
        var retrofit = RetrofitHelper.getRetrofitInstance()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        // 먼저 String 데이터들은 Map collection 으로 묶어서 전송 :  @PartMap
        val dataPart:MutableMap<String,String> = mutableMapOf()
        dataPart[ "name"] = name
        dataPart[ "title"] = title
        dataPart[ "msg"] = msg
        dataPart[ "price"] = price

        // 다음으로 이미지파일을 MultipartBody.Part 로 포장하여 전송 : @Part
        val filePart: MultipartBody.Part? = imgPath?.let {
            val file = File(it)
            val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            MultipartBody.Part.createFormData("img1", file.name, requestBody)
        }

        // 네트워크 작업 시작
        retrofitService.postDataToServer(dataPart,filePart).enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val s = response.body()
                Toast.makeText(this@EditActivity, "$s", Toast.LENGTH_SHORT).show()
                finish() // 업로드가 완료되면 액티비티 종료
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@EditActivity, "나니?:${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

}
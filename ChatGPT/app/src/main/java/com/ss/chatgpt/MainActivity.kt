package com.ss.chatgpt

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ss.chatgpt.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private var mList: ArrayList<MessageModel>? = ArrayList()
    private var mAdapter: MessageAdapter? = null

    val JSON = "application/json; charset=utf-8".toMediaType()
    var client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mBinding?.apply {


            //setup recycler view
            mAdapter = MessageAdapter()
            recyclerView.adapter = mAdapter
            val layoutManager: LinearLayoutManager = LinearLayoutManager(this@MainActivity)
            layoutManager.stackFromEnd = true
            recyclerView.layoutManager = layoutManager


            //send  button action
            sendButtonIV.setOnClickListener {
                val question = editText.text.toString().trim()
                Toast.makeText(this@MainActivity, question, Toast.LENGTH_SHORT).show()
                addToChat(question, MessageModel.SENT_BY_USER)
                editText.setText("")
                callAPI(question)
                welcomeText.visibility = View.GONE


            }
        }

    }


    private fun addToChat(msg: String, sentBy: String) {
        Thread(Runnable {
            runOnUiThread {
                mList?.add(MessageModel(msg, sentBy))
                mList?.let { mAdapter?.message(it) }
                mAdapter?.notifyDataSetChanged()
                mAdapter?.let { mBinding?.recyclerView?.smoothScrollToPosition(it.itemCount) }
            }

        }).start()
    }


    private fun addResponse(response: String) {

        addToChat(response, MessageModel.SENT_BY_BOT)
    }

    private fun callAPI(question: String) {
        //create jason object


        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "text-davinci-003")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //okhttp
        val body: RequestBody = RequestBody.create(JSON,jsonBody.toString())
        val request: Request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer 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")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("failed to load due to " + e.localizedMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim { it <= ' ' })
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body.toString())
                }
            }

        })

    }

}
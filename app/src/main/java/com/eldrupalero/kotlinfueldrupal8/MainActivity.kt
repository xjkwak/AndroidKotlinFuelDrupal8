package com.eldrupalero.kotlinfueldrupal8

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

  private val EVADAS = "evadas"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    FuelManager.instance.basePath = "http://dev-evadas.pantheonsite.io"

    button_login.setOnClickListener {
      val username = edit_text_username.text.toString()
      val password = edit_text_password.text.toString()
      login(username, password)
    }
  }

  private fun login(username: String, password: String) {
    Fuel.post("/user/login?_format=json")
      .header(Headers.CONTENT_TYPE, "application/json")
      .body("{\"name\":\"${username}\", \"pass\":\"${password}\"}")
      .also { println(it) }
      .responseString() { request, response, result ->
        when (result) {
          is Result.Failure -> {
            Toast.makeText(this, "Incorrect Credentials!", Toast.LENGTH_SHORT)
          }
          is Result.Success -> {
            val data = result.get()
            val jsonObject = JSONObject(data)
            val logoutToken = jsonObject.getString("logout_token")
            val uid = jsonObject.getJSONObject("current_user").getInt("uid")
            val name = jsonObject.getJSONObject("current_user").getString("name")
            val collection = response.headers["Set-Cookie"]
            val cookie = collection.first()
            enter(logoutToken, uid, name, cookie)
          }
        }
      }
  }

  private fun enter(logoutToken: String, uid: Int, name: String, cookie: String) {
    saveData(logoutToken, uid, name, cookie)
    val intent = Intent(this, DashboardActivity::class.java)
    startActivity(intent)
    finish()
  }

  private fun saveData(logoutToken: String, uid: Int, name: String, cookie: String) {
    val sharedPref = this.getSharedPreferences(EVADAS, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putString("token", logoutToken)
        putInt("uid", uid)
        putString("name", name)
        putString("cookie", cookie)
        commit()
    }
  }
}

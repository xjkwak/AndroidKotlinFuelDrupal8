package com.eldrupalero.kotlinfueldrupal8

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

  private val EVADAS = "evadas"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_dashboard)

    val sharedPref = this.getSharedPreferences(EVADAS, Context.MODE_PRIVATE)
    val name = sharedPref.getString("name", "")
    text_view_welcome.text = "Welcome ${name}"

    FuelManager.instance.basePath = "http://dev-evadas.pantheonsite.io"

    button_logout.setOnClickListener{
      logout()
    }
  }


  private fun logout() {

    val sharedPref = this.getSharedPreferences(EVADAS, Context.MODE_PRIVATE)
    val token = sharedPref.getString("token", "")
    val cookie = sharedPref.getString("cookie", "")

    Fuel.post("/user/logout?_format=json&token=${token}")
      .header(Headers.COOKIE, cookie)
      .also { println(it) }
      .responseString() { request, response, result ->
        when (result) {
          is Result.Failure -> {
            val data = result.error.message
            Toast.makeText(this,data, Toast.LENGTH_SHORT).show()
          }
          is Result.Success -> {
            goMainActivity()
          }
        }
      }
  }

  private fun goMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }
}

package com.example.badstore
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.badstore.db.DatabaseHandler
import com.example.badstore.model.Auth
import com.example.badstore.model.Login
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var db:DatabaseHandler? = null
    private lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
        val token = db!!.retrieveJWT()
        if (token.isEmpty()) {
            apiService = APIConfig.getRetrofitClient(this).create(APIService::class.java)
            btnLogin.setOnClickListener {
                val authInfo = Login()
                authInfo.email = edtEmail.text.toString()
                authInfo.password = edtPassword.text.toString()
                apiService.login(authInfo).enqueue(object: retrofit2.Callback<Auth> {
                    override fun onFailure(call: Call<Auth>, t: Throwable) {
                        Log.d("Server Error", t.message)
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                        Log.v("[AUTH] Status code", response.code().toString())
                        if (response.code() == 200) {
                            val data = response.body()!!
                            if (remember_me_box.isChecked) {
                                db!!.setJWT(data.access_token)
                                Log.v("[AUTH]", "Access token saved in DB")
                                Toast.makeText(applicationContext, "Credentials saved!", Toast.LENGTH_LONG).show()
                            }
                            Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_LONG).show()
                            val intent= Intent(applicationContext, HomeActivity::class.java)
                            intent.putExtra("JWT", data.access_token)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            edtPassword.text = null
                            Toast.makeText(applicationContext, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        } else {
            Log.v("[AUTH]", "Using saved JWT token")
            Toast.makeText(applicationContext, "Using saved credentials", Toast.LENGTH_LONG).show()
            val intent= Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("JWT", token)
            startActivity(intent)
            finish()
        }

    }
}

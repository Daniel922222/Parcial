package com.example.parcial

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import java.security.Provider

enum class providerType{
    BASIC,GOOGLE
}
class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        //setup()
        val  bundle=intent.extras
        val email =bundle?.getString("email")
        val  provider=bundle?.getString("provider")
        setup()

         val prefs=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
       prefs.apply()
    }
    private fun  setup(){
        title="Inicio"
       var  btncerrar=findViewById<Button>(R.id.btncerrar)as Button
        btncerrar.setOnClickListener {
            val prefs=getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}
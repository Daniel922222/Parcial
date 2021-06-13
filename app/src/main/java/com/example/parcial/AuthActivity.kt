package com.example.parcial

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FilterQueryProvider
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    private val GOOGLE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        Setup()
        session()
    }

    override fun onStart() {
        var layout = findViewById<LinearLayout>(R.id.authLayout) as LinearLayout

        super.onStart()
        layout.visibility = View.VISIBLE

    }

    private fun session() {
        var layout = findViewById<LinearLayout>(R.id.authLayout) as LinearLayout
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if (email != null && provider != null) {
            layout.visibility = View.INVISIBLE
            sowHome(email, providerType.valueOf(provider))

        }

    }

    private fun Setup() {
        var btnregistrar = findViewById<Button>(R.id.btnregistrar) as Button
        var btngoogle = findViewById<Button>(R.id.btngoogle)
        var btnacceder = findViewById<Button>(R.id.btnacceder)
        var emailtext = findViewById<EditText>(R.id.emailtext)
        var passwordtext = findViewById<EditText>(R.id.passwordtext)
        title = "Autenticacion"
        btnregistrar.setOnClickListener {
            if (emailtext.text.isNotEmpty() && passwordtext.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailtext.text.toString(), passwordtext.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        sowHome(it.result?.user?.email ?: "", providerType.BASIC)

                    } else {
                        showAlert()
                    }

                }
            }
        }
        btnacceder.setOnClickListener {
            if (emailtext.text.isNotEmpty() && passwordtext.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailtext.text.toString(), passwordtext.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        sowHome(it.result?.user?.email ?: "", providerType.BASIC)

                    } else {
                        showAlert()
                    }

                }
            }
        }
        btngoogle.setOnClickListener {
            val googleconf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            val googleClient = GoogleSignIn.getClient(this, googleconf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error de autenticacion al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sowHome(email: String, provider: providerType) {
        val HomeIntent = Intent(this, InicioActivity::class.java).apply {

        }
        startActivity(HomeIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                val accout = task.getResult(ApiException::class.java)
                if (accout != null) {
                    val credential = GoogleAuthProvider.getCredential(accout.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            sowHome(accout.email ?: "", providerType.GOOGLE)

                        } else {
                            showAlert()
                        }

                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }
}


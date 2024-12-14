package pgl.proyectof

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.objetos.Personaje
import java.util.concurrent.Executors


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DatabaseProvider.getInstance(this)

        val botonEntrar = findViewById<Button>(R.id.buttEntrar)
        val botonRegistrarse = findViewById<Button>(R.id.buttCrear)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContra)
        val tvPj = findViewById<TextView>(R.id.tvDatosPJ)
        val botonJugar = findViewById<Button>(R.id.buttJugar)
        val imagenPJ = findViewById<ImageView>(R.id.imagenPJ)

        val executor = Executors.newSingleThreadExecutor()

        lateinit var pj: Personaje

        botonEntrar.setOnClickListener {
            executor.execute {
                val usuarioLogin = db.usuarioDao().getUsuarioByUsuario(etUsuario.text.toString())
                if (usuarioLogin != null) {
                    if (usuarioLogin.contrasena == etContrasena.text.toString()) {

                        val personaje = db.personajeDao().getPersonajesByUsuario(usuarioLogin.id)

                        runOnUiThread {
                            Toast.makeText(this, "Bienvenido ${usuarioLogin.usuario}", Toast.LENGTH_SHORT).show()
                            tvPj.setText(personaje.toString())
                            pj = personaje
                            mostrarPJ(imagenPJ, tvPj, botonJugar)
                        }
                    } else {
                        runOnUiThread {
                            limpiar(etUsuario, etContrasena)
                            Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        limpiar(etUsuario, etContrasena)
                        Toast.makeText(this, "Este usuario no existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        botonRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
        botonJugar.setOnClickListener {
            val intent = Intent(this, Inicio::class.java)
            intent.putExtra("personaje", pj)
            startActivity(intent)
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}

fun limpiar(user: EditText, pass: EditText) {
    user.setText("")
    pass.setText("")
}

fun mostrarPJ(icono: ImageView, datos: TextView, boton:Button) {
icono.visibility = View.VISIBLE
datos.visibility = View.VISIBLE
boton.visibility = View.VISIBLE
}
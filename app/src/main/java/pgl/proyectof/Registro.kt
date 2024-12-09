package pgl.proyectof

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.objetos.Personaje
import pgl.proyectof.objetos.Usuario
import java.util.concurrent.Executors

class Registro : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var contrasena: EditText
    private lateinit var contrasenaConfirm: EditText
    private lateinit var nombrePJ: EditText
    private lateinit var clase: Spinner
    private lateinit var atributos: EditText
    private lateinit var botonRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DatabaseProvider.getInstance(this)

        usuario = findViewById(R.id.etUsuarioReg)
        contrasena = findViewById(R.id.etContraReg)
        contrasenaConfirm = findViewById(R.id.etContraRepReg)
        nombrePJ = findViewById(R.id.etNombrePJ)

        clase = findViewById(R.id.spinnerClases)
        val clases = arrayOf("Seleccione una clase:", "Bardo", "Guerrero", "Mago")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, clases)
        clase.adapter = adaptador

        atributos = findViewById<EditText>(R.id.atributosPJ)
        botonRegistro = findViewById<Button>(R.id.buttRegistro)

        clase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (parentView.getItemAtPosition(position)) {
                    "Bardo" -> {
                        atributos.setText("Vida: 100\nAtaque: 10\nDefensa: 10\nMana: 100")
                        atributos.setSelection(0)
                    }

                    "Guerrero" -> {
                        atributos.setText("Vida: 150\nAtaque: 15\nDefensa: 15\nMana: 50")
                        atributos.setSelection(0)
                    }

                    "Mago" -> {
                        atributos.setText("Vida: 50\nAtaque: 5\nDefensa: 5\nMana: 150")
                        atributos.setSelection(0)
                    }

                    else -> atributos.setText("")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        botonRegistro.setOnClickListener {
            if (usuario.text.isNullOrBlank() || contrasena.text.isNullOrBlank() || contrasenaConfirm.text.isNullOrBlank() || nombrePJ.text.isNullOrBlank()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena.text.toString() != contrasenaConfirm.text.toString()) {
                Toast.makeText(this, "La Contraseña no coincide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuarioNuevo = Usuario(
                usuario = usuario.text.toString(),
                contrasena = contrasena.text.toString(),
                idPersonaje = null
            )

            val personajeNuevo = when (clase.selectedItem) {
                "Bardo" -> Personaje(
                    idUsuario = 0,
                    clase = "Bardo",
                    nivel = 1,
                    experiencia = 0,
                    nombre = nombrePJ.text.toString(),
                    vida = 100,
                    ataque = 10,
                    defensa = 10,
                    mana = 100,
                    idHabilidad = 1
                )

                "Guerrero" -> Personaje(
                    idUsuario = 0,
                    clase = "Guerrero",
                    nivel = 1,
                    experiencia = 0,
                    nombre = nombrePJ.text.toString(),
                    vida = 150,
                    ataque = 15,
                    defensa = 15,
                    mana = 50,
                    idHabilidad = 2
                )

                "Mago" -> Personaje(
                    idUsuario = 0,
                    clase = "Mago",
                    nivel = 1,
                    experiencia = 0,
                    nombre = nombrePJ.text.toString(),
                    vida = 50,
                    ataque = 5,
                    defensa = 5,
                    mana = 150,
                    idHabilidad = 3
                )

                else -> {
                    Toast.makeText(this, "Selecciona una clase", Toast.LENGTH_SHORT).show()
                    null
                }
            }

            if (personajeNuevo != null) {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    db.usuarioDao().insertUsuarioConPersonaje(usuarioNuevo, personajeNuevo)
                    runOnUiThread {
                        Toast.makeText(this, "Registro completado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Error al seleccionar clase", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
@file:Suppress("DEPRECATION")

package pgl.proyectof

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pgl.proyectof.baseDatos.AppDatabase
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.objetos.Habilidad
import pgl.proyectof.objetos.Monstruo
import pgl.proyectof.objetos.Personaje
import java.util.concurrent.Executors

class Inicio : AppCompatActivity() {
    lateinit private var personaje: Personaje
    lateinit private var botonEntrenar : Button
    lateinit private var botonInfo : Button
    lateinit private var botonAventura : Button

    private val db: AppDatabase by lazy { DatabaseProvider.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        personaje = intent.getSerializableExtra("personaje") as Personaje
        //Recordar bloquear el boton de ir atras nativo de android

        botonInfo = findViewById(R.id.buttInformacion)
        botonEntrenar = findViewById(R.id.buttEntrenar)
        botonAventura = findViewById(R.id.buttAventura)

        cargarHabilidades()
        cargarMonstruos()


        botonInfo.setOnClickListener {
            val intent = Intent(this, InformacionPJ::class.java)
            intent.putExtra("personaje", personaje)
            startActivity(intent)
        }

        botonEntrenar.setOnClickListener{
            val intent = Intent(this, Combate::class.java)
            intent.putExtra("personaje", personaje)
            startActivity(intent)
        }

        botonAventura.setOnClickListener {
            val intent = Intent(this, Aventura::class.java)
            intent.putExtra("personaje", personaje)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Toast.makeText(this,"Para retroceder, cierre sesión en \"Información\"", Toast.LENGTH_SHORT).show()
        // Deja este método vacío o realiza alguna acción personalizada si deseas
    }

    //Cargar habilidades en la base de datos
    fun cargarHabilidades(){
        val listaHabilidades = listOf(
            Habilidad(id = 1, nombre = "Canción curativa", descripcion = "Recupera un porcentaje de la vida del usuario.", poder = 20, coste = 20),
            Habilidad(id = 2, nombre = "Embate Brutal", descripcion = "Ataque pesado que reduce la defensa del enemigo.", poder = 25, coste = 15),
            Habilidad(id = 3, nombre = "Bola de Fuego", descripcion = "Inflige daño mágico.", poder = 20, coste = 15),
            Habilidad(id = 4, nombre = "Golpe Rápido", descripcion = "Un ataque veloz que ignora la defensa enemiga.", poder = 15, coste = 10),
            Habilidad(id = 5, nombre = "Aullido Intimidante", descripcion = "Salta el turno del enemigo", poder = 0, coste = 20),
            Habilidad(id = 6, nombre = "Llama Oscura", descripcion = "Inflige daño mágico con un alto poder destructivo.", poder = 30, coste = 25)
        )
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            //comprobar si la tabla habilidades esta vacia
            if (db.habilidadDao().countHabilidades()==0){
                for (habilidad in listaHabilidades){
                    db.habilidadDao().insert(habilidad)
                }
            }
        }
    }

    fun cargarMonstruos(){
        val listaMonstruos = listOf(
            Monstruo(id = 1, nombre = "Goblin", vida = 60, ataque = 8, defensa = 6, idHabilidad = 4),
            Monstruo(id = 2, nombre = "Orco", vida = 120, ataque = 12, defensa = 10, idHabilidad = 2),
            Monstruo(id = 3, nombre = "Troll", vida = 150, ataque = 15, defensa = 12, idHabilidad = 2),
            Monstruo(id = 4, nombre = "Lobo", vida = 80, ataque = 10, defensa = 5, idHabilidad = 5),
            Monstruo(id = 5, nombre = "Mini-Liche", vida = 70, ataque = 9, defensa = 7, idHabilidad = 6)
        )
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            //comprobar si la tabla habilidades esta vacia
            if (db.monstruoDao().countMonstruos()==0){
                for (monstruo in listaMonstruos){
                    db.monstruoDao().insert(monstruo)
                }
            }
        }
    }
}
package pgl.proyectof

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pgl.proyectof.baseDatos.DatabaseProvider
import pgl.proyectof.fragmentos.adaptadorInformacion
import pgl.proyectof.objetos.Personaje
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class InformacionPJ : AppCompatActivity() {
    private val db = DatabaseProvider.getInstance(this)
    lateinit private var personaje: Personaje
    lateinit private var tabLayout: TabLayout
    lateinit private var viewPager : ViewPager2
    lateinit private var nombre: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.activity_informacion_pj)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        personaje = intent.getSerializableExtra("personaje") as Personaje
        nombre = findViewById(R.id.tvNombrePrincipal)
        nombre.text = personaje.nombre

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = adaptadorInformacion(this, personaje.id)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Personaje"
                1 -> tab.text = "Usuario"
            }
        }.attach()


    }

}
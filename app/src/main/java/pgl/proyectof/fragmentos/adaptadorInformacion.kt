package pgl.proyectof.fragmentos

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class adaptadorInformacion(fragmentActivity: FragmentActivity, private val id: Long) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DatosPersonaje.newInstance(id)
            1 -> DatosUsuario.newInstance(id)
            else -> {
                throw IllegalArgumentException("No se puede crear el fragmento")
            }
        }
    }
}
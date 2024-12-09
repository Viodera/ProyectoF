package pgl.proyectof.baseDatos

import DAO.HabilidadDao
import DAO.MonstruoDao
import DAO.PersonajeDao
import DAO.UsuarioDao
import androidx.room.Database
import androidx.room.RoomDatabase
import pgl.proyectof.objetos.Habilidad
import pgl.proyectof.objetos.Monstruo
import pgl.proyectof.objetos.Personaje
import pgl.proyectof.objetos.Usuario


@Database(entities = [Usuario::class, Personaje::class, Habilidad::class, Monstruo::class], version = 1)
//@Database(entities = [Usuario::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun personajeDao(): PersonajeDao
    abstract fun habilidadDao(): HabilidadDao
    abstract fun monstruoDao(): MonstruoDao
}
package DAO



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import pgl.proyectof.objetos.Personaje
import pgl.proyectof.objetos.Usuario

@Dao
interface UsuarioDao {
    @Insert
    fun insert(usuario: Usuario): Long

    @Update
    fun updateUsuario(usuario: Usuario)

    @Insert
    fun insertPersonaje(personaje: Personaje): Long

    @Query("SELECT * FROM USUARIOS")
    fun getAllUsuarios(): List<Usuario>

    @Query("SELECT * FROM USUARIOS WHERE usuario = :usuario")
    fun getUsuarioByUsuario(usuario: String): Usuario?

    @Query("SELECT * FROM USUARIOS WHERE id = :id")
    fun getUsuarioById(id: Long?): Usuario?

    @Query("SELECT * FROM USUARIOS WHERE idPersonaje = :idPersonaje")
    fun getUsuarioByPersonaje(idPersonaje: Long): Usuario?

    @Transaction
    fun insertUsuarioConPersonaje(usuario: Usuario, personaje: Personaje) {
        val usuarioId = insert(usuario)
        val personajeId = insertPersonaje(personaje.copy(idUsuario = usuarioId))

        updateUsuario(usuario.copy(id = usuarioId, idPersonaje = personajeId))
    }
}


package DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pgl.proyectof.objetos.Personaje

@Dao
interface PersonajeDao {
    @Insert
     fun insert(personaje: Personaje)

    @Query("SELECT * FROM PERSONAJES WHERE idUsuario = :idUsuario")
     fun getPersonajesByUsuario(idUsuario: Long): Personaje

    @Query("SELECT * FROM PERSONAJES WHERE id = :id")
     fun getPersonajeById(id: Long): Personaje

    @Query("DELETE FROM PERSONAJES WHERE id = :id")
     fun deletePersonajeById(id: Long)

}
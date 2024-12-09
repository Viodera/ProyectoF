package DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pgl.proyectof.objetos.Habilidad

@Dao
interface HabilidadDao {
    @Insert
     fun insert(habilidad: Habilidad)

    @Query("SELECT * FROM HABILIDADES")
     fun getAllHabilidades(): List<Habilidad>

    @Query("SELECT * FROM HABILIDADES WHERE id = :id")
     fun getHabilidadById(id: Long): Habilidad

    @Query("DELETE FROM HABILIDADES WHERE id = :id")
     fun deleteHabilidadById(id: Long)

    @Query("SELECT COUNT(*) FROM Habilidades")
    fun countHabilidades(): Int

}
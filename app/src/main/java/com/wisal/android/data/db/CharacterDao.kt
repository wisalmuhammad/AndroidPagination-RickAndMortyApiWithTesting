package com.wisal.android.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wisal.android.models.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAllCharacters():PagingSource<Int,Character>

    @Query("SELECT * FROM characters WHERE id =:characterID")
    fun getCharacter(characterID: String): Flow<Character>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Character>)

    @Query("DELETE FROM characters")
    suspend fun clearAllCharacters()

}
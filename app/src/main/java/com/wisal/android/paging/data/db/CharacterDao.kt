package com.wisal.android.paging.data.db

import android.database.Cursor
import androidx.paging.PagingSource
import androidx.room.*
import com.wisal.android.paging.models.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters_items ORDER BY id ASC")
    fun getAllCharacters():PagingSource<Int,Character>

    @Query("SELECT * FROM characters_items ORDER BY id ASC")
    fun getAllCharactersCursor(): Cursor?

    @Query("SELECT * FROM characters_items WHERE id =:characterID")
    fun getCharacter(characterID: String): Flow<Character>

    @Query("SELECT COUNT(id) FROM characters_items")
    fun getAllCharactersCount(): Cursor?

    @Query("SELECT * FROM characters_items WHERE id =:id")
    fun getCharacterByIdCursor(id: String): Cursor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Character>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(character: Character): Long

    @Query("DELETE FROM characters_items")
    suspend fun clearAllCharacters()

    @Query("DELETE FROM characters_items WHERE id =:id")
    fun deleteById(id: String): Int

    @Update
    fun updateCharacter(character: Character): Int

}
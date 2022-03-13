package com.wisal.android.paging.data.db

import androidx.room.*
import com.wisal.android.paging.models.CharacterPageKeys

@Dao
interface CharacterPageKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characterPageKey: List<CharacterPageKeys>)

    @Query("SELECT * FROM characters_keys WHERE id=:charId")
    suspend fun remoteKeysCharacterId(charId: Int): CharacterPageKeys?

    @Query("DELETE FROM characters_keys")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM characters_keys WHERE id =:id")
    fun deleteById(id: String): Int

}
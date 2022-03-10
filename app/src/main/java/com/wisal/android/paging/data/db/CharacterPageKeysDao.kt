package com.wisal.android.paging.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wisal.android.paging.models.CharacterPageKeys

@Dao
interface CharacterPageKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characterPageKey: List<CharacterPageKeys>)

    @Query("SELECT * FROM characters_keys WHERE id=:charId")
    suspend fun remoteKeysCharacterId(charId: Int): CharacterPageKeys?

    @Query("DELETE FROM characters_keys")
    suspend fun clearRemoteKeys()

}
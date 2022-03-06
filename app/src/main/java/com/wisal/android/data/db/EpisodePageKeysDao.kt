package com.wisal.android.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wisal.android.models.EpisodePageKeys


@Dao
interface EpisodePageKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPagesKeys(pageKeys: List<EpisodePageKeys>)


    @Query("SELECT * FROM episodes_keys WHERE id LIKE :id")
    fun getPageKeyById(id: Int?): EpisodePageKeys


    @Query("DELETE FROM episodes_keys")
    suspend fun deleteAll()

}
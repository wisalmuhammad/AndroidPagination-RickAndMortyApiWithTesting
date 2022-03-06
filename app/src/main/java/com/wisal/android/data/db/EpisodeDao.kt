package com.wisal.android.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wisal.android.models.Episode

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(episodes: List<Episode>)

    @Query("SELECT * FROM episodes")
    fun pagingSource(): PagingSource<Int,Episode>

    @Query("DELETE FROM episodes")
    suspend fun deleteAll()

}
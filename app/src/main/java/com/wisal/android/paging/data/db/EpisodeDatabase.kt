package com.wisal.android.paging.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wisal.android.paging.models.Episode
import com.wisal.android.paging.models.EpisodePageKeys


@Database(entities = [Episode::class,EpisodePageKeys::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class EpisodeDatabase: RoomDatabase() {

    abstract fun episodesDao(): EpisodeDao

    abstract fun pageKeysDao(): EpisodePageKeysDao

    companion object {

        @Volatile private var instance: EpisodeDatabase? = null

        fun getDatabase(context: Context): EpisodeDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).
                also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext,
                EpisodeDatabase::class.java,
                "Rick_and_Morty_Api_Episodes")
                .fallbackToDestructiveMigration()
                .build()
    }

}
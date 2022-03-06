package com.wisal.android.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wisal.android.models.Character
import com.wisal.android.models.CharacterPageKeys


@Database(entities = [Character::class,CharacterPageKeys::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class CharacterDatabase: RoomDatabase() {

    abstract fun charactersDao(): CharacterDao

    abstract fun remoteKeysDao(): CharacterPageKeysDao


    companion object {
        @Volatile private var instance: CharacterDatabase? = null

        fun getDatabase(context: Context): CharacterDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).
                also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext,
                CharacterDatabase::class.java,
                "Rick_and_Morty_Api_Characters")
                .fallbackToDestructiveMigration()
                .build()
    }

}
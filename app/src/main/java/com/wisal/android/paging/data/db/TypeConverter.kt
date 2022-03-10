package com.wisal.android.paging.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wisal.android.paging.models.Gender
import com.wisal.android.paging.models.NameUrl
import com.wisal.android.paging.models.Status

class TypeConverter {

    @TypeConverter
    fun nameUrlToString(name: NameUrl): String {
        return Gson().toJson(name).toString()
    }

    @TypeConverter
    fun stringToNameUrl(data: String): NameUrl {
        return Gson().fromJson(data,NameUrl::class.java)
    }

    @TypeConverter
    fun genderToString(gender: Gender): String {
        return Gson().toJson(gender).toString()
    }

    @TypeConverter
    fun stringToGender(data: String): Gender {
        return Gson().fromJson(data,Gender::class.java)
    }

    @TypeConverter
    fun statusToString(status: Status): String {
        return Gson().toJson(status).toString()
    }

    @TypeConverter
    fun stringToStatus(data: String): Status {
        return Gson().fromJson(data,Status::class.java)
    }

    @TypeConverter
    fun listToJsonString(episodes: List<String>): String {
        return Gson().toJson(episodes).toString()
    }

    @TypeConverter
    fun jsonToStringList(data: String): List<String> {
        return Gson().fromJson(data, object : TypeToken<List<String>>(){}.type)
    }
}
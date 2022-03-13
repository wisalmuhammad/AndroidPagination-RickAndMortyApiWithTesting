package com.wisal.android.paging.models

import android.content.ContentValues
import android.os.Parcelable
import android.provider.BaseColumns
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize


data class PagedResponse<T>(
    @SerializedName("info") val pageInfo: PageInfo,
    val results: List<T> = emptyList()
)

data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

@Entity(tableName = "characters_items")
data class Character(
    @PrimaryKey @field:SerializedName("id") val id: Int,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("status") val status: Status,
    @field:SerializedName("species") val species: String,
    @field:SerializedName("type") val type: String,
    @field:SerializedName("gender") val gender: Gender,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("url") val url: String,
    @field:SerializedName("origin") val origin: NameUrl,
    @field:SerializedName("location") val location: NameUrl,
    @field:SerializedName("episode") val episode: List<String>,
    @field:SerializedName("created") val created: String
)

@Entity(tableName = "characters_keys")
data class CharacterPageKeys(
    @PrimaryKey val id: Int,
    val nextPageUrl: String?
)


enum class Gender(private val gender: String) {
    @SerializedName(value = "Female", alternate = ["female"])
    FEMALE("Female"),

    @SerializedName(value = "Male", alternate = ["male"])
    MALE("Male"),

    @SerializedName(value = "Genderless", alternate = ["genderless"])
    GENDERLESS("Genderless"),

    @SerializedName(value = "unknown", alternate = ["Unknown"])
    UNKNOWN("Unknown");

    override fun toString() = gender

}

enum class Status(private val status: String) {
    @SerializedName(value = "Alive", alternate = ["alive"])
    ALIVE("Alive"),

    @SerializedName(value = "Dead", alternate = ["dead"])
    DEAD("Dead"),

    @SerializedName(value = "unknown", alternate = ["Unknown"])
    UNKNOWN("Unknown");

    override fun toString() = status
}

@Parcelize
data class NameUrl(
    val name: String,
    val url: String
): Parcelable
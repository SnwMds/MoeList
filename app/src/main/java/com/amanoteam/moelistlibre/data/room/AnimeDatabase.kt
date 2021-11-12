package com.amanoteam.moelistlibre.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amanoteam.moelistlibre.data.model.User
import com.amanoteam.moelistlibre.data.model.anime.AnimeDetails
import com.amanoteam.moelistlibre.data.model.anime.UserAnimeList
import com.amanoteam.moelistlibre.data.model.manga.MangaDetails
import com.amanoteam.moelistlibre.data.model.manga.UserMangaList

@TypeConverters(value = [com.amanoteam.moelistlibre.data.room.TypeConverters::class])
@Database(entities = [AnimeDetails::class, MangaDetails::class,
    UserAnimeList::class, UserMangaList::class, User::class],
    version = 49)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun mangaDetailsDao(): MangaDetailsDao
    //abstract fun userAnimeListDao(): UserAnimeListDao
    //abstract fun userMangaListDao(): UserMangaListDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AnimeDatabase? = null

        fun getAnimeDatabase(context: Context): AnimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "animeDatabase"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
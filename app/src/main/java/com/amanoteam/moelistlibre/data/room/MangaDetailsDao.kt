package com.amanoteam.moelistlibre.data.room

import androidx.room.*
import com.amanoteam.moelistlibre.data.model.manga.MangaDetails

@Dao
interface MangaDetailsDao {

    @Query("SELECT * FROM mangadetails WHERE id LIKE :mangaId ")
    fun getMangaDetailsById(mangaId: Int): MangaDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMangaDetails(manga_details: MangaDetails)

    @Delete
    fun deleteMangaDetails(manga_details: MangaDetails)

    @Delete
    fun deleteAllMangaDetails(manga_details: MutableList<MangaDetails>)
}
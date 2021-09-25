package xyz.flussikatz.searchmovie.data

import android.content.ContentValues
import android.database.Cursor
import xyz.flussikatz.searchmovie.data.db.DatabaseHelper
import xyz.flussikatz.searchmovie.data.entity.Film


class MainRepository(databaseHelper: DatabaseHelper) {
    private val sqlDb = databaseHelper.readableDatabase
    private lateinit var cursor: Cursor

    fun putToDB(film: Film) {
        val cv = ContentValues()
        cv.apply {
            put(DatabaseHelper.COLUMN_ID, film.id)
            put(DatabaseHelper.COLUMN_TITLE, film.title)
            put(DatabaseHelper.COLUMN_POSTER, film.posterId)
            put(DatabaseHelper.COLUMN_DESCRIPTION, film.description)
            put(DatabaseHelper.COLUMN_RATING, film.rating)
        }
        sqlDb.insert(DatabaseHelper.TABLE_NAME, null, cv)
    }

    fun getAllFromDb(): List<Film> {
        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
        val result = mutableListOf<Film>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title =  cursor.getString(1)
                val posterId = cursor.getString(2)
                val description = cursor.getString(3)
                val rating = cursor.getInt(4)

                result.add(
                    Film(
                    id = id,
                    title = title,
                    posterId = posterId,
                    description = description,
                    rating = rating,
                )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    fun clearDB(): Boolean {
        var result = false
        sqlDb.delete(DatabaseHelper.TABLE_NAME,null,null)
        cursor = sqlDb.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME}", null)
        if (cursor.count == 0) {
            result = true
        }
        cursor.close()
        return result
    }
}
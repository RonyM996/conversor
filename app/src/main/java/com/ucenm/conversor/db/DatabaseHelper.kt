package com.ucenm.conversor.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ucenm.conversor.data.Conversion
import com.ucenm.conversor.data.Rate

// Cambiamos la versión a 2 para que se ejecute onUpgrade y se actualicen las tablas
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "CurrencyDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        // Agregada la columna is_favorite a la tabla rates
        val createRatesTable = """
            CREATE TABLE rates (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                from_code TEXT,
                to_code TEXT,
                rate REAL,
                is_custom INTEGER DEFAULT 0,
                is_favorite INTEGER DEFAULT 0 
            )
        """.trimIndent()

        val createConversionsTable = """
            CREATE TABLE conversions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                from_code TEXT,
                to_code TEXT,
                amount REAL,
                result REAL,
                date TEXT,
                is_favorite INTEGER DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createRatesTable)
        db.execSQL(createConversionsTable)

        // Datos semilla
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('HNL', 'USD', 0.040)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('USD', 'HNL', 26.4849)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('EUR', 'USD', 1.18)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('GTQ', 'USD', 0.130)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('NIO', 'USD', 0.027)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('CRC', 'USD', 0.002)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('SVC', 'USD', 1)")
        db.execSQL("INSERT INTO rates (from_code, to_code, rate) VALUES ('PAB', 'USD', 1)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS rates")
        db.execSQL("DROP TABLE IF EXISTS conversions")
        onCreate(db)
    }

    fun getRate(from: String, to: String): Double {
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT rate FROM rates WHERE from_code=? AND to_code=?", arrayOf(from, to))
        var rate = 0.0
        if (cursor.moveToFirst()) {
            rate = cursor.getDouble(0)
        }
        cursor.close()
        return rate
    }

    fun addConversion(conversion: Conversion): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("from_code", conversion.fromCode)
            put("to_code", conversion.toCode)
            put("amount", conversion.amount)
            put("result", conversion.result)
            put("date", conversion.date)
            put("is_favorite", if (conversion.isFavorite) 1 else 0)
        }
        return db.insert("conversions", null, values)
    }

    fun addCustomRate(rate: Rate): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("from_code", rate.fromCode)
            put("to_code", rate.toCode)
            put("rate", rate.rate)
            put("is_custom", 1)
        }
        return db.insert("rates", null, values)
    }

    fun getAllHistory(): List<Conversion> {
        val list = ArrayList<Conversion>()
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM conversions ORDER BY is_favorite DESC, id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Conversion(
                        id = cursor.getInt(0),
                        fromCode = cursor.getString(1),
                        toCode = cursor.getString(2),
                        amount = cursor.getDouble(3),
                        result = cursor.getDouble(4),
                        date = cursor.getString(5),
                        isFavorite = cursor.getInt(6) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun toggleFavorite(id: Int, isFav: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("is_favorite", if (isFav) 1 else 0)
        }
        db.update("conversions", values, "id=?", arrayOf(id.toString()))
    }

    fun getAvailableCurrencies(): List<String> {
        val list = ArrayList<String>()
        val db = this.readableDatabase
        val query = """
            SELECT DISTINCT from_code FROM rates
            UNION
            SELECT DISTINCT to_code FROM rates
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }


    // --- MÉTODOS PARA ACCESO RÁPIDO A FAVORITOS ---

    fun getFavoriteConversions(): List<Conversion> {
        val list = ArrayList<Conversion>()
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM conversions WHERE is_favorite = 1 ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Conversion(
                        id = cursor.getInt(0),
                        fromCode = cursor.getString(1),
                        toCode = cursor.getString(2),
                        amount = cursor.getDouble(3),
                        result = cursor.getDouble(4),
                        date = cursor.getString(5),
                        isFavorite = true
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
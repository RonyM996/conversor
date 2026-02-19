package com.ucenm.conversor

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ucenm.conversor.databinding.ActivityHistorialBinding
import com.ucenm.conversor.db.DatabaseHelper

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)


        val listaConversiones = dbHelper.getAllHistory()

        val listaTextos = listaConversiones.map { conversion ->
            "${conversion.date} | ${conversion.amount} ${conversion.fromCode} ➔ ${String.format("%.2f", conversion.result)} ${conversion.toCode}"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTextos)
        binding.listViewHistorial.adapter = adapter

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}
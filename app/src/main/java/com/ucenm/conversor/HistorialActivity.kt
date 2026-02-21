package com.ucenm.conversor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ucenm.conversor.databinding.ActivityHistorialBinding
import com.ucenm.conversor.db.DatabaseHelper
import com.ucenm.conversor.data.Conversion

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var historialAdapter: HistorialAdapter // Agregado: Referencia al adaptador
    private var mostrandoSoloFavoritos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Configuración necesaria para RecyclerView
        setupRecyclerView()

        // --- LÓGICA DEL RETO ADICIONAL: ACCESO RÁPIDO ---
        binding.btnFiltrarFavoritos.setOnClickListener {
            mostrandoSoloFavoritos = !mostrandoSoloFavoritos

            // Cambiar el texto del botón según el estado
            binding.btnFiltrarFavoritos.text = if (mostrandoSoloFavoritos) "Ver Todo" else "Ver Favoritos"

            actualizarLista()
        }

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // Obtenemos los datos iniciales de la base de datos
        val listaConversiones = dbHelper.getAllHistory()

        // Inicializamos el adaptador personalizado con la lista
        historialAdapter = HistorialAdapter(listaConversiones)

        // Configuramos el layoutManager (obligatorio para RecyclerView)
        binding.listViewHistorial.layoutManager = LinearLayoutManager(this)

        // Asignamos el adaptador al RecyclerView
        binding.listViewHistorial.adapter = historialAdapter
    }

    private fun actualizarLista() {
        // Obtenemos los datos dependiendo del filtro activo usando los nuevos métodos
        val listaFiltrada = if (mostrandoSoloFavoritos) {
            dbHelper.getFavoriteConversions()
        } else {
            dbHelper.getAllHistory()
        }

        // Usamos la función updateData que creamos en el adaptador para refrescar la vista
        historialAdapter.updateData(listaFiltrada)
    }
}
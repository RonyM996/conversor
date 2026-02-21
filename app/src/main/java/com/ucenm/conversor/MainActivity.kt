package com.ucenm.conversor

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ucenm.conversor.R
import com.ucenm.conversor.data.Conversion
import com.ucenm.conversor.databinding.ActivityMainBinding
import com.ucenm.conversor.db.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inicializamos el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos la base de datos
        dbHelper = DatabaseHelper(this)


        actualizarSpinner()

        binding.btnConvertir.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            val fromCurrency = binding.spinnerFrom.selectedItem.toString()
            val toCurrency = "USD"

            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDouble()
                val rate = dbHelper.getRate(fromCurrency, toCurrency)

                if (rate > 0) {
                    val result = amount * rate

                    val conversion = Conversion(
                        fromCode = fromCurrency,
                        toCode = toCurrency,
                        amount = amount,
                        result = result,
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )
                    dbHelper.addConversion(conversion)

                    // Ir a pantalla Resultado
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("RESULT", result)
                    intent.putExtra("RATE", rate)
                    intent.putExtra("FROM", fromCurrency)
                    intent.putExtra("TO", toCurrency)
                    intent.putExtra("AMOUNT", amount)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Tasa de cambio no encontrada", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa un monto", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnVerHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }


        binding.btnAgregarMoneda.setOnClickListener { mostrarDialogoAgregarMoneda() }
    }



    private fun actualizarSpinner() {
        // Obtenemos la lista dinámica
        val monedasDisponibles = dbHelper.getAvailableCurrencies()

        // Usamos android.R.layout explícitamente
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, monedasDisponibles)
        binding.spinnerFrom.adapter = adapter
    }


    private fun mostrarDialogoAgregarMoneda() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_agregar_moneda, null)
        val etCodigo = dialogView.findViewById<android.widget.EditText>(R.id.etCodigoMoneda)
        val etTasa = dialogView.findViewById<android.widget.EditText>(R.id.etTasaCambio)

        android.app.AlertDialog.Builder(this)
            .setTitle("Agregar Moneda Personalizada")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val codigo = etCodigo.text.toString().uppercase()
                val tasaStr = etTasa.text.toString()

                if (codigo.isNotEmpty() && tasaStr.isNotEmpty()) {
                    val tasa = tasaStr.toDouble()

                    val nuevaTasa = com.ucenm.conversor.data.Rate(
                        fromCode = codigo,
                        toCode = "USD",
                        rate = tasa
                    )

                    val idGuardado = dbHelper.addCustomRate(nuevaTasa)

                    if (idGuardado != -1L) {
                        Toast.makeText(this, "¡Moneda agregada con éxito!", Toast.LENGTH_SHORT).show()
                        // ¡Actualizamos el Spinner inmediatamente!
                        actualizarSpinner()
                    } else {
                        Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, llena el código y la tasa", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
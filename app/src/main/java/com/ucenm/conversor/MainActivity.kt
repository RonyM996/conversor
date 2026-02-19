package com.ucenm.conversor

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ucenm.conversor.data.Conversion
import com.ucenm.conversor.databinding.ActivityMainBinding
import com.ucenm.conversor.db.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // 1. Declaramos las variables a nivel de clase
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inicializamos el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializamos la base de datos
        dbHelper = DatabaseHelper(this)

        val monedasOrigen = arrayOf("HNL", "USD", "EUR", "GTQ", "NIO", "CRC", "SVC", "PAV",)

        // 2. Creas el adaptador que le da el formato visual a esa lista
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, monedasOrigen)

        // 3. Le asignas el adaptador a tu Spinner
        binding.spinnerFrom.adapter = adapter

        binding.btnConvertir.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            val fromCurrency = binding.spinnerFrom.selectedItem.toString() // Ej: "HNL"
            val toCurrency = "USD"

            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDouble()
                val rate = dbHelper.getRate(fromCurrency, toCurrency)

                if (rate > 0) {
                    val result = amount * rate

                    // Guardar en historial
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
                // Agregué esta validación para evitar errores si el usuario presiona el botón sin escribir nada
                Toast.makeText(this, "Por favor, ingresa un monto", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnVerHistorial.setOnClickListener {
            // Reemplaza "HistorialActivity" con el nombre real de tu pantalla de historial
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    // 4. (Opcional pero recomendado) Cerrar la base de datos cuando la app se destruye
    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
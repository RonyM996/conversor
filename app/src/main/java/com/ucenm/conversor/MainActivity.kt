package com.ucenm.conversor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ucenm.conversor.data.Conversion
import com.ucenm.conversor.databinding.ActivityMainBinding
import com.ucenm.conversor.db.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.jvm.java


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?)
    {
        lateinit var dbHelper: DatabaseHelper
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            }
        }

    }


}
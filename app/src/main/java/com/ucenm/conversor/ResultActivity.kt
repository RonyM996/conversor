package com.ucenm.conversor

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ucenm.conversor.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar el binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Recuperar los datos que se enviaron desde el MainActivity
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)
        val result = intent.getDoubleExtra("RESULT", 0.0)
        val rate = intent.getDoubleExtra("RATE", 0.0)
        val from = intent.getStringExtra("FROM") ?: ""

        // 3. MEJORA: Formatear los números para mostrar solo 2 decimales (y 4 para la tasa)
        val formattedAmount = String.format("%.2f", amount)
        val formattedResult = String.format("%.2f", result)
        val formattedRate = String.format("%.4f", rate)

        // 4. Mostrar el texto formateado en el TextView
        binding.txtResultado.text = """
            Monto: $formattedAmount $from
            Tasa aplicada: $formattedRate
            -----------------------
            Total: $formattedResult USD
        """.trimIndent()

        // 5. Configurar botón para volver
        binding.btnVolver.setOnClickListener {
            // finish() destruye esta actividad y te devuelve a la anterior
            finish()
        }
    }
}
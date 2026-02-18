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

        // Inicializar el binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Recuperar los datos que se enviaron desde la otra pantalla
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)
        val result = intent.getDoubleExtra("RESULT", 0.0)
        val rate = intent.getDoubleExtra("RATE", 0.0)
        val from = intent.getStringExtra("FROM") ?: ""

        // 4. Mostrar el texto en el TextView (Asegúrate de tener el ID correcto en el XML)
        binding.txtResultado.text = """
            Monto: $amount $from
            Tasa aplicada: $rate
            -----------------------
            Total: $result USD
        """.trimIndent()

        // 5. Configurar botón para volver (Opcional)
        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}
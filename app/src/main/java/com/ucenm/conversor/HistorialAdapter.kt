package com.ucenm.conversor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ucenm.conversor.data.Conversion
import com.ucenm.conversor.db.DatabaseHelper

class HistorialAdapter(private var lista: List<Conversion>) :
    RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    // ESTA CLASE ES NECESARIA - No la borres
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        val ivFavorite: ImageView = view.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversion = lista[position]

        holder.tvInfo.text = "${conversion.amount} ${conversion.fromCode} -> ${conversion.result} ${conversion.toCode}"

        // Configurar icono de estrella
        val icon = if (conversion.isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        holder.ivFavorite.setImageResource(icon)

        holder.ivFavorite.setOnClickListener {
            val db = DatabaseHelper(holder.itemView.context)

            // IMPORTANTE: Solo cambiamos el estado visual y en DB
            val nuevoEstado = !conversion.isFavorite
            db.toggleFavorite(conversion.id, nuevoEstado)

            // Actualizamos el objeto en la lista
            conversion.isFavorite = nuevoEstado

            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    // Esta función permite el "Acceso rápido" del reto adicional
    fun updateData(newList: List<Conversion>) {
        this.lista = newList
        notifyDataSetChanged()
    }
}
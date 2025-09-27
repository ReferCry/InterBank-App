package com.interbank.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.interbank.app.R
import com.interbank.app.model.Movimiento

class MovAdapter(
    private val data: MutableList<Movimiento>
) : RecyclerView.Adapter<MovAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val desc: TextView = v.findViewById(R.id.tvDesc)
        val monto: TextView = v.findViewById(R.id.tvMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)
        return VH(v)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val it = data[pos]
        h.desc.text = it.descripcion
        val abs = kotlin.math.abs(it.monto)
        val texto = "S/ %.2f".format(abs)
        h.monto.text = if (it.monto >= 0) texto else "- $texto"
    }

    // Accesos útiles para guardar/leer
    fun items(): List<Movimiento> = data
    fun setItems(newItems: List<Movimiento>) {
        data.clear()
        data.addAll(newItems)
        notifyDataSetChanged()
    }
}

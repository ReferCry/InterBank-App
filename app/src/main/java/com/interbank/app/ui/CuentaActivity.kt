package com.interbank.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.interbank.app.databinding.ActivityCuentaBinding
import com.interbank.app.model.Movimiento
import com.interbank.app.ui.adapter.MovAdapter
import org.json.JSONArray
import org.json.JSONObject
import com.interbank.app.LoginActivity

class CuentaActivity : AppCompatActivity() {

    private lateinit var b: ActivityCuentaBinding
    private lateinit var adapter: MovAdapter
    private val archivo = "movimientos.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Toolbar abre el Drawer
        setSupportActionBar(b.toolbar)
        b.toolbar.setNavigationOnClickListener { b.drawer.open() }

        // Saldo: viene por Intent (desde ProductosActivity). Si no, default 10000.0
        val saldo = intent.getDoubleExtra("saldo", 10000.0)
        b.tvSaldo.text = "S/ %.2f".format(saldo)

        // RecyclerView
        adapter = MovAdapter(mutableListOf())
        b.rvMovs.layoutManager = LinearLayoutManager(this)
        b.rvMovs.adapter = adapter

        // Cargar de archivo o generar aleatorios
        val cargados = loadMovs()
        if (cargados != null && cargados.isNotEmpty()) {
            adapter.setItems(cargados)
        } else {
            adapter.setItems(genMovsAleatorios())
        }

        // Drawer: opciones
        b.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.interbank.app.R.id.nav_productos -> {
                    startActivity(Intent(this, ProductosActivity::class.java))
                    true
                }
                com.interbank.app.R.id.nav_salir -> {
                    val i = Intent(this, LoginActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    finish()
                    true
                }
                else -> false
            }.also { b.drawer.close() }
        }

        // BottomNavigation (decorativo salvo "Inicio")
        b.bottomNav.setOnItemSelectedListener { mi ->
            if (mi.itemId == com.interbank.app.R.id.btn_inicio) {
                startActivity(Intent(this, ProductosActivity::class.java))
                true
            } else {
                // Solo marcamos como seleccionado
                true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Guardar movimientos al salir/parar
        saveMovs(adapter.items())
    }

    // ======= Datos =======

    private fun genMovsAleatorios(): List<Movimiento> {
        val base = listOf(
            "Transferencia web/app", "itf",
            "transferencia interbancaria", "depósito cajero", "plin"
        )
        return (1..10).map {
            val d = base.random()
            // Monto aleatorio: algunos negativos (salidas), otros positivos (entradas)
            val signo = listOf(-1, 1).random()
            val monto = (listOf(0.10, 5.0, 18.0, 60.0, 150.0, 400.0, 2900.0).random()) * signo
            Movimiento(d, monto)
        }
    }

    private fun saveMovs(movs: List<Movimiento>) {
        val arr = JSONArray()
        movs.forEach {
            val o = JSONObject().apply {
                put("d", it.descripcion)
                put("m", it.monto)
            }
            arr.put(o)
        }
        val json = arr.toString()
        openFileOutput(archivo, Context.MODE_PRIVATE).use { it.write(json.toByteArray()) }
    }

    private fun loadMovs(): List<Movimiento>? {
        return try {
            val json = openFileInput(archivo).bufferedReader().use { it.readText() }
            val arr = JSONArray(json)
            val list = mutableListOf<Movimiento>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(Movimiento(o.getString("d"), o.getDouble("m")))
            }
            list
        } catch (e: Exception) {
            null
        }
    }
}

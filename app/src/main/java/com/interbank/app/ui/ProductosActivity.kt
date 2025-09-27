package com.interbank.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.interbank.app.databinding.ActivityProductosBinding
import com.interbank.app.data.Prefs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductosActivity : AppCompatActivity() {

    private lateinit var b: ActivityProductosBinding
    private var hide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)

        // 1) Observar preferencia de "ocultar saldos" y reflejar en UI
        lifecycleScope.launch {
            Prefs.hideBalanceFlow(this@ProductosActivity).collectLatest { h ->
                hide = h
                renderSaldo()
                b.tvToggleSaldos.text = if (hide) "Mostrar saldos" else "Ocultar saldos"
                // (Opcional: subrayar para "look" de hipervínculo)
                b.tvToggleSaldos.paint.isUnderlineText = true
            }
        }

        // 2) Alternar la preferencia al tocar el "enlace"
        b.tvToggleSaldos.setOnClickListener {
            lifecycleScope.launch {
                Prefs.setHideBalance(this@ProductosActivity, !hide)
            }
        }

        // 3) Solo la cuenta es funcional -> luego abrirá Actividad 3
        b.cardCuenta1.setOnClickListener {
            val intent = Intent(this, CuentaActivity::class.java)
            intent.putExtra("saldo", 1200.00)       // saldo a mostrar en la cuenta
            startActivity(intent)
        }

    }

    private fun renderSaldo() {
        // Si hide = true, mostramos asteriscos
        b.tvSaldo1.text = if (hide) "********" else "S/ 1,200.00"
    }
}

package com.interbank.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.interbank.app.data.Prefs
import com.interbank.app.databinding.ActivityLoginBinding
import com.interbank.app.util.Validators
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        b.tvOlvido.paint.isUnderlineText = true
        b.tvRegistro.paint.isUnderlineText = true

        // 1) Cargar DNI guardado (si existe) y marcar el checkbox
        lifecycleScope.launch {
            Prefs.dniFlow(this@LoginActivity).collectLatest { dniGuardado ->
                if (!dniGuardado.isNullOrBlank()) {
                    b.etDni.setText(dniGuardado)
                    b.cbRecordar.isChecked = true
                }
            }
        }

        // 2) Hipervínculos: abrir navegador (ajusta las URLs oficiales cuando las tengas)
        fun openLink(url: String) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        b.tvOlvido.setOnClickListener {
            openLink("https://www.interbank.pe/") // Recuperar contraseña / ayuda
        }
        b.tvRegistro.setOnClickListener {
            openLink("https://www.interbank.pe/") // Registro / banca móvil
        }

        // 3) Huella -> Toast
        b.ivHuella.setOnClickListener {
            Toast.makeText(this, "Función en construcción", Toast.LENGTH_SHORT).show()
        }

        // 4) Botón Ingresar: validar y guardar/limpiar DNI
        b.btnIngresar.setOnClickListener {
            val dni = b.etDni.text?.toString().orEmpty()
            val pass = b.etPassword.text?.toString().orEmpty()

            // Validación DNI
            if (!Validators.isValidDni(dni)) {
                b.etDni.error = "DNI inválido: deben ser 8 dígitos"
                b.etDni.requestFocus()
                return@setOnClickListener
            }

            // Validación contraseña
            if (!Validators.isValidPassword(pass)) {
                b.etPassword.error = "Solo se permiten letras y dígitos"
                b.etPassword.requestFocus()
                return@setOnClickListener
            }

            // Guardar o limpiar DNI según el checkbox
            lifecycleScope.launch {
                if (b.cbRecordar.isChecked) {
                    Prefs.saveDni(this@LoginActivity, dni)
                } else {
                    Prefs.saveDni(this@LoginActivity, "")
                }
            }

            startActivity(Intent(this, com.interbank.app.ui.ProductosActivity::class.java))

        }
    }
}

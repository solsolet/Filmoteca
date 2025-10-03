package eps.ua.es.filmoteca

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

enum class Mode {
    Layouts,
    Compose,
}

class AboutActivity : ComponentActivity() {
    private val mode = Mode.Compose  // O Mode.Layouts, según quieras probar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }
    private fun initUI() {
        when (mode) {
            Mode.Layouts -> initLayouts()
            Mode.Compose -> initCompose()
        }
    }
    private fun initLayouts() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        // Inicializa tus vistas y lógica aquí
        setContentView(R.layout.activity_about)

        //botones
        val button1 = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)

        button1.setOnClickListener {
            Toast.makeText(this@AboutActivity, "boton 1 pulsado", Toast.LENGTH_LONG).show()
        }
        button2.setOnClickListener {
            Toast.makeText(this@AboutActivity, "boton 2 pulsado", Toast.LENGTH_LONG).show()
        }
        button3.setOnClickListener {
            Toast.makeText(this@AboutActivity, "boton 3 pulsado", Toast.LENGTH_LONG).show()
        }
    }
    private fun initCompose() { //no se què fer ací
        setContent {
            MaterialTheme {
                AboutFilmoteca()
            }
        }
    }
    @Composable
    fun AboutFilmoteca() {
        val context = LocalContext.current

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separat com posa en els apunts
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Creada por: Gemma Selles", style = MaterialTheme.typography.h2)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.monito),
                contentDescription = "Fotillo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Toast.makeText(context, "Funcionalidad sin implementar", Toast.LENGTH_SHORT).show()
            }) {
                Text("Ir al sitio web")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                Toast.makeText(context, "Funcionalidad sin implementar", Toast.LENGTH_SHORT).show()
            }) {
                Text("Obtener soporte")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                finish()
            }) {
                Text("Volver")
            }
        }
    }
}
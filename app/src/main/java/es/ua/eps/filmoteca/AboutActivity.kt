package es.ua.eps.filmoteca

import android.content.Intent
//import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding
import androidx.core.net.toUri

class AboutActivity : ComponentActivity() {
    private lateinit var bindings : ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initUI()
    }
    private fun initUI() {
        when (Filmoteca.GlobalMode) {
            Mode.Bindings -> initLayouts()
            Mode.Compose -> initCompose()
        }
    }
    private fun initLayouts() {
        bindings = ActivityAboutBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            bindings.imageView2.setImageResource(R.drawable.monito)
            bindings.button.setOnClickListener {
                //Toast.makeText(this@AboutActivity, R.string.aviso, Toast.LENGTH_LONG).show()
                val irWeb = Intent(Intent.ACTION_VIEW, "https://www.ua.es/va/".toUri())
                startActivity(irWeb)
            }
            bindings.button2.setOnClickListener {
                //Toast.makeText(this@AboutActivity, R.string.aviso, Toast.LENGTH_LONG).show()
                val soporte = Intent(Intent.ACTION_SENDTO, "mailto:gsl21@alu.ua.es".toUri())
                startActivity(soporte)
            }
            bindings.button3.setOnClickListener {
                //Toast.makeText(this@AboutActivity, R.string.aviso, Toast.LENGTH_LONG).show()
                finish()
            }
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
    private fun AboutFilmoteca() {
        val context = LocalContext.current

        Column( //equivalent a LinearLayout(vertical)
            modifier = Modifier
                .fillMaxSize()      // separat com posa en els apunts
                .padding(64.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.TextView))
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.monito),
                contentDescription = stringResource(R.string.contentImage),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                //Toast.makeText(context, R.string.aviso, Toast.LENGTH_SHORT).show()
                val irWeb = Intent(Intent.ACTION_VIEW, "https://www.ua.es/va/".toUri())
                startActivity(irWeb)
            }) {
                Text(stringResource(R.string.button))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                //Toast.makeText(context, R.string.aviso, Toast.LENGTH_SHORT).show()
                val soporte = Intent(Intent.ACTION_SENDTO, "mailto:gsl21@alu.ua.es".toUri())
                startActivity(soporte)
            }) {
                Text(stringResource(R.string.button2))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                finish()
            }) {
                Text(stringResource(R.string.button3))
            }
        }
    }
}
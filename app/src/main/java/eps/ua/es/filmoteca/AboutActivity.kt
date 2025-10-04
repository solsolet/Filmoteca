package eps.ua.es.filmoteca

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
//import androidx.compose.material3.Text
//import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

enum class Mode {
    Layouts,
    Compose,
}

class AboutActivity : ComponentActivity() {
    private val mode = Mode.Layouts // O Mode.Layouts, según quieras probar

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

//        Log.d("AboutActivity", "initLayouts: layout about assignat")
        val imageView = findViewById<ImageView>(R.id.imageView2)
//        Log.d("AboutActivity", "imageView is null? ${imageView == null}")
//        imageView?.visibility = View.VISIBLE

        imageView?.setImageResource(R.drawable.monito)

        //botones
        val button1 = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)

        button1.setOnClickListener {
            Toast.makeText(this@AboutActivity, R.string.aviso, Toast.LENGTH_LONG).show()
        }
        button2.setOnClickListener {
            Toast.makeText(this@AboutActivity, R.string.aviso, Toast.LENGTH_LONG).show()
        }
        button3.setOnClickListener {
            Toast.makeText(this@AboutActivity, R.string.aviso, Toast.LENGTH_LONG).show()
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
            Text(text = stringResource(R.string.TextView), style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.monito),
                contentDescription = stringResource(R.string.contentImage),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Toast.makeText(context, R.string.aviso, Toast.LENGTH_SHORT).show()
            }) {
                Text(stringResource(R.string.button))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                Toast.makeText(context, R.string.aviso, Toast.LENGTH_SHORT).show()
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
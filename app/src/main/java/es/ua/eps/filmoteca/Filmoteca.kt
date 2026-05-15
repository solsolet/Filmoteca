package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds

class Filmoteca : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        val context: Context?
            get() = mContext

        val GlobalMode = Mode.Bindings
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        // Inicialitza el SDK de Mobile Ads una sola vegada
        MobileAds.initialize(this) {}
    }

    override fun onTerminate() {
        super.onTerminate()
        mContext = null
    }
}
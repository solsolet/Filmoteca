package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class Filmoteca : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        val context: Context?
            get() = mContext

        val GlobalMode = Mode.Compose
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    override fun onTerminate() {
        super.onTerminate()
        mContext = null
    }
}
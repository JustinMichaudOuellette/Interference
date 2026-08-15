package ca.justinmo.interference

import android.app.Application
import java.io.File
import java.io.IOException

class InterferenceApplication: Application() {

    var displayedTooltip: Boolean = false

    override fun onCreate() {
        super.onCreate()

        val tooltipFile = File(filesDir, ".tooltip")
        displayedTooltip = tooltipFile.exists()
        if (!displayedTooltip) {
            try {
                tooltipFile.createNewFile()
            } catch (e: IOException) {

            }
        }
    }
}
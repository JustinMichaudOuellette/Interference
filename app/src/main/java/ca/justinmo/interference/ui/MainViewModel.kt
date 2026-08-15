package ca.justinmo.interference.ui

import android.app.Application
import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import ca.justinmo.interference.InterferenceJNI
import ca.justinmo.interference.NoteScale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var interferenceJNI: InterferenceJNI? = null

    val frequencies = NoteScale.scale(0, 0)
    
    var selectedWave by mutableIntStateOf(0)
    var isXAxis by mutableStateOf(false)
    var invert by mutableStateOf(false)
    var stairs by mutableStateOf(false)
    var distortion by mutableFloatStateOf(0f)
    var exponential by mutableFloatStateOf(1f)
    
    var minFrequencyIndex by mutableIntStateOf(frequencies.indexOf("110.00"))
    var maxFrequencyIndex by mutableIntStateOf(frequencies.indexOf("440.00"))

    init {
        initJni()
    }

    private fun initJni() {
        val audioManager = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toIntOrNull() ?: 44100
        val bufferSize = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toIntOrNull() ?: 512
        
        interferenceJNI = InterferenceJNI(sampleRate, bufferSize, minFrequencyIndex, maxFrequencyIndex)
        updateWave()
    }

    fun onWaveSelected(wave: Int) {
        selectedWave = wave
        updateWave()
    }

    fun onTogglesChanged(isX: Boolean, inv: Boolean, st: Boolean) {
        isXAxis = isX
        invert = inv
        stairs = st
        updateWave()
    }

    fun onDistortionChanged(value: Float) {
        distortion = value
        updateWave()
    }

    fun onExponentialChanged(value: Float) {
        exponential = value
        updateWave()
    }

    fun onMinFrequencyChanged(index: Int) {
        minFrequencyIndex = index
        if (minFrequencyIndex >= maxFrequencyIndex) {
            maxFrequencyIndex = minFrequencyIndex + 1
        }
        interferenceJNI?.setWaveRange(minFrequencyIndex, maxFrequencyIndex)
    }

    fun onMaxFrequencyChanged(index: Int) {
        maxFrequencyIndex = index
        if (maxFrequencyIndex <= minFrequencyIndex) {
            minFrequencyIndex = maxFrequencyIndex - 1
        }
        interferenceJNI?.setWaveRange(minFrequencyIndex, maxFrequencyIndex)
    }

    private fun updateWave() {
        interferenceJNI?.selectWave(
            selectedWave,
            isXAxis,
            invert,
            stairs,
            distortion,
            exponential
        )
    }

    override fun onCleared() {
        interferenceJNI?.destroy()
        super.onCleared()
    }
}

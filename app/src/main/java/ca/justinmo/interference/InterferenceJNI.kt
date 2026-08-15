package ca.justinmo.interference

class InterferenceJNI(sampleRate: Int, bufferSize: Int, minFrequency: Int, maxFrequency: Int) {

    init {
        System.loadLibrary("mowave")
        create(sampleRate, bufferSize)
        setWaveRange(minFrequency, maxFrequency)
    }

    external fun create(sampleRate: Int, samplesPerBuf: Int)

    external fun selectWave(
        waveFunction: Int,
        isY: Boolean,
        invert: Boolean,
        stairs: Boolean,
        distortion: Float,
        exponential: Float)

    external fun setWaveRange(min: Int, max: Int)

    external fun destroy()
}
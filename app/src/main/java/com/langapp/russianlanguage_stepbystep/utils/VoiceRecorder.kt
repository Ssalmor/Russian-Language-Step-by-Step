package com.langapp.russianlanguage_stepbystep.utils

import android.media.MediaRecorder
import java.io.File

class VoiceRecorder {

    private var mediaRecorder: MediaRecorder? = null
    private var output: String = ""

    fun startRecording(outputFile: File) {
        output = outputFile.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(output)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun getOutputFile(): String {
        return output
    }
}
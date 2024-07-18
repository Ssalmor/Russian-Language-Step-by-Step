package com.langapp.russianlanguage_stepbystep.lessonOne

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.utils.VoiceRecorder
import java.io.File
import java.io.IOException
import java.util.Locale

class TestOne : Fragment() {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private val requestCodePermission = 201
    private val speechRecognizer: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    private val answers: MutableMap<Int, String> = mutableMapOf()
    private var currentIndex = 0

    private lateinit var nextTestBtn: Button
    private lateinit var prevBtn: ImageButton
    private lateinit var recordBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var wordTV: TextView
    private lateinit var speechTv: TextView

    private lateinit var dbRef: DatabaseReference
    private var data: ArrayList<String> = arrayListOf()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            initMediaRecorder()
        } else {
            Toast.makeText(requireContext(), "Предоставьте разрешение;(", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), requestCodePermission)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_one, container, false)

        nextTestBtn = view.findViewById(R.id.nextTestBtn)
        wordTV = view.findViewById(R.id.testOneTV)
        nextBtn = view.findViewById(R.id.nextBtn)
        prevBtn = view.findViewById(R.id.prevBtn)
        recordBtn = view.findViewById(R.id.testBtn)
        speechTv = view.findViewById(R.id.saidText)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        readData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        nextBtn.setOnClickListener { navigateQuestions(1) }
        prevBtn.setOnClickListener { navigateQuestions(-1) }

        recordBtn.setOnClickListener {
            if (isRecording) {
                stopRecording()
                recordBtn.setBackgroundResource(R.drawable.baseline_mic_off_24)
            } else {
                startRecording()
                recordBtn.setBackgroundResource(R.drawable.baseline_mic_24)
            }
        }

        nextTestBtn.setOnClickListener {
            showCustomConfirmationDialog()
        }

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onBeginningOfSpeech() {
                TODO("Not yet implemented")
            }

            override fun onRmsChanged(rmsdB: Float) {
                TODO("Not yet implemented")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                TODO("Not yet implemented")
            }

            override fun onEndOfSpeech() {
                TODO("Not yet implemented")
            }

            override fun onError(error: Int) {
                TODO("Not yet implemented")
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    answers[currentIndex] = text
                    speechTv.text = text

                    // Сравнение с текстом из wordTV
                    if (text.equals(wordTV.text.toString(), ignoreCase = true)) {
                        // Ответ совпал с текстом
                    } else {
                        // Ответ не совпал с текстом
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showCustomConfirmationDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.transfer_confirm_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.yesBtn)?.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_testOne_to_testOneExtension)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.noBtn)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun navigateQuestions(direction: Int) {
        val totalQuestions = data.size
        currentIndex = (currentIndex + direction + totalQuestions) % totalQuestions
        updateViews()
    }

    private fun initMediaRecorder() {
        val audioFile = File(requireContext().externalCacheDir, "test_audio.3gp")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
            prepare()
        }
    }


    private fun readData() {
        dbRef.child("WordsToPronounce").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    data.add(it.value.toString())
                }
                updateViews()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun startRecording() {
        if (mediaRecorder == null) {
            initMediaRecorder()
        }
        mediaRecorder?.start()
        isRecording = true
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        mediaRecorder = null
        isRecording = false
    }

    private fun updateViews() {
        val testData = data.elementAtOrNull(currentIndex) ?: return
        wordTV.text = testData
        speechTv.text = answers[currentIndex] ?: ""
    }
}
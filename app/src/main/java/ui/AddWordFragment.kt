package ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hw14.R
import com.example.hw14.databinding.FragmentAddWordBinding
import model.Word
import viewModels.MainViewModel
import java.io.IOException


class AddWordFragment : Fragment() {
    private lateinit var binding:FragmentAddWordBinding
    private val vModel: MainViewModel by activityViewModels()
    private var isFavorite=false
    private var flagStartRecording = true
    private var voiceRecorded=false
    private var recorder: MediaRecorder? = null


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            onClickButtonRecord()
        } else {
            Toast.makeText(requireContext(), "you denied this permission", Toast.LENGTH_SHORT).show()
        }
    }


    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                //if user already granted the permission
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                    onClickButtonRecord()
                }
                //if user already denied the permission once
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.RECORD_AUDIO) -> {
                    //you can show rational massage in any form you want
                    showRationDialog()
                    //Snackbar.make( binding.buttonRecord, "we use camera to scan text.", Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }
    private fun showRationDialog() {
        val builder= AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage("we need allow to record audio.")
            setTitle("permission required")
            setPositiveButton("ok"){ _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
        builder.create().show()
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddWordBinding.inflate (inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_add_word, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        recordAudio()

        binding.buttonFavorite.setOnClickListener {
            binding.buttonFavorite.setIconResource(R.drawable.ic_baseline_favorite_24)
            isFavorite=!isFavorite
        }

        binding.buttonSave.setOnClickListener {
            when{
                binding.editTextWord.editText?.text.isNullOrBlank()-> binding.editTextWord.error="کلمه را وارد کنید"
                vModel.searchWord(binding.editTextWord.editText?.text.toString())!=0->binding.editTextWord.error="این کلمه قبلا وارد شده است"
                binding.editTextMeaning.editText?.text.isNullOrBlank()-> binding.editTextMeaning.error="معنی را وارد کنید"
                binding.editTextSynonyms.editText?.text.isNullOrBlank()-> binding.editTextSynonyms.error="مترادف را وارد کنید"

                else ->{

                        vModel.insert(Word(0,binding.editTextWord.editText?.text.toString(),
                            binding.editTextMeaning.editText?.text.toString(),binding.editTextSynonyms.editText?.text.toString(),
                            binding.editTextExample.editText?.text.toString(),binding.editTextDescription.editText?.text.toString(),isFavorite,voiceRecorded))
                        Toast.makeText(requireActivity(),"کلمه ذخیره شد", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_addWordFragment_to_searchWordFragment)
                }
            }
        }
    }

    private fun recordAudio() {
        binding.buttonRecord.setOnClickListener {
            requestPermissions()
        }
    }

    private fun onClickButtonRecord() {
        if (binding.editTextWord.editText?.text.toString()=="") {
            Toast.makeText(requireContext(), "یک کلمه وارد کنید", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            startRecord()
        }
    }

    private fun startRecord() {
        onRecord(flagStartRecording)

        binding.buttonRecord.setIconResource(when (flagStartRecording) {
            true -> R.drawable.ic_baseline_mic_off_2
            false -> R.drawable.ic_baseline_mic_2
        })

        binding.buttonRecord.text=
            when (flagStartRecording) {
                true -> "قطع ضبط"
                false -> "ضبط صدا"
            }
        flagStartRecording = !flagStartRecording
    }


    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        val name=binding.editTextWord.editText?.text.toString()
        val fileName="${requireActivity().externalCacheDir?.absolutePath}/$name.3gp"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("AudioRecordTest", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        voiceRecorded=true
        recorder = null
    }


}
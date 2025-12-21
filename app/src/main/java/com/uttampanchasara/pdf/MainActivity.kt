package com.uttampanchasara.pdf

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.uttampanchasara.pdf.databinding.ActivityMainBinding
import com.uttampanchasara.pdfgenerator.CreatePdf

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerate.setOnClickListener { generatePdf(showPrintDialog = false) }
        binding.btnGenerateAndPrint.setOnClickListener { generatePdf(showPrintDialog = true) }
    }

    private fun generatePdf(showPrintDialog: Boolean) {
        CreatePdf(this)
            .setPdfName("Sample")
            .setFilePath(CreatePdf.getDefaultSavePath(this, "MyPdf"))
            .setContent(getString(R.string.content))
            .openPrintDialog(showPrintDialog)
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onSuccess(filePath: String) {
                    Log.i(TAG, "PDF saved at: $filePath")
                    showSnackbar("PDF saved successfully!")
                }

                override fun onFailure(errorMsg: String) {
                    Log.e(TAG, "PDF generation failed: $errorMsg")
                    showSnackbar("Error: $errorMsg")
                }
            })
            .create()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

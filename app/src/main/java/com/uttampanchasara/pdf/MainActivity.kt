package com.uttampanchasara.pdf

import android.os.Bundle
import android.print.PrintAttributes
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.uttampanchasara.pdf.databinding.ActivityMainBinding
import com.uttampanchasara.pdfgenerator.CreatePdf

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var openPrintDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnGenerate.setOnClickListener {
            openPrintDialog = false
            generatePdf()
        }

        binding.btnGenerateAndPrint.setOnClickListener {
            openPrintDialog = true
            generatePdf()
        }
    }

    private fun generatePdf() {
        // Use modern app-specific storage path (no permissions needed)
        val savePath = CreatePdf.getDefaultSavePath(this, "MyPdf")

        CreatePdf(this)
            .setPdfName("Sample")
            .openPrintDialog(openPrintDialog)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setFilePath(savePath)
            .setContent(getString(R.string.content))
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    showError(errorMsg)
                }

                override fun onSuccess(filePath: String) {
                    showSuccess(filePath)
                }
            })
            .create()
    }

    private fun showSuccess(filePath: String) {
        Log.i(TAG, "PDF saved at: $filePath")
        Snackbar.make(
            binding.root,
            "PDF saved successfully!",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showError(errorMsg: String) {
        Log.e(TAG, "PDF generation failed: $errorMsg")
        Snackbar.make(
            binding.root,
            "Error: $errorMsg",
            Snackbar.LENGTH_LONG
        ).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

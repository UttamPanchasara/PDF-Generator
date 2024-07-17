package com.uttampanchasara.pdf

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.uttampanchasara.pdfgenerator.CreatePdf

class MainActivity : AppCompatActivity(), CreatePdf.PdfCallbackListener {

    override fun onSuccess(filePath: String) {
        Log.i("MainActivity", "Pdf Saved at: $filePath")

        Toast.makeText(this, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }

    var openPrintDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPrint = findViewById<Button>(R.id.btnPrint)
        val btnPrintAndSave = findViewById<Button>(R.id.btnPrintAndSave)
        btnPrint.setOnClickListener {
            openPrintDialog = false
           doPrint()
        }

        btnPrintAndSave.setOnClickListener {
            openPrintDialog = true
            doPrint()
        }
    }

    private fun doPrint() {
        CreatePdf(this)
            .setPdfName("Sample")
            .openPrintDialog(openPrintDialog)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setFilePath("MyPdf")
            .setContent(getString(R.string.content))
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(filePath: String) {
                    Toast.makeText(this@MainActivity, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
                }
            })
            .create()
    }
}
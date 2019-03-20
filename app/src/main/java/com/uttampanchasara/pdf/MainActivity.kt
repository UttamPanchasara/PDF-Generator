package com.uttampanchasara.pdf

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.uttampanchasara.pdfgenerator.CreatePdf
import kotlinx.android.synthetic.main.activity_main.*

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
            .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/MyPdf")
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
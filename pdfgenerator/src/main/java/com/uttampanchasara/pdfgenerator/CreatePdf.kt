package com.uttampanchasara.pdfgenerator

import android.annotation.SuppressLint
import android.content.Context
import android.print.PdfPrint
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.io.File


/**
 * @since 11/30/2018
 */
open class CreatePdf(private val mContext: Context) {

    private var MimeType = "text/html"
    private var ENCODING = "utf-8"

    private var mBaseURL: String? = null
    private var mPdfName: String? = ""
    private var mCallbacks: PdfCallbackListener? = null
    private var mContent: String? = ""
    private var doPrint: Boolean = false

    /**
     * file will be saved as : pdfName
     *
     **/
    fun setPdfName(@NotNull pdfName: String): CreatePdf {
        this.mPdfName = "$pdfName.pdf"
        return this
    }

    /**
     * if content going to be load from assets or from other source
     */
    fun setContentBaseUrl(@Nullable baseUrl: String?): CreatePdf {
        this.mBaseURL = baseUrl
        return this
    }

    fun setContent(@NotNull content: String?): CreatePdf {
        this.mContent = content
        return this
    }

    fun openPrintDialog(doPrint: Boolean): CreatePdf {
        this.doPrint = doPrint
        return this
    }

    fun setCallbackListener(@NotNull callbacks: PdfCallbackListener): CreatePdf {
        this.mCallbacks = callbacks
        return this
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun create() {
        var webView: WebView? = WebView(mContext)
        if (mContent?.isNotEmpty()!!) {
            webView?.loadDataWithBaseURL(mBaseURL, mContent, MimeType, ENCODING, null)
            webView?.settings?.javaScriptEnabled = true
            webView?.clearCache(true)
            webView?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    // Get a print adapter instance
                    val printAdapter = webView?.createPrintDocumentAdapter(mPdfName)!!

                    if (doPrint) {
                        // Get a PrintManager instance
                        val printManager = mContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
                        // Create a print job with name and adapter instance
                        printManager.print(mPdfName, printAdapter, PrintAttributes.Builder().build())
                    }
                    savePdf(printAdapter)
                    webView = null
                }
            }
        } else {
            mCallbacks?.onFailure("Empty or null content")
        }
    }

    private fun savePdf(printAdapter: PrintDocumentAdapter) {
        //save pdf
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        PdfPrint(printAttributes).print(
            printAdapter,
            File(mContext.cacheDir?.absolutePath),
            mPdfName,
            object : PdfPrint.CallbackPrint {
                override fun success(path: String?) {
                    mCallbacks?.onSuccess(path!!)
                }

                override fun onFailure(errorMsg: String?) {
                    mCallbacks?.onFailure(errorMsg!!)
                }
            })
    }

    interface PdfCallbackListener {
        fun onFailure(errorMsg: String)
        fun onSuccess(filePath: String)
    }
}
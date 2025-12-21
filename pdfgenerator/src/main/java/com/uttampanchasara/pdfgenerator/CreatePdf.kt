package com.uttampanchasara.pdfgenerator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.print.PdfPrint
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume

/**
 * PDF Generator Library
 *
 * Easy way to create PDF from HTML content, URLs, or Asset files.
 *
 * @since 11/30/2018
 * @version 2.0
 */
@Suppress("unused") // Public API methods are used by library consumers
open class CreatePdf(private val mContext: Context) {

    companion object {
        /** Default resolution in DPI */
        const val DEFAULT_DPI = 600

        /** Default timeout in milliseconds (30 seconds) */
        const val DEFAULT_TIMEOUT_MS = 30_000L

        /**
         * Helper to get a modern storage path that works on Android 10+
         * Uses app-specific external files directory which doesn't require permissions
         */
        @JvmStatic
        fun getDefaultSavePath(context: Context, subdirectory: String = "PDF"): String {
            val dir = File(context.getExternalFilesDir(null), subdirectory)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir.absolutePath
        }
    }

    private var mimeType = "text/html"
    private var encoding = "utf-8"

    private var mBaseURL: String? = null
    private var mPdfName: String = ""
    private var mCallbacks: PdfCallbackListener? = null
    private var mContent: String? = null
    private var mUrl: String? = null
    private var mAssetPath: String? = null
    private var doPrint: Boolean = false
    private var mPdfFilePath: String = ""
    private var mWebView: WebView? = null
    private var mMediaSize: PrintAttributes.MediaSize = PrintAttributes.MediaSize.ISO_A4
    private var mMargins: PrintAttributes.Margins = PrintAttributes.Margins.NO_MARGINS
    private var mResolutionDpi: Int = DEFAULT_DPI
    private var mTimeoutMs: Long = DEFAULT_TIMEOUT_MS
    private var mIsLandscape: Boolean = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isCompleted = false
    private var timeoutRunnable: Runnable? = null

    // ==================== Builder Methods ====================

    /**
     * Set the PDF file name.
     *
     * The .pdf extension will be added automatically if not provided.
     *
     * @param pdfName File name for the generated PDF (e.g., "invoice" or "invoice.pdf")
     */
    fun setPdfName(pdfName: String): CreatePdf {
        this.mPdfName = if (pdfName.endsWith(".pdf")) pdfName else "$pdfName.pdf"
        return this
    }

    /**
     * Set the base URL for resolving relative paths in HTML content.
     *
     * Useful when your HTML references local images or stylesheets with relative paths.
     * For asset files, this is automatically set to "file:///android_asset/".
     *
     * @param baseUrl Base URL for relative path resolution (e.g., "file:///android_asset/")
     */
    fun setContentBaseUrl(baseUrl: String?): CreatePdf {
        this.mBaseURL = baseUrl
        return this
    }

    /**
     * Set HTML content to convert to PDF.
     *
     * Pass your HTML string directly. Can be plain text or full HTML with CSS styling.
     * Note: Only one content source can be used (content, URL, or asset path).
     *
     * @param content HTML or plain text content to render as PDF
     */
    fun setContent(content: String): CreatePdf {
        this.mContent = content
        this.mUrl = null
        this.mAssetPath = null
        return this
    }

    /**
     * Set a web URL to load and convert to PDF.
     *
     * The webpage will be fully loaded before PDF generation.
     * Requires INTERNET permission in AndroidManifest.xml.
     * Note: Only one content source can be used (content, URL, or asset path).
     *
     * @param url Full URL of the webpage (e.g., "https://example.com")
     */
    fun setUrl(url: String): CreatePdf {
        this.mUrl = url
        this.mContent = null
        this.mAssetPath = null
        return this
    }

    /**
     * Set an asset file path to load and convert to PDF.
     *
     * Load HTML files from your app's assets folder.
     * The base URL is automatically set to "file:///android_asset/" for relative paths.
     * Note: Only one content source can be used (content, URL, or asset path).
     *
     * @param assetPath Path relative to assets folder (e.g., "templates/invoice.html")
     */
    fun setAssetPath(assetPath: String): CreatePdf {
        this.mAssetPath = assetPath
        this.mContent = null
        this.mUrl = null
        this.mBaseURL = "file:///android_asset/"
        return this
    }

    /**
     * Open the Android system print dialog after PDF generation.
     *
     * When enabled, the print dialog will appear after the PDF is saved,
     * allowing the user to print or save to another location.
     *
     * @param doPrint true to show print dialog after PDF creation, false otherwise
     */
    fun openPrintDialog(doPrint: Boolean): CreatePdf {
        this.doPrint = doPrint
        return this
    }

    /**
     * Set the directory path where the PDF will be saved.
     *
     * If not set, the PDF will be saved in the app's cache directory.
     * Use [getDefaultSavePath] for a recommended app-specific location that
     * doesn't require storage permissions.
     *
     * @param pdfFilePath Directory path to save the PDF (e.g., "/storage/emulated/0/Documents")
     */
    fun setFilePath(pdfFilePath: String): CreatePdf {
        this.mPdfFilePath = pdfFilePath
        return this
    }

    /**
     * Set the page size for the PDF.
     *
     * Common options:
     * - PrintAttributes.MediaSize.ISO_A4 (default, 210 x 297 mm)
     * - PrintAttributes.MediaSize.NA_LETTER (US Letter, 8.5 x 11 inches)
     * - PrintAttributes.MediaSize.ISO_A3 (297 x 420 mm)
     *
     * @param size Page size from PrintAttributes.MediaSize
     */
    fun setPageSize(size: PrintAttributes.MediaSize): CreatePdf {
        this.mMediaSize = size
        return this
    }

    /**
     * Set the page orientation to landscape.
     *
     * Default is portrait (false). When set to true, the page dimensions are swapped.
     *
     * @param landscape true for landscape orientation, false for portrait
     */
    fun setLandscape(landscape: Boolean): CreatePdf {
        this.mIsLandscape = landscape
        return this
    }

    /**
     * Set page margins in millimeters.
     *
     * Defines the printable area boundaries. For consistent results across devices,
     * consider adding CSS padding to your HTML content instead.
     *
     * @param left Left margin in millimeters
     * @param top Top margin in millimeters
     * @param right Right margin in millimeters
     * @param bottom Bottom margin in millimeters
     */
    fun setMargins(left: Float, top: Float, right: Float, bottom: Float): CreatePdf {
        // Convert mm to mils (1 mm = 39.3701 mils)
        val factor = 39.3701f
        this.mMargins = PrintAttributes.Margins(
            (left * factor).toInt(),
            (top * factor).toInt(),
            (right * factor).toInt(),
            (bottom * factor).toInt()
        )
        return this
    }

    /**
     * Set the PDF resolution (quality).
     *
     * Higher DPI means better quality but larger file size.
     * - 150 DPI: Draft quality, small file size
     * - 300 DPI: Good quality for most uses
     * - 600 DPI: High quality (default)
     *
     * @param dpi Resolution in dots per inch
     */
    fun setResolution(dpi: Int): CreatePdf {
        this.mResolutionDpi = dpi
        return this
    }

    /**
     * Set the timeout for PDF generation.
     *
     * If the PDF is not generated within this time, the operation fails.
     * Increase for complex pages or slow network connections.
     * Default: 30 seconds (30000 ms)
     *
     * @param timeoutMs Timeout in milliseconds
     */
    fun setTimeout(timeoutMs: Long): CreatePdf {
        this.mTimeoutMs = timeoutMs
        return this
    }

    /**
     * Set callback listener for PDF generation results.
     *
     * Receive notifications when PDF generation succeeds or fails.
     * - onSuccess: Called with the file path of the generated PDF
     * - onFailure: Called with an error message describing what went wrong
     *
     * @param callbacks Listener implementing [PdfCallbackListener]
     */
    fun setCallbackListener(callbacks: PdfCallbackListener): CreatePdf {
        this.mCallbacks = callbacks
        return this
    }

    // ==================== Create Methods ====================

    /**
     * Start PDF generation with the configured settings.
     *
     * This is an asynchronous operation. Results are delivered via the callback
     * set with [setCallbackListener]. For Kotlin coroutines, use [createAsync] instead.
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun create() {
        isCompleted = false

        if (!validateInputs()) return

        // Ensure WebView operations run on main thread
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { createWebViewAndLoad() }
        } else {
            createWebViewAndLoad()
        }
    }

    /**
     * Create the PDF with configured settings (Kotlin Coroutines)
     *
     * @return Result containing file path on success or error message on failure
     * @throws kotlinx.coroutines.TimeoutCancellationException if timeout exceeded
     */
    suspend fun createAsync(): Result<String> = withTimeout(mTimeoutMs) {
        suspendCancellableCoroutine { continuation ->
            val originalCallback = mCallbacks

            mCallbacks = object : PdfCallbackListener {
                override fun onSuccess(filePath: String) {
                    originalCallback?.onSuccess(filePath)
                    if (continuation.isActive) {
                        continuation.resume(Result.success(filePath))
                    }
                }

                override fun onFailure(errorMsg: String) {
                    originalCallback?.onFailure(errorMsg)
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(PdfGenerationException(errorMsg)))
                    }
                }
            }

            continuation.invokeOnCancellation {
                cleanupWebView()
                mCallbacks = originalCallback
            }

            create()
        }
    }

    // ==================== Private Methods ====================

    private fun validateInputs(): Boolean {
        if (mPdfName.isEmpty()) {
            handleError("PDF name must not be empty.")
            return false
        }

        if (mContent == null && mUrl == null && mAssetPath == null) {
            handleError("Content, URL, or Asset path must be set.")
            return false
        }

        return true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebViewAndLoad() {
        try {
            // Start timeout timer
            startTimeout()

            mWebView = WebView(mContext).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        if (isCompleted) return

                        // Small delay to ensure content is fully rendered
                        mainHandler.postDelayed({
                            if (!isCompleted) {
                                try {
                                    val printAdapter: PrintDocumentAdapter = view.createPrintDocumentAdapter(mPdfName)
                                    savePdf(printAdapter)
                                } catch (e: Exception) {
                                    handleError("Failed to create print adapter: ${e.message}")
                                }
                            }
                        }, 100)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        if (request?.isForMainFrame == true) {
                            handleError("WebView error: ${error?.description}")
                        }
                    }
                }

                // Load content based on source type
                when {
                    mContent != null -> {
                        loadDataWithBaseURL(mBaseURL, mContent!!, mimeType, encoding, null)
                    }
                    mUrl != null -> {
                        loadUrl(mUrl!!)
                    }
                    mAssetPath != null -> {
                        val assetContent = loadAssetFile(mAssetPath!!)
                        if (assetContent != null) {
                            loadDataWithBaseURL(mBaseURL, assetContent, mimeType, encoding, null)
                        } else {
                            handleError("Failed to load asset file: $mAssetPath")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            handleError("Failed to initialize WebView: ${e.message}")
        }
    }

    private fun loadAssetFile(assetPath: String): String? {
        return try {
            mContext.assets.open(assetPath).bufferedReader().use { it.readText() }
        } catch (_: IOException) {
            null
        }
    }

    private fun savePdf(printAdapter: PrintDocumentAdapter) {
        val filePath = mPdfFilePath.ifEmpty { mContext.cacheDir.absolutePath }

        val file = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }

        // Apply landscape orientation if set
        val mediaSize = if (mIsLandscape) {
            mMediaSize.asLandscape()
        } else {
            mMediaSize
        }

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(mediaSize)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", mResolutionDpi, mResolutionDpi))
            .setMinMargins(mMargins)
            .build()

        PdfPrint(printAttributes).print(
            printAdapter,
            file,
            mPdfName,
            object : PdfPrint.CallbackPrint {
                override fun onSuccess(path: String) {
                    cancelTimeout()
                    isCompleted = true
                    mCallbacks?.onSuccess(path)

                    if (doPrint) {
                        // Create a fresh adapter from WebView for print dialog
                        // (the original adapter was consumed by PDF generation)
                        // Note: Don't cleanup WebView here - print dialog needs it alive
                        mWebView?.let { webView ->
                            val freshAdapter = webView.createPrintDocumentAdapter(mPdfName)
                            openPrintDialog(freshAdapter)
                        }
                    } else {
                        cleanupWebView()
                    }
                }

                override fun onFailure(errorMsg: String) {
                    handleError(errorMsg)
                }
            }
        )
    }

    private fun openPrintDialog(printAdapter: PrintDocumentAdapter) {
        try {
            val printManager = mContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print(mPdfName, printAdapter, PrintAttributes.Builder().build())
        } catch (_: Exception) {
            // Print dialog failed but PDF was created successfully
        }
    }

    private fun startTimeout() {
        cancelTimeout()
        timeoutRunnable = Runnable {
            if (!isCompleted) {
                handleError("PDF generation timed out after ${mTimeoutMs}ms")
            }
        }
        mainHandler.postDelayed(timeoutRunnable!!, mTimeoutMs)
    }

    private fun cancelTimeout() {
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null
    }

    private fun handleError(errorMsg: String) {
        if (isCompleted) return

        cancelTimeout()
        isCompleted = true
        mCallbacks?.onFailure(errorMsg)
        cleanupWebView()
    }

    private fun cleanupWebView() {
        mWebView?.let { webView ->
            mainHandler.post {
                try {
                    webView.stopLoading()
                    webView.clearHistory()
                    webView.clearCache(true)
                    webView.loadUrl("about:blank")
                    webView.onPause()
                    webView.removeAllViews()
                    webView.destroy()
                } catch (_: Exception) {
                    // Ignore cleanup errors
                }
            }
        }
        mWebView = null
    }

    // ==================== Interfaces & Exceptions ====================

    interface PdfCallbackListener {
        fun onFailure(errorMsg: String)
        fun onSuccess(filePath: String)
    }

    class PdfGenerationException(message: String) : Exception(message)
}

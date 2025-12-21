# PDF Generator Library ProGuard Rules

# Keep all public API classes
-keep public class com.uttampanchasara.pdfgenerator.CreatePdf { *; }
-keep public interface com.uttampanchasara.pdfgenerator.CreatePdf$PdfCallbackListener { *; }

# Keep the PdfPrint class in android.print package
-keep class android.print.PdfPrint { *; }
-keep interface android.print.PdfPrint$CallbackPrint { *; }

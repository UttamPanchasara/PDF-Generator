# PDF-Generator

![Version](https://img.shields.io/badge/Version-2.0.0-blue.svg) ![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg) [![](https://jitpack.io/v/UttamPanchasara/PDF-Generator.svg)](https://jitpack.io/#UttamPanchasara/PDF-Generator)

PDF Generator library - Easy way to create PDF from HTML content, URLs, or Asset files.

## Features

- Generate PDF from **HTML content**, **URLs**, or **Asset files**
- **Kotlin Coroutines** support with `createAsync()`
- Customizable **page size**, **margins**, **resolution**, and **orientation**
- Built-in **timeout handling** to prevent hanging
- Modern **scoped storage** support (no permissions needed)
- Proper **memory management** and error handling

## Installation

Add JitPack repository to your root `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app's `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.UttamPanchasara:PDF-Generator:2.0.0'
}
```

## Quick Start

### Basic Usage (Kotlin)

```kotlin
val savePath = CreatePdf.getDefaultSavePath(context, "MyPDFs")

CreatePdf(context)
    .setPdfName("document")
    .setContent("<html><body><h1>Hello World</h1></body></html>")
    .setFilePath(savePath)
    .setCallbackListener(object : CreatePdf.PdfCallbackListener {
        override fun onSuccess(filePath: String) {
            // PDF created at filePath
        }
        override fun onFailure(errorMsg: String) {
            // Handle error
        }
    })
    .create()
```

### With Kotlin Coroutines

```kotlin
lifecycleScope.launch {
    val result = CreatePdf(context)
        .setPdfName("document")
        .setContent("<html><body><h1>Hello World</h1></body></html>")
        .setFilePath(CreatePdf.getDefaultSavePath(context))
        .createAsync()

    result.onSuccess { filePath ->
        // PDF created at filePath
    }.onFailure { error ->
        // Handle error
    }
}
```

### Generate PDF from URL

```kotlin
CreatePdf(context)
    .setPdfName("webpage")
    .setUrl("https://example.com")
    .setTimeout(60_000) // 60 seconds for web pages
    .setCallbackListener(listener)
    .create()
```

### Generate PDF from Asset File

```kotlin
CreatePdf(context)
    .setPdfName("invoice")
    .setAssetPath("templates/invoice.html")
    .setCallbackListener(listener)
    .create()
```

### Landscape with Custom Margins

```kotlin
CreatePdf(context)
    .setPdfName("report")
    .setContent(htmlContent)
    .setLandscape(true)
    .setMarginsFromMm(10f, 10f, 10f, 10f) // 10mm margins
    .setCallbackListener(listener)
    .create()
```

### Custom Resolution (DPI)

```kotlin
CreatePdf(context)
    .setPdfName("high-quality")
    .setContent(htmlContent)
    .setResolution(300) // 300 DPI (default is 600)
    .setCallbackListener(listener)
    .create()
```

## API Reference

### Content Sources (use one)

| Method | Description |
|--------|-------------|
| `setContent(html)` | Set HTML/text content to convert |
| `setUrl(url)` | Load URL and convert to PDF (requires INTERNET permission) |
| `setAssetPath(path)` | Load HTML from assets folder |

### Page Configuration

| Method | Default | Description |
|--------|---------|-------------|
| `setPageSize(size)` | `ISO_A4` | Page size (ISO_A4, NA_LETTER, etc.) |
| `setLandscape(bool)` | `false` | Enable landscape orientation |
| `setMargins(l,t,r,b)` | `NO_MARGINS` | Set margins in mils (1/1000 inch) |
| `setMarginsFromMm(l,t,r,b)` | - | Set margins in millimeters |
| `setResolution(dpi)` | `600` | PDF resolution/quality |

### Output Configuration

| Method | Description |
|--------|-------------|
| `setPdfName(name)` | PDF filename (without .pdf extension) |
| `setFilePath(path)` | Directory to save PDF |
| `openPrintDialog(bool)` | Open print dialog after creation |

### Advanced Options

| Method | Default | Description |
|--------|---------|-------------|
| `setTimeout(ms)` | `30000` | Timeout in milliseconds |
| `setContentBaseUrl(url)` | `null` | Base URL for relative paths |

### Creation Methods

| Method | Description |
|--------|-------------|
| `create()` | Create PDF asynchronously with callbacks |
| `createAsync()` | Create PDF with Kotlin Coroutines (returns `Result<String>`) |

### Helper Methods

| Method | Description |
|--------|-------------|
| `CreatePdf.getDefaultSavePath(context, subdir)` | Get app-specific storage path (no permissions needed) |

## Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Compile SDK**: 35 (Android 15)
- **Kotlin**: 2.0+

## Permissions

For **URL loading**, add to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**No storage permissions needed** when using `getDefaultSavePath()`.

## Migration from v1.x

1. Update dependency to use JitPack
2. Replace `Environment.getExternalStorageDirectory()` with `CreatePdf.getDefaultSavePath()`
3. Remove storage permissions if using app-specific storage
4. `setPageSize()` is now optional (defaults to A4)

## Questions?

**Ping me on:** [![Twitter](https://img.shields.io/badge/Twitter-%40UTM__Panchasara-blue.svg)](https://twitter.com/UTM_Panchasara) [![Facebook](https://img.shields.io/badge/Facebook-Uttam%20Panchasara-blue.svg)](https://www.facebook.com/UttamPanchasara94)

<a href="https://stackoverflow.com/users/5719935/uttam-panchasara">
<img src="https://stackoverflow.com/users/flair/5719935.png" width="208" height="58" alt="profile for Uttam Panchasara at Stack Overflow, Q&amp;A for professional and enthusiast programmers" title="profile for Uttam Panchasara at Stack Overflow, Q&amp;A for professional and enthusiast programmers">
</a>

## Donate

> If you found this library helpful, consider buying me a cup of :coffee:
- PayPal **https://paypal.me/UttamPanchasara**

## Stargazers over time

[![Stargazers over time](https://starchart.cc/UttamPanchasara/PDF-Generator.svg)](https://starchart.cc/UttamPanchasara/PDF-Generator)

## License

```
Copyright 2019-2025 Uttam Panchasara

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

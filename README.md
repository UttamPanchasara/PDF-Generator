# PDF-Generator

![Version](https://img.shields.io/badge/Version-2.0.0-blue.svg) ![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg) [![](https://jitpack.io/v/UttamPanchasara/PDF-Generator.svg)](https://jitpack.io/#UttamPanchasara/PDF-Generator)

PDF Generator library - Easy way to create PDF from String Content or HTML Content.

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

### Kotlin

```kotlin
// Use app-specific storage (recommended, no permissions needed)
val savePath = CreatePdf.getDefaultSavePath(context, "MyPDFs")

CreatePdf(context)
    .setPdfName("FirstPdf")
    .openPrintDialog(false)
    .setContentBaseUrl(null)
    .setPageSize(PrintAttributes.MediaSize.ISO_A4)
    .setFilePath(savePath)
    .setContent("<html><body><h1>Hello World</h1></body></html>")
    .setCallbackListener(object : CreatePdf.PdfCallbackListener {
        override fun onFailure(errorMsg: String) {
            // Handle error
        }

        override fun onSuccess(filePath: String) {
            // PDF created successfully at filePath
        }
    })
    .create()
```

### Java

```java
// Use app-specific storage (recommended, no permissions needed)
String savePath = CreatePdf.getDefaultSavePath(context, "MyPDFs");

new CreatePdf(context)
    .setPdfName("FirstPdf")
    .openPrintDialog(false)
    .setContentBaseUrl(null)
    .setPageSize(PrintAttributes.MediaSize.ISO_A4)
    .setFilePath(savePath)
    .setContent("<html><body><h1>Hello World</h1></body></html>")
    .setCallbackListener(new CreatePdf.PdfCallbackListener() {
        @Override
        public void onFailure(@NotNull String errorMsg) {
            // Handle error
        }

        @Override
        public void onSuccess(@NotNull String filePath) {
            // PDF created successfully at filePath
        }
    })
    .create();
```

## API Reference

| Method | Type | Description |
|--------|------|-------------|
| `setPdfName` | String | Name for the PDF file (without extension) |
| `openPrintDialog` | Boolean | If `true`, opens Android print dialog after PDF creation |
| `setContentBaseUrl` | String? | Base URL for loading assets (same as WebView baseUrl) |
| `setPageSize` | PrintAttributes.MediaSize | Page size (e.g., `ISO_A4`, `ISO_A3`, `NA_LETTER`) |
| `setContent` | String | HTML or text content to convert to PDF |
| `setFilePath` | String | Directory path to save the PDF |
| `setCallbackListener` | PdfCallbackListener | Callback for success/failure notifications |

### Helper Methods

| Method | Description |
|--------|-------------|
| `CreatePdf.getDefaultSavePath(context, subdirectory)` | Returns app-specific storage path (no permissions needed) |

## Storage

Starting with version 2.0, the library uses app-specific storage by default, which doesn't require any storage permissions. Use `CreatePdf.getDefaultSavePath()` to get a safe storage path.

If you need to save to a custom location, ensure your app has the appropriate permissions.

## Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Compile SDK**: 35 (Android 15)
- **Kotlin**: 2.0+

## Migration from v1.x

1. Update the dependency to use JitPack
2. Replace `Environment.getExternalStorageDirectory()` with `CreatePdf.getDefaultSavePath()`
3. Remove storage permissions if using app-specific storage

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
Copyright 2019-2024 Uttam Panchasara

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

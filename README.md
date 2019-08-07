# PDF-Generator
![Download](https://img.shields.io/badge/Download-1.3-blue.svg) ![Download](https://img.shields.io/badge/API-%2B21-brightgreen.svg) [![Download](https://img.shields.io/badge/Android%20Arsenal-PDF%20Generator-red.svg)](https://android-arsenal.com/details/1/7355)

PDF Generator library, easy way to create PDF from String Content or Any HTML Content.

## Get Started

```gradle
dependencies {
     implementation 'com.uttampanchasara.pdfgenerator:pdfgenerator:1.3'
}
```

## Quick Start
In order to start using PdfGenerator, Just copy below code to your project and just pass the required values and that's all you done!


### Kotlin Code:
```kotlin
 CreatePdf(this)
            .setPdfName("FirstPdf")
            .openPrintDialog(false)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setContent("Your Content")
            .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/MyPdf")
            .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(filePath: String) {
                    Toast.makeText(this@MainActivity, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
                }
            })
            .create()
```


### Java Code:
```java
new CreatePdf(this)
            .setPdfName("FirstPdf")
            .openPrintDialog(false)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setContent("Your Content")
            .setFilePath(Environment.getExternalStorageDirectory().absolutePath + "/MyPdf")
            .setCallbackListener(new CreatePdf.PdfCallbackListener() {
                @Override
                public void onFailure(@NotNull String s) {
                    // handle error
                }

                @Override
                public void onSuccess(@NotNull String s) {
                    // do your stuff here
                }
            })
            .create();
```
## NOTE: ( Provide STORAGE Permission if you are providing filePath to library )
### In library, I'm not handling any storage permission related exception, If you are providing your custom filePath then your application must have STORAGE READ-WRITE Permission in order to store Pdf in provided path. 

## Usage

- #### `setPdfName` : String
Provide Your Pdf name, Library will use to save pdf with this name.


- #### `openPrintDialog` : Boolean
Default is `false`, If you set `true` it will send your pdf for print and open the android default pdf print view.


- #### `setContentBaseUrl` : String
If you are loading content from assets folder in that case you can pass your base url here, same as we passed in webview.

- #### `setPageSize` : PrintAttributes.MediaSize
To set custom page size for your pdf, you will have to pass the mediaSize as argument. 

Example - For A4 size: `PrintAttributes.MediaSize.ISO_A4 | ISO_A0 | ISO_A1`

- #### `setContent` : String
Provide your String content, which you want to generate Pdf.

- #### `setFilePath` : String
Provide custom file path to save pdf in your own directory, default will be the cache directory of Application

- #### `setCallbackListener` : Listener Interface
Set this callback listener to get callback on pdf generated.

## Benefits
- Easily Generate Pdf
- No Extra codes
- Time saving
- Lightweight

## More?
If you have any suggestions or you can make this library better write me, create issue, or you can also write code and send pull request.

## Questions?
 
**Ping-Me on :**  [![Twitter](https://img.shields.io/badge/Twitter-%40UTM__Panchasara-blue.svg)](https://twitter.com/UTM_Panchasara)
[![Facebook](https://img.shields.io/badge/Facebook-Uttam%20Panchasara-blue.svg)](https://www.facebook.com/UttamPanchasara94)


<a href="https://stackoverflow.com/users/5719935/uttam-panchasara">
<img src="https://stackoverflow.com/users/flair/5719935.png" width="208" height="58" alt="profile for Uttam Panchasara at Stack Overflow, Q&amp;A for professional and enthusiast programmers" title="profile for Uttam Panchasara at Stack Overflow, Q&amp;A for professional and enthusiast programmers">
</a>


# Donate
> If you found this library helpful, consider buying me a cup of :coffee:
- Google Pay **(panchasarauttam@okaxis)**

## License

```
   Copyright 2019 Uttam Panchasara

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

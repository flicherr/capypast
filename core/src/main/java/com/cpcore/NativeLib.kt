package com.cpcore

class NativeLib {
    /**
     * A native method that is implemented by the 'cpcore' native library,
     * which is packaged with this application.
     */
//    external fun nativeSaveText(text: String, timestamp: Long)
//    external fun nativeSaveImage(imageData: ByteArray, timestamp: Long)

    companion object {
        // Used to load the 'core' library on application startup.
        init {
            System.loadLibrary("core")
        }
    }
}
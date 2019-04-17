package com.wran.androidpaint

import android.graphics.Path

class FingerPath {

    constructor(color: Int, strokeWidth: Int, path: Path?) {
        this.color = color
        this.strokeWidth = strokeWidth
        this.path = path
    }

    var color: Int = 0
    var strokeWidth: Int = 0
    var path: Path? = null

}

package com.vfi.android.communication

import com.vfi.android.libtools.utils.LogUtil
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
       var a = 0x81;
        var b = 0x7F;
        var c = a and b;
        System.out.println("c=" + c)
    }
}

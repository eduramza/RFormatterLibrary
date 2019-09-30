package com.ramattec.rmaskerformatter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.StringBuilder
import java.lang.ref.WeakReference

/**
 * Created by Eduardo Brandão on 29/09/2019
 *
 **/

enum class DateFormatType{
    PT_BR, YYYY_MM_DD
}

class DateFormatter(weakEditReference: WeakReference<EditText>, formatType: DateFormatType) : TextWatcher{

    companion object{
        const val MAX_LENGTH_PT_BR = 8
    }

    private val mWeakEditText: WeakReference<EditText> = weakEditReference
    private val mFormatType: DateFormatType = formatType
    private var maxLength = MAX_LENGTH_PT_BR

    private var mFormatting: Boolean = false // this is a flag which prevents the stack(onTextChanged)
    private var mClearFlag: Boolean = false
    private var mLastStartLocation: Int = 0
    private var mLastBeforeText: String? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (after == 0){
            mClearFlag = true
        }

        mLastStartLocation = start
        mLastBeforeText = s.toString()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable?) {
        // Make sure to ignore calls to afterTextChanged
        // caused by the work done below
        if (!mFormatting) {
            mFormatting = true
            val curPos = mLastStartLocation
            val beforeValue = mLastBeforeText
            val currentValue = s.toString()
            val formattedValue = formatDate(s)

            if (beforeValue != null) {
                var setCursorPos: Int
                if (currentValue.length > beforeValue.length) {
                    setCursorPos = curPos + (currentValue.length - beforeValue.length)

                    if (formattedValue.length > setCursorPos) {
                        val numbersInsideBrackets = 2
                        if (formattedValue[setCursorPos] == '/' && beforeValue.length == numbersInsideBrackets) {
                            setCursorPos += 3
                        } else if (formattedValue[setCursorPos-1] == '/') {
                            setCursorPos += 2
                        }
                    }
                } else {
                    setCursorPos = curPos - (beforeValue.length - currentValue.length) + 1

                    if (setCursorPos < currentValue.length) {
                        while (setCursorPos > 1 && !Character.isDigit(currentValue.get(setCursorPos-1))) {
                            setCursorPos -= 1
                        }
                    }
                }

                mWeakEditText.get()!!.setSelection(if (setCursorPos < 0) 0 else minOf(setCursorPos, formattedValue.length))
            }
            mFormatting = false
        }
    }

    private fun formatDate(text: Editable?): String{
        val formatBuilder = StringBuilder()

        //remove everything except digits
        var p = 0
        text?.let {
            while (p < it.length){
                val ch = it[p]
                if (!Character.isDigit(ch)){
                    it.delete(p, p + 1)
                } else {
                    p++
                }
            }
        }

        //Now that only digits are remaining
        var allDigitString = text.toString()
        var totalDigits = allDigitString.length

        if (totalDigits > maxLength){
            allDigitString = allDigitString.substring(0, maxLength)
            totalDigits = allDigitString.length
        }

        if (totalDigits == 0 || totalDigits > MAX_LENGTH_PT_BR){
            //May be the total length of input length is greater than the
            //expected value so we'll remove all formating
            text?.clear()
            text?.append(allDigitString)
            return allDigitString
        }

        //For the Day Bar
        var alreadyPlacedDigitCount = 0
        val numbersInsetFirstBar = 2
        if (totalDigits - alreadyPlacedDigitCount > numbersInsetFirstBar){
            formatBuilder
                .append(allDigitString.substring(alreadyPlacedDigitCount,
                    alreadyPlacedDigitCount + numbersInsetFirstBar) + "/"
                )
            alreadyPlacedDigitCount += numbersInsetFirstBar

        }

        //For the Month Bar
        val numberOfMonth = 2
        if (totalDigits - alreadyPlacedDigitCount > numberOfMonth){
            formatBuilder
                .append(allDigitString.substring(alreadyPlacedDigitCount,
                    alreadyPlacedDigitCount + numberOfMonth) + "/")
            alreadyPlacedDigitCount += numberOfMonth
        }

        //All the required formmating is done so we'll just copy the
        //remaining digits.
        if (totalDigits > alreadyPlacedDigitCount){
            formatBuilder.append(allDigitString.substring(alreadyPlacedDigitCount))
        }

        text?.clear()
        text?.append(formatBuilder.toString())
        return formatBuilder.toString()

    }

}
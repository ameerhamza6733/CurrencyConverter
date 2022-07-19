package com.example.myapplication

import java.util.concurrent.TimeUnit

class Utils {
    companion object{
        fun getTimeDifferenceInMints(dateLong:Long?):Long{
            if (dateLong==null){
                return 0
            }else{
                val diffInMilli =   System.currentTimeMillis()-dateLong
                val differInSecond=  TimeUnit.MILLISECONDS.toMinutes(diffInMilli)
                return differInSecond
            }

        }
    }
}
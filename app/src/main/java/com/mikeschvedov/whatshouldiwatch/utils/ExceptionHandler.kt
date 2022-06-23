package com.mikeschvedov.whatshouldiwatch.utils


import retrofit2.HttpException
import java.io.IOException



class ExceptionHandler() {

    companion object{
        fun getExceptionMessage(throwable: Throwable?) : String{
            return when (throwable) {
                is MyException.NoInternetException -> Constants.NO_INTERNET_EXCEPTION
                is MyException.ApiException -> Constants.API_EXCEPTION
                else -> Constants.UNKNOWN_EXCEPTION
            }
        }

        fun mappingExceptions(e: Throwable?): Exception =
            when(e){
                is IOException ->  MyException.NoInternetException()
                is HttpException ->  MyException.ApiException()
                else -> {
                    MyException.UnknownException()
                }
            }

        sealed class MyException(): Exception() {
            class NoInternetException: MyException()
            class ApiException: MyException()
            class UnknownException: MyException()
        }


    }

}
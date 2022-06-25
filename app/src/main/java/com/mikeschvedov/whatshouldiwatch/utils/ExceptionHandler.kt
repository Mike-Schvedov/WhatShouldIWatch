package com.mikeschvedov.whatshouldiwatch.utils


import retrofit2.HttpException
import java.io.IOException



class ExceptionHandler() {

    companion object{
        // Getting a relevant error message according to the exception type
        fun getExceptionMessage(throwable: Throwable?) : String{
            return when (throwable) {
                is MyException.NoInternetException -> Constants.NO_INTERNET_EXCEPTION
                is MyException.ApiException -> Constants.API_EXCEPTION
                else -> Constants.UNKNOWN_EXCEPTION
            }
        }

        // Mapping the exception to become our custom exceptions
        fun mappingExceptions(e: Throwable?): Exception =
            when(e){
                is IOException ->  MyException.NoInternetException()
                is HttpException ->  MyException.ApiException()
                else -> {
                    MyException.UnknownException()
                }
            }

        // Creating our own exceptions
        sealed class MyException(): Exception() {
            class NoInternetException: MyException()
            class ApiException: MyException()
            class UnknownException: MyException()
        }


    }

}
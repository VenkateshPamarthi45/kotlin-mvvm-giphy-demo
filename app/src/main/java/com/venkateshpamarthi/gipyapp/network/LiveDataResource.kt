package com.venkateshpamarthi.gipyapp.network

class LiveDataResource<T>(
    val status: Status, val statusCode: Int, val data: T?,
    val message: String?) {

    /**
     * When response status
     */
    enum class Status
    /**
     * success
     */
    {
        SUCCESS
        /**
         * error
         */
        ,
        ERROR
        /**
         * loading
         */
        ,
        LOADING
    }

    companion object {

        fun <T> success(statusCode: Int, data: T): LiveDataResource<T> {
            return LiveDataResource(Status.SUCCESS, statusCode, data, null)
        }

        fun <T> error(statusCode: Int, msg: String, data: T?): LiveDataResource<T> {
            return LiveDataResource(Status.ERROR, statusCode, data, msg)
        }

        fun <T> loading(data: T?): LiveDataResource<T> {
            return LiveDataResource(Status.LOADING, 0, data, null)
        }
    }
}
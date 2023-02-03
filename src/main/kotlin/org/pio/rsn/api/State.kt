package org.pio.rsn.api

interface State {
    fun <T> request(data: Class<T>, type: String, uuid: String, api: String) : T?
    fun put(data: Any, type: String, uuid: String, api: String) : Boolean
    fun post(data: Any, type: String, uuid: String, api: String) : Boolean
}

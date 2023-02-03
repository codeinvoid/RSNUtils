package org.pio.rsn.api

interface ConfigOperator {
    fun <T> read(config: Class<T>): T
    fun <T> write(config: Class<T>, content: T)
}

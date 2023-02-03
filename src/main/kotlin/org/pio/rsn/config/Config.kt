package org.pio.rsn.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val token : String,
    val api : MutableMap<String, String>
)

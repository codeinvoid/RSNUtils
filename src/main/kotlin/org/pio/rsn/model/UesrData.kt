package org.pio.rsn.model

import kotlinx.serialization.Serializable

@Serializable
data class Integration(val count: Long, val time: Long, val active: Boolean, val nanoid: String)

@Serializable
data class Banned(val active: Boolean, val time: Long, val reason: String, val operator: String, val nanoid: String)

@Serializable
data class Whitelist(val active: Boolean, val time: Long)

@Serializable
data class Valid(val code: Int)

@Serializable
data class Player(val name: String, val id: String)
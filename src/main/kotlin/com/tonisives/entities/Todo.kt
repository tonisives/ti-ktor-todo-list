package com.tonisives.entities

import kotlinx.serialization.Serializable

@Serializable
data class Todo(val id: Int, var title: String, var done: Boolean)
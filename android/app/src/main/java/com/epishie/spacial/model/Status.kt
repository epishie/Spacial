package com.epishie.spacial.model

sealed class Status {
    object Loading : Status()
    data class Loaded(val empty: Boolean) : Status()
    data class Error(val throwable: Throwable) : Status()
}
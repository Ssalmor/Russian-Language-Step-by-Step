package com.langapp.russianlanguage_stepbystep.models

class CollectionItem(
    private val originalKey: String,
    private val displayName: String,
    val sound: String = String(),
    var userInput: String? = null,
    var answer: String? = null
) {

    fun getOriginalKey(): String {
        return originalKey
    }

    fun getDisplayName(): String {
        return displayName
    }
}
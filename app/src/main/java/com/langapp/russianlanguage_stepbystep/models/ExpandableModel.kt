package com.langapp.russianlanguage_stepbystep.models

class ExpandableModel(
    private var nestedList: List<CollectionItem>,
    private var itemText: String,
    var explanationList: List<Int> = listOf(),
    private var isExpandable: Boolean = false,
    var isAnimated: Boolean = false
) {

    fun setExpandable(expandable: Boolean) {
        isExpandable = expandable
    }

    fun getNestedList(): List<CollectionItem> {
        return nestedList
    }

    fun getItemText(): String {
        return itemText
    }

    fun isExpandable(): Boolean {
        return isExpandable
    }

}
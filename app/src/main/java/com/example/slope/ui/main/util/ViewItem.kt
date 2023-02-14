package com.example.slope.ui.main.util

interface ViewItem<T> : Comparable<T> {
    fun areContentsTheSame(other: T): Boolean
    fun areItemsTheSame(other: T): Boolean
}

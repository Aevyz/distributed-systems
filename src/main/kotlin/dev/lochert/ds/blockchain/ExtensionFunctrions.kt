package dev.lochert.ds.blockchain

fun String.isAlphanumeric(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9]+$"))
}


package io.foldright.cffu

infix fun <T, R> (() -> Unit).andThen(f: (T) -> R): (T) -> R = {
    this()
    f(it)
}

infix fun <R> (() -> Unit).andThen(f: () -> R): () -> R = {
    this()
    f()
}

fun <T> merge(list1: List<T>, list2: List<T>) = list1.merge(list2)

@JvmName("mergeExt")
infix fun <T> List<T>.merge(other: List<T>): List<T> = mutableListOf<T>().apply {
    addAll(this@merge)
    addAll(other)
}

@Suppress("NOTHING_TO_INLINE")
inline fun sleep(millis: Long) {
    Thread.sleep(millis)
}

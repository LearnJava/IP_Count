package ru.konstantin


class TestMe {


}

fun main(args: Array<String>) {
    val li = mutableListOf<Int>()
    for ( index in 0 downTo Int.MIN_VALUE) {
        li.add(index)
    }
    println()
}
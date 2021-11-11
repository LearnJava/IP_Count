package ru.konstantin

data class MyIp(val paramList: List<UByte>) : Comparable<MyIp> {

    val first: UByte = paramList[0]
    val second: UByte = paramList[1]
    val third: UByte = paramList[2]
    val fourth: UByte = paramList[3]

    override fun toString(): String {
        return "$first.$second.$third.$fourth"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyIp

        if (first != other.first) return false
        if (second != other.second) return false
        if (third != other.third) return false
        if (fourth != other.fourth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + third.hashCode()
        result = 31 * result + fourth.hashCode()
        return result
    }

    override fun compareTo(other: MyIp): Int {
        if (this.first > other.first) return 1
        if (this.first < other.first) return -1
        if (this.second > other.second) return 1
        if (this.second < other.second) return -1
        if (this.third > other.third) return 1
        if (this.third < other.third) return -1
        if (this.fourth > other.fourth) return 1
        if (this.fourth < other.fourth) return -1
        return 0
    }
}
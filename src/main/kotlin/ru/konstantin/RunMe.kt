package ru.konstantin

import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class RunMe {
    suspend fun getUniqIPs(path: String?) {
        var partOfIpList = mutableListOf<MyIp>()
        val allIpCounter = AtomicLong()
        var counter: Long = 1

        val startDate = Date()
        val jobList = mutableListOf<Job>()

        try {
            val sourceLine = Files.lines(Paths.get(path))
            sourceLine.forEach { ip: String ->
                partOfIpList.add(MyIp(ip.split(".").map { it.toUByte() }))
                if (partOfIpList.size % 10_000_000 == 0) {
                    val tempList = partOfIpList
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        println(tempList.size)
                        addStringToFile(createTempFile("${counter}_${tempList.count()}", "D:\\Temp\\ip_addresses\\test"), tempList)
                    }
                    jobList.add(job)
                    partOfIpList.clear()
//                    println("Всего прочитали IP адресов " + partOfIpList.size)
//                    pathToFile = ru.ru.konstantin.createTempFile(counter, "D:\\Temp\\ip_addresses\\test")
//                    uniqIPs = mutableListOf<MyIp>()
                }
                allIpCounter.getAndIncrement()
                counter++
            }
            println("Всего IP адресов $allIpCounter")
            println("Time left - ${(Date().time - startDate.time) / 1000 / 60}")
            println("Всего IP адресов ${partOfIpList.count()}")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            jobList.joinAll()
        }
    }
}

fun createTempFile(prefix: String, path: String): String {
    val uuid = UUID.randomUUID()
    val randomUUIDString = uuid.toString()
    val file = File("$path/${prefix}_$randomUUIDString.txt")
    file.createNewFile()
    return file.absoluteFile.toString()
}

fun addStringToFile(path: String, ipLists: List<MyIp>) {

    FileWriter(path, false).use { writer ->
        // запись всей строки
        val text = ipLists.joinToString("\n") { it.toString() }
        writer.write(text)
        // запись по символам
        writer.flush()
    }
}

suspend fun main() {
    RunMe().getUniqIPs("D:\\Temp\\ip_addresses\\ip_addresses")
    println("The end")
}

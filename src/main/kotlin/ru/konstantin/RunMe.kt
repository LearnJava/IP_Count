package ru.konstantin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class RunMe {
    fun getUniqIPs(path: String?, ipCountInFile: Int) {
        var partOfIps = mutableListOf<MyIp>()
        val allIpCounter = AtomicLong()
        var counter: Long = 1

        var uniqIpSet = mutableSetOf<MyIp>()

        val startEndParamsInFile = mutableMapOf<String, Pair<MyIp, MyIp>>()

        val startDate = Date().time

        try {
            val sourceLine = Files.lines(Paths.get("$path\\ip_addresses"))
            sourceLine.forEach { ip: String ->
                partOfIps.add(MyIp(ip.split(".").map { it.toUByte() }))
                if (partOfIps.size % ipCountInFile == 0) {
                    var tempList = partOfIps.toList()
                    GlobalScope.launch(Dispatchers.IO) {
                        val fileName = createTempFile(counter, ("$path\\test\\$ipCountInFile"))
                        tempList = tempList.sorted()
                        startEndParamsInFile.put(fileName.substringAfterLast("\\"), Pair(tempList.first(), tempList.last()))
                        println("${fileName.substringAfterLast("\\")} --> ${tempList.first()} - ${tempList.last()}")
                        saveIPsToFile(fileName, tempList)
                    }
//                    println("Всего прочитали IP адресов $allIpCounter")
                    partOfIps.clear()
                }
                allIpCounter.getAndIncrement()
                counter++
            }
            //Дописываем остатки
            if (partOfIps.isNotEmpty()) {
                saveIPsToFile(createTempFile(counter, ("$path\\test\\$ipCountInFile")), partOfIps)
            }
            println("Всего IP адресов $allIpCounter")
            println("Всего IP адресов ${uniqIpSet.count()}")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            println("Whole time is ${Date().time - startDate}")
        }
    }
}

fun createTempFile(prefix: Long, path: String): String {
    val uuid = UUID.randomUUID()
    val randomUUIDString = uuid.toString()
    val file = File("$path/${prefix}_$randomUUIDString.txt")
    file.createNewFile()
    return file.absoluteFile.toString()
}

//@Synchronized
fun saveIPsToFile(path: String, ipLists: List<MyIp>) {
    File(path).printWriter().use { out ->
        out.print(ipLists.joinToString("\n") { it.toString() })
    }


////    try {
//        FileWriter(path, false).use { writer ->
//            // запись всей строки
//            val text = ipLists.joinToString("\n") { it.toString() }
//            writer.write(text)
//            // запись по символам
//            writer.flush()
//        }
////    } catch (ex: IOException) {
////        println(ex.message)
////    }
}

fun main(args: Array<String>) {
    RunMe().getUniqIPs("C:\\kostja\\Temp", args[0].toInt())
//    RunMe().getUniqIPs("C:\\kostja\\Temp", 10_000_000)
    println("The end")
}

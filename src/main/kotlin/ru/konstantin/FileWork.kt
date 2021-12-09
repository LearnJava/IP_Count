package ru.konstantin

import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.streams.toList


class FileWork {

    private val listOf256 = mutableMapOf<String, MutableList<String>>()

//    init {
//        initListOf256()
//    }

    suspend fun getUniqIPs(path: String, countOfPart: Int) {
        var counter: Long = 1
        val startDate = Date().time
        val partOfIps = mutableListOf<String>()

        val jobs = Collections.synchronizedList(mutableListOf<Job>())

        var uniqIpCount = 0L

//        createNeededFiles(path)

        var stopFlag = true

        GlobalScope.launch(Dispatchers.IO) {
            while (stopFlag) {
                try {
                    if (jobs.isNotEmpty()) {
                        val iterator = jobs.iterator()
                        while (iterator.hasNext()) {
                            val myjob = iterator.next()
                            if (myjob.isCompleted) {
                                iterator.remove()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.message
                }
            }
            println("Stopped job-cleaner coroutine")
        }

        try {
            val sourceLine = Files.lines(Paths.get("$path\\ip_addresses"))
            sourceLine.forEach { ip: String ->
                partOfIps.add(ip)
                if (counter % countOfPart == 0L) {
                    pickupIpsByType(partOfIps)
                    val tempList = listOf256.toMap()
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        for ((key, data) in tempList.entries) {
                            val subJob = GlobalScope.launch(Dispatchers.IO) {

                                addStringToFile("$path/test/ip_group_files/$key.txt", "${data.toSet().joinToString("\n")}\n")
                            }
                            jobs.add(subJob)
                        }
                    }

                    println("jobsList = ${jobs.count()}")

                    jobs.add(job)
                    partOfIps.clear()

                    println("Handled $counter ips")
                    println("$counter time is ${Date().time - startDate}")
                    listOf256.clear()
//                    initListOf256()
                }

                counter++
            }

//            val i = InetAddress.getByName(IPString)
//            val intRepresentation: Int = ByteBuffer.wrap(i.address).int
            partOfIps.clear()
            sourceLine.close()

            println("Ждём завершения всех потоков... Time is ${Date().time - startDate}")
            try {
                jobs.joinAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            jobs.clear()

            stopFlag = false

            if (listOf256.isNotEmpty()) {
                for ((key, data) in listOf256.entries) {
                    addStringToFile("$path/test/ip_group_files/$key.txt", data.toSet().joinToString("\n"))
                }
            }

            listOf256.clear()

            val filePathList = getFilePathList("${path}/test/ip_group_files/")
            filePathList.forEach { myFile ->
                println(myFile.toString())

                uniqIpCount += Files.lines(myFile)
                    .filter { it.isNotEmpty() }
                    .toList()
                    .toSet()
                    .count()

                println("$uniqIpCount -> ${Date().time - startDate}")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            println("Whole time is ${Date().time - startDate}")
            println("Count uniq ips is $uniqIpCount")
        }
    }

    private fun addStringToFile(path: String, str: String) {
        File(path).appendText(str)
    }

//    private fun createNeededFiles(pathToFolder: String) {
//        for (index in 0..255) {
//            val file = File("$pathToFolder/test/ip_group_files/$index.txt")
//            file.createNewFile()
//        }
//    }

    private fun pickupIpsByType(partOfIps: MutableList<String>) {
//        val testMap = mutableMapOf<String, MutableList<String>>()
        for (ip in partOfIps) {
            val tl = mutableListOf<String>(ip)
            val key = ip.substringBeforeLast(".").substringBeforeLast(".")
            if (listOf256[key].isNullOrEmpty()) {
                listOf256[key] = tl
            } else {
                listOf256[key]?.addAll(tl)
            }
        }
//        listOf256.putAll(testMap)
    }

//    private fun initListOf256() {
//        for (index in 0..255) {
//            listOf256.add(mutableListOf<String>())
//        }
//    }

    private fun getFilePathList(path: String): Set<Path> {
        return File(path).walkTopDown().filter { it.isFile }.map { it.toPath() }.toSet()
    }
}

fun main(args: Array<String>) = runBlocking {
//    FileWork().getUniqIPs("C:\\kostja\\Temp")
    FileWork().getUniqIPs("D:\\Temp\\ip_addresses", 1_000_000)
//    FileWork().getUniqIPs("D:\\Temp\\ip_addresses", args[0].replace("\\D".toRegex(), "").toInt())
//    RunMe().getUniqIPs("C:\\kostja\\Temp", 10_000_000)
    println("The end")
}

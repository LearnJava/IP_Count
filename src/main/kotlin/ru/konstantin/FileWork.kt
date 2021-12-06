package ru.konstantin

import kotlinx.coroutines.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.streams.toList
import java.io.FileInputStream

import java.io.IOException

import java.io.File
import java.nio.file.Paths
import kotlin.collections.LinkedHashSet


class FileWork {

    private val listOf256 = mutableListOf<MutableList<String>>()

    init {
        initlistOf256()
    }

    suspend fun getUniqIPs(path: String, countOfPart: Int) {
        var counter: Long = 1
        val startDate = Date().time
        val partOfIps = mutableListOf<String>()

        val jobs = mutableListOf<Job>()

        var uniqIpCount = 0L

        createNeededFiles(path)

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                if (jobs.isNotEmpty()) {
                    val iterator = jobs.iterator()
                    for (job in jobs.iterator()){
                        if(job.isCompleted){
                            iterator.remove()
                        }
                    }
                }
            }
        }

        try {
            val sourceLine = Files.lines(Paths.get("$path\\ip_addresses"))
            sourceLine.forEach { ip: String ->
                partOfIps.add(ip)
                if (counter % countOfPart == 0L) {
                    pickupIpsByType(partOfIps)
                    val tempList = listOf256.toList()
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        for ((index, data) in tempList.withIndex()) {
//                            fileStreams[index].appendLine(data.joinToString("\n"))
                            addStringToFile("$path/test/ip_group_files/$index.txt", "${data.toSet().joinToString("\n")}\n")
                        }
                    }

                    jobs.add(job)
                    partOfIps.clear()

                    println("Handled $counter ips")
                    println("$counter time is ${Date().time - startDate}")
                    listOf256.clear()
                    initlistOf256()
                }

                counter++
            }
            if (listOf256.isNotEmpty()) {
                val tempList = listOf256.toList()
                for ((index, data) in tempList.withIndex()) {
                    addStringToFile("$path/test/ip_group_files/$index.txt", data.joinToString("\n"))
                }
            }

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
            println("Ждём завершения всех потоков... Time is ${Date().time - startDate}")
            jobs.joinAll()

            println("Whole time is ${Date().time - startDate}")
            println("Count uniq ips is $uniqIpCount")
        }
    }

    private fun addStringToFile(path: String, str: String) {
        File(path).appendText(str)
    }

    private fun createNeededFiles(pathToFolder: String) {
        for (index in 0..255) {
            val file = File("$pathToFolder/test/ip_group_files/$index.txt")
            file.createNewFile()
        }
    }

    private fun pickupIpsByType(partOfIps: MutableList<String>) {
        for (ip in partOfIps) {
            listOf256[ip.substringBefore(".").toInt()].add(ip)
        }
    }

    private fun initlistOf256() {
        for (index in 0..255) {
            listOf256.add(mutableListOf<String>())
        }
    }

    private fun getFilePathList(path: String): Set<Path> {
        return File(path).walkTopDown().filter { it.isFile }.map { it.toPath() }.toSet()
    }
}

suspend fun main(args: Array<String>) = runBlocking {
//    FileWork().getUniqIPs("C:\\kostja\\Temp")
//    FileWork().getUniqIPs("D:\\Temp\\ip_addresses", args[0].toInt())
    FileWork().getUniqIPs("D:\\Temp\\ip_addresses", 1_000_000)
//    RunMe().getUniqIPs("C:\\kostja\\Temp", 10_000_000)
    println("The end")
}

package ru.konstantin

import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class FileWork {

    suspend fun getUniqIPs(path: String) {
        var counter: Long = 1
        val startDate = Date().time
        val partOfIps = mutableListOf<String>()
//        val ipList = mutableListOf<String>()

        val jobs = mutableListOf<Job>()

//        val fixedThread = newFixedThreadPoolContext(100_000, "myThread----------->")

        createNeededFiles(path)
        var fileStreams = openAllFileStreams("$path/test/ip_group_files/")

        try {
            val sourceLine = Files.lines(Paths.get("$path\\ip_addresses"))
            sourceLine.forEach { ip: String ->
                partOfIps.add(ip)


                if (counter % 1_000_000 == 0L) {
                    for (index in partOfIps) {
                        val job = GlobalScope.launch(Dispatchers.IO) {
                            fileStreams[index.substringBefore(".").toInt()].appendLine(index)
//                    addStringToFile("$path/test/ip_group_files/${ip.substringBefore(".")}.txt", ip)
//                    println("Added to file $ip")
                        }
                        jobs.add(job)
                    }
                    while (jobs.any { !it.isCompleted }) {
                        println("Ждём завершения всех работ -> ${(jobs.filter { !it.isCompleted }).count()}")
                    }
                    jobs.clear()
                    fileStreams.forEach { it.close() }
                    println("Handled $counter ips")
//                    ipList.clear()
                    fileStreams = openAllFileStreams("$path/test/ip_group_files/")
                    println("$counter time is ${Date().time - startDate}")
                }

                counter++
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            jobs.joinAll()

            fileStreams.forEach { it.close() }
            println("Whole time is ${Date().time - startDate}")
        }
    }

    //@Synchronized
    private fun addStringToFile(path: String, ip: String) {
        File(path).appendText("$ip\n")
    }

    private fun createNeededFiles(pathToFolder: String) {
        for (index in 0..255) {
            val file = File("$pathToFolder/test/ip_group_files/$index.txt")
            file.createNewFile()
        }
    }

    fun openAllFileStreams(pathToFolder: String): MutableList<BufferedWriter> {
        val fileStreams = mutableListOf<BufferedWriter>()
        for (index in 0..255) {
            fileStreams.add(File("${pathToFolder}/$index.txt").bufferedWriter())
        }
        return fileStreams
    }
}


suspend fun main(args: Array<String>) = runBlocking {
    FileWork().getUniqIPs("C:\\kostja\\Temp")
//    RunMe().getUniqIPs("C:\\kostja\\Temp", 10_000_000)
    println("The end")
}

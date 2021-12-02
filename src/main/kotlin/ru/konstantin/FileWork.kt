package ru.konstantin

import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class FileWork {

    @OptIn(ObsoleteCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend fun getUniqIPs(path: String) {
        var counter: Long = 1
        val startDate = Date().time

        val ipList = mutableListOf<String>()

        val jobs = mutableListOf<Job>()

        val fixedThread = newFixedThreadPoolContext(1_000, "myThread----------->")

        createNeededFiles(path)
        try {
            val sourceLine = Files.lines(Paths.get("$path\\ip_addresses"))
            sourceLine.forEach { ip: String ->
                ipList.add(ip)

                if (counter % 1_000_000 == 0L) {
                    val tempList = ipList.toList()
                    val job = GlobalScope.launch(fixedThread) {
                        for (oneIp in tempList) {
                            addStringToFile("$path/test/ip_group_files/${oneIp.substringBefore(".")}.txt", oneIp)
//                    println("Added to file $ip")
                        }
                    }
                    println("Handled $counter ips")
                    ipList.clear()
                    jobs.add(job)
                }

                counter++
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            jobs.joinAll()
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
}


suspend fun main(args: Array<String>) = runBlocking {
    FileWork().getUniqIPs("C:\\kostja\\Temp")
//    RunMe().getUniqIPs("C:\\kostja\\Temp", 10_000_000)
    println("The end")
}

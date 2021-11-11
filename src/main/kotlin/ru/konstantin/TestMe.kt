package ru.konstantin

import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors

class TestMe {

    fun compareTwoLists() {

        val fileOne =
            getFileToList("D:\\Temp\\ip_addresses\\test\\10000000_10000000_d7eb964f-36b4-4367-b69e-3d5165c0e04f.txt")
        val fileTwo =
            getFileToList("D:\\Temp\\ip_addresses\\test\\20000000_10000000_16201dbf-dc15-44ca-b93e-99f1743b129d.txt")

        val result = fileOne?.minus(fileTwo)
        println(result)
    }

    fun getFilesFromFolder(absoutePath: String?): List<String>? {
        return Files.walk(Paths.get(absoutePath))
            .filter { path: Path? -> Files.isRegularFile(path) }
            .map { p: Path ->
                p.toString().lowercase(Locale.getDefault())
            }
            .filter { file: String -> file.endsWith("txt") }
            .collect(Collectors.toList())
    }


    fun getFileToList(absolutePath: String): List<String>? {
        return Files.readAllLines(Paths.get(absolutePath))
    }
}

suspend fun main() {
    val testMe = TestMe()
    val files = testMe.getFilesFromFolder("D:\\Temp\\ip_addresses\\test")

//    val jobList = mutableListOf<Job>()

    if (files != null) {
        for ((indexMain, fileMain) in files.withIndex()) {
            var textMainFile: List<String> = testMe.getFileToList(fileMain)!!
            for ((index, file) in files.withIndex()) {
                if (fileMain != file) {
//                    val job = GlobalScope.launch(Dispatchers.IO) {

                    var textFile: List<String> = testMe.getFileToList(file)!!
                    textMainFile = textMainFile.minus(textFile)
                    textFile = textFile.minus(textMainFile)
                    println("$index $file mainFile has ${textFile.count()}")
//                    }
//                    jobList.add(job)
                }
            }
            println(indexMain)
        }
//        jobList.joinAll()
    }

//    TestMe().compareTwoLists()
}
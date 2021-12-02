package ru.konstantin

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors



class TestMe {

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

fun main(args: Array<String>) {
    var ipCountInFile = args[0].toLong().toInt()
    val testMe = TestMe()
    val pathToFolder = "F:\\kostja\\Temp\\test\\$ipCountInFile"
    val files = testMe.getFilesFromFolder(pathToFolder)

    var fileCounter = 0
    val startDate = Date().time
//    val jobList = mutableListOf<Job>()

    if (files != null) {
        for ((indexMain, fileMain) in files.withIndex()) {
            var textMainFile: List<String> = testMe.getFileToList(fileMain)!!
            for ((index, file) in files.withIndex()) {
                if (fileMain != file ) {
                    val textFile: List<String> = testMe.getFileToList(file)!!
                    if (textMainFile.isEmpty()) {
                        println("$fileMain is Empty. No more compares.")
                        break
                    }
                    if (textFile.isNotEmpty()) {
                        textMainFile = textMainFile.minus(textFile)
                        println("$index $file mainFile has ${textMainFile.count()}")
                    } else {
                        println("File $file is empty.")
                    }
                }
            }
            println(indexMain)
            fileCounter += textMainFile.count()
            println("Common counter files = $fileCounter")
            if (textMainFile.count() != ipCountInFile) {
                saveIPsToFile("$pathToFolder\\${fileMain.substringAfterLast("\\")}", textMainFile.map { ip -> MyIp(ip.split(".").map { it.toUByte() }) })
            }
            println()
        }
        println("Whole time is ${Date().time - startDate}")
        println()
    }

//    TestMe().compareTwoLists()
}
package ie.tcd.cs7cs3.wayfinding.service.routing

import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream
import java.util.*

class Shell private constructor() {

    interface ServiceCallbackListener {
        fun PreStart()
        fun StartFailed(reason: String)
        fun Running(port: Int, pid: Int)
        fun OutputStdout(line: String)
        fun OutputStderr(line: String)
        fun PreStop()
        fun PostStop()
    }

    var callbackListener: ServiceCallbackListener? = null
    var process: Process? = null
        private set
    private var outputStream: Deferred<Unit>? = null
    private var errorStream: Deferred<Unit>? = null

    companion object {
        var instance: Shell = Shell()
            private set
    }

    fun shell_stop(): Boolean {
        var wasrunning = false
        if(process != null) {
            callbackListener?.PreStop()
            process!!.destroy()
            runBlocking {
                outputStream?.await()
                errorStream?.await()
            }
            errorStream = null
            outputStream = null
            process = null
            wasrunning = true
        }
        callbackListener?.PostStop()
        return wasrunning
    }

    private fun getPid(p: Process): Int {
        var pid = -1
        try {
            val f = p.javaClass.getDeclaredField("pid")
            f.isAccessible = true
            pid = f.getInt(p)
            f.isAccessible = false
        } catch (e: Throwable) {
            pid = -1
        }
        return pid
    }

    private fun startFailed(reason: String): Boolean {
        callbackListener?.StartFailed(reason)
        return false
    }

    fun shell_start(path: String, env: Map<String, String>): Boolean {
        callbackListener?.PreStart()
        val bin_executable = File(path)
        if (!bin_executable.exists())
            return startFailed("Routing executable file $path does not exist.")
        bin_executable.setReadOnly()
        bin_executable.setExecutable(true)
        val cmd_line = ArrayList<String>()
        cmd_line.add("./" + bin_executable.name)
        var pb = ProcessBuilder()
        pb = pb.directory(bin_executable.parentFile)
        val pb_env = pb.environment()
        pb_env.putAll(env)
        pb = pb.command(cmd_line)
        process = pb.start()
        if (process == null)
            return startFailed("ProcessBuilder returns null on start()")
        val pid = getPid(process!!)
        if (pid==-1)
            return startFailed("pid==-1")
        outputStream = GlobalScope.async(Dispatchers.IO) { callbackListener?.OutputStdout(readStream(process!!.inputStream)) }
        errorStream = GlobalScope.async(Dispatchers.IO) { callbackListener?.OutputStderr(readStream(process!!.errorStream)) }
        //TODO make port configurable
        callbackListener?.Running(9000, pid)
        return true
    }

    private suspend fun readStream(inputStream: InputStream): String {
        val readLines = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            try {
                inputStream.bufferedReader().use { reader ->
                    var line: String?
                    do {
                        line = reader.readLine()
                        if (line != null) {
                            readLines.add(line)
                        }
                    } while (line != null)
                }
            } catch (e: Exception) {
            }
        }
        return readLines.joinToString(System.lineSeparator())
    }

}

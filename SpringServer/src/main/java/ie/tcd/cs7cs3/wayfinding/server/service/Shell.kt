package ie.tcd.cs7cs3.wayfinding.server.service

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.reflect.KFunction1

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

    private val logger = LoggerFactory.getLogger(Shell::class.java)
    var callbackListener: ServiceCallbackListener? = null
    var process: Process? = null
        private set
    private var outputStream: Job? = null
    private var errorStream: Job? = null

    enum class OS {
        WINDOWS, LINUX, MAC, SOLARIS
    }

    companion object {
        var instance: Shell = Shell()
            private set

        fun getOS(): OS? {
            val os = System.getProperty("os.name").toLowerCase()
            return when {
                os.contains("win") -> {
                    OS.WINDOWS
                }
                os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
                    OS.LINUX
                }
                os.contains("mac") -> {
                    OS.MAC
                }
                os.contains("sunos") -> {
                    OS.SOLARIS
                }
                else -> null
            }
        }
    }

    fun shell_stop(): Boolean {
        var wasrunning = false
        if(process != null) {
            callbackListener?.PreStop()
            process!!.destroy()
            runBlocking {
                outputStream?.cancelAndJoin()
                errorStream?.cancelAndJoin()
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
        logger.warn(reason)
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
        logger.info("shell_start: starting new routing server process...")

        val sb_cmdline = StringBuilder()
        if (pb.command() != null) {
            for (i in pb.command().indices) {
                if (i > 0) sb_cmdline.append(" ")
                sb_cmdline.append(pb.command()[i])
            }
        }
        val s_cmdline = sb_cmdline.toString()
        val s_working_dir = pb.directory().canonicalPath
        logger.debug("shell_start: starting routing server process (cmdline is '$s_cmdline' and will run in $s_working_dir)...")

        process = pb.start()
        if (process == null)
            return startFailed("ProcessBuilder returns null on start()")
        val pid = getPid(process!!)
        if (pid==-1)
            return startFailed("pid==-1")
        logger.info("shell_start: started routing server process pid: $pid")

        val outputFun = callbackListener?.let { it::OutputStdout }
        outputStream = getStreamMonitor(process!!.inputStream, outputFun)
        val errorFun = callbackListener?.let { it::OutputStderr }
        errorStream = getStreamMonitor(process!!.errorStream, errorFun)

        //TODO make port configurable
        callbackListener?.Running(9000, pid)
        return true
    }

    private fun getStreamMonitor(inputStream: InputStream, callback: KFunction1<String, Unit>?): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            logger.debug("shell_monitor_coroutine: coroutine started")
            withContext(Dispatchers.IO) {
                try {
                    inputStream.bufferedReader().use { reader ->
                        var line: String?
                        while (isActive) {
                            do {
                                line = reader.readLine()
                                if (line != null) {
                                    logger.info("$line")
                                    callback?.invoke(line)
                                }
                            } while (line != null)
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }
}

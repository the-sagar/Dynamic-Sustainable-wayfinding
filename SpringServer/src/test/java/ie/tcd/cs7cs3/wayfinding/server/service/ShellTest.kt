package ie.tcd.cs7cs3.wayfinding.server.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File

class ShellTest {
    private val logger = LoggerFactory.getLogger(ShellTest::class.java)
    @Test
    fun shellStart() {
        val loader = ShellTest::class.java.classLoader
        val OSName = Shell.getOS()
        var binFile: File? = null
        when(OSName) {
            Shell.OS.MAC -> binFile = File(loader.getResource("native/macos/librouting.so").file)
            Shell.OS.LINUX -> binFile = File(loader.getResource("native/linux_amd64/librouting.so").file)
            Shell.OS.WINDOWS -> binFile = File(loader.getResource("native/windows_amd64/librouting.dll").file)
        }
        assertNotNull(binFile)
        logger.info(binFile!!.path)
        val env = HashMap<String, String>()
        env["OSMROUTEDATADIR"] = File(System.getProperty("user.home"), "route_data").path
        val result = Shell.instance.shell_start(binFile.path, env)
        assertTrue(result)
        logger.info("pid: ${Shell.instance.process!!.pid()}")
        Thread.sleep(5000)
    }

    @Test
    fun shellStop() {
        val result = Shell.instance.shell_stop()
        logger.info("wasrunning: ${result}")
    }
}
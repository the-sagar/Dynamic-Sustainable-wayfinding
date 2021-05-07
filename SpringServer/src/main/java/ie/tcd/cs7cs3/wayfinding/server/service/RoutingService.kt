package ie.tcd.cs7cs3.wayfinding.server.service

import ie.tcd.cs7cs3.wayfinding.server.controller.AuthController
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Service
import java.io.File

//@Service
class RoutingService: DisposableBean {
    private val logger = LoggerFactory.getLogger(RoutingService::class.java)
    val routingShell: Shell = Shell.instance
    constructor() {
        val loader = RoutingService::class.java.classLoader
        val OSName = Shell.getOS()
        var binFile: File? = null
        when(OSName) {
            Shell.OS.MAC -> binFile = File(loader.getResource("native/macos/librouting.so").file)
            Shell.OS.LINUX -> binFile = File(loader.getResource("native/linux_amd64/librouting.so").file)
            Shell.OS.WINDOWS -> binFile = File(loader.getResource("native/windows_amd64/librouting.dll").file)
        }
        logger.debug("librouting path", binFile!!.path)
        val env = HashMap<String, String>()
        env["OSMROUTEDATADIR"] = File(System.getProperty("user.home"), "route_data").path
        val result = routingShell.shell_start(binFile.path, env)
        logger.info("routing backend start result", result)
    }

    override fun destroy() {
        routingShell.shell_stop()
    }

}
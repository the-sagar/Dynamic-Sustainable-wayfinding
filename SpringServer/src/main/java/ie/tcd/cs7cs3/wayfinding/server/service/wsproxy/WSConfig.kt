package ie.tcd.cs7cs3.wayfinding.server.service.wsproxy

import com.github.ji4597056.DefaultWsHandlerRegistration
import com.github.ji4597056.ForwardHandler
import com.github.ji4597056.WsHandlerRegistration
import com.github.ji4597056.server.AbstractWsServerHandler
import com.github.ji4597056.utils.CommonUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration
import org.springframework.web.socket.server.HandshakeInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.socket.WebSocketSession
import java.lang.Exception
import java.io.UnsupportedEncodingException

import com.github.ji4597056.utils.WebsocketConstant
import io.netty.util.CharsetUtil

import org.springframework.web.util.UriUtils
import java.net.URI


@Configuration
@EnableWebSocket
class WSConfig {
    companion object {
        val logger = LoggerFactory.getLogger(WSConfig::class.java)
    }

    @Bean
    @ConditionalOnMissingBean(AbstractWsServerHandler::class)
    fun discoveryForwardHandler(): AbstractWsServerHandler? {
        return object : AbstractWsServerHandler() {
            override fun getForwardUrl(session: WebSocketSession): String {
                return getWsForwardUrl("localhost:9000", session.uri!!)
            }
            private fun getWsForwardUrl(address: String, uri: URI): String {
                return try {
                    val url: String = UriUtils.encodePath(uri.path, CharsetUtil.UTF_8.name())
                    var query: String?
                    if(uri.query == null) {
                        WebsocketConstant.WS_SCHEME + "://" + address + url
                    } else {
                        query = UriUtils.encodeQuery(uri.query, CharsetUtil.UTF_8.name())
                        WebsocketConstant.WS_SCHEME + "://" + address + url + "?" + query
                    }
                } catch (e: UnsupportedEncodingException) {
                    throw IllegalStateException(e)
                }
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(WsHandlerRegistration::class)
    fun wsHandlerRegistration(): WsHandlerRegistration? {
        val registration = DefaultWsHandlerRegistration()
        registration.addInterceptor(HeaderFixInterceptor())
        return registration
    }

    @Configuration
    @AutoConfigureAfter(
        WsHandlerRegistration::class,
        AbstractWsServerHandler::class
    )
    protected class WebsocketConfig : WebSocketConfigurer {
        @Autowired
        private val handlerRegistration: WsHandlerRegistration? = null

        @Autowired(required = false)
        private val defaultInterceptors: List<HandshakeInterceptor>? = null

        @Autowired
        private val defaultHandler: AbstractWsServerHandler? = null

        override fun registerWebSocketHandlers(webSocketHandlerRegistry: WebSocketHandlerRegistry) {
            registryHandler(webSocketHandlerRegistry, object : ForwardHandler() {
                init {
                    id="routing"
                    prefix="/rpc.RouteService"
                    uri="/**"
                    interceptorClasses=arrayOf("ie.tcd.cs7cs3.wayfinding.server.service.wsproxy.HeaderFixInterceptor")
                }
            });
        }

        /**
         * register handler
         *
         * @param registry WebSocketHandlerRegistry
         * @param handler ForwardHandler
         */
        private fun registryHandler(registry: WebSocketHandlerRegistry, handler: ForwardHandler) {
            val registration = getRegistration(registry, handler)
            // set allowedOrigins
            if (handler.allowedOrigins == null) {
                registration.setAllowedOrigins("*")
            } else {
                registration.setAllowedOrigins(*handler.allowedOrigins)
            }
            // set interceptors
            val interceptorClasses: Array<String>? = handler.interceptorClasses
            if (interceptorClasses != null) {
                val interceptors = interceptorClasses
                    .map { className -> handlerRegistration!!.getInterceptor(className) }
                    .toTypedArray()
                registration.addInterceptors(*interceptors)
            } else {
                if (defaultInterceptors != null) {
                    registration.addInterceptors(
                        *defaultInterceptors
                            .toTypedArray()
                    )
                }
            }
            // set withSocketJs
            if (handler.isWithJs) {
                registration.withSockJS()
            }
        }

        /**
         * get WebSocketHandlerRegistration
         *
         * @param registry WebSocketHandlerRegistry
         * @param handler ForwardHandler
         * @return WebSocketHandlerRegistration
         */
        private fun getRegistration(
            registry: WebSocketHandlerRegistry,
            handler: ForwardHandler
        ): WebSocketHandlerRegistration {
            // set handler
            val className: String? = handler.handlerClass
            return if (className == null) {
                registry.addHandler(defaultHandler!!, CommonUtils.getWsPattern(handler))
            } else {
                try {
                    registry
                        .addHandler(
                            handlerRegistration!!.getHandler(handler.handlerClass),
                            CommonUtils.getWsPattern(handler)
                        )
                } catch (e: Exception) {
                    logger.error("Set webosocket handler error!error: {}", e)
                    throw IllegalArgumentException("Set websocket handler error!")
                }
            }
        }
    }
}
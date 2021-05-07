package ie.tcd.cs7cs3.wayfinding.server

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.lang.Nullable
import org.springframework.web.servlet.resource.ResourceResolver
import org.springframework.web.servlet.resource.ResourceResolverChain

import java.io.IOException
import javax.servlet.http.HttpServletRequest

@Configuration
@EnableWebMvc
class WebConfig: WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val resolver: ResourceResolver = ReactResourceResolver()
        registry.addResourceHandler("/admin/**", "/admin")
            .resourceChain(true)
            .addResolver(resolver)
    }

    class ReactResourceResolver : ResourceResolver {
        private val index: Resource = ClassPathResource(REACT_DIR + "index.html")
        private val rootStaticFiles: List<String> = listOf(
            "favicon.io",
            "asset-manifest.json", "manifest.json", "service-worker.js"
        )

        override fun resolveResource(
            @Nullable request: HttpServletRequest?,
            requestPath: String,
            locations: MutableList<out Resource>,
            p3: ResourceResolverChain
        ): Resource? {
            return resolve(requestPath, locations)
        }

        override fun resolveUrlPath(
            resourcePath: String,
            locations: MutableList<out Resource>,
            p2: ResourceResolverChain
        ): String? {
            val resolvedResource: Resource = resolve(resourcePath, locations) ?: return null
            return try {
                resolvedResource.url.toString()
            } catch (e: IOException) {
                resolvedResource.filename
            }
        }

        private fun resolve(
            requestPath: String?, locations: List<Resource?>
        ): Resource? {
            if (requestPath == null) return null
            return if (rootStaticFiles.contains(requestPath)
                || requestPath.startsWith(REACT_STATIC_DIR)
            ) {
                ClassPathResource(REACT_DIR + requestPath)
            } else index
        }

        companion object {
            // root dir of react files
            // example REACT_DIR/index.html
            private const val REACT_DIR = "/build/"

            // this is directory inside REACT_DIR for react static files
            // example REACT_DIR/REACT_STATIC_DIR/js/
            // example REACT_DIR/REACT_STATIC_DIR/css/
            private const val REACT_STATIC_DIR = "static"
        }
    }
}
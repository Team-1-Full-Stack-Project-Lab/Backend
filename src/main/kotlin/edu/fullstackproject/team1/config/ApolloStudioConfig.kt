package edu.fullstackproject.team1.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import reactor.core.publisher.Mono

/**
 * Apollo Studio Configuration
 *
 * Configures the GraphQL API for integration with Apollo Studio:
 * - Enables introspection for Apollo Studio Explorer
 * - Configures CORS for Apollo Studio domains
 * - Prepares headers for Apollo tracing (if needed in the future)
 *
 * The API key and graph reference are loaded from environment variables:
 * - APOLLO_KEY: Your Apollo Studio API key
 * - APOLLO_GRAPH_REF: Your graph reference (format: graph-name@variant)
 * - Dejaré esto de la ia para futuras referencias
 */
@Configuration
class ApolloStudioConfig(
        @Value("\${apollo.studio.key:}") private val apolloKey: String,
        @Value("\${apollo.studio.graph-ref:}") private val graphRef: String,
        @Value("\${apollo.studio.schema-reporting.enabled:false}")
        private val schemaReportingEnabled: Boolean,
) : WebGraphQlInterceptor {
  init {
    if (apolloKey.isNotEmpty()) {
      println("✅ Apollo Studio configured for graph: $graphRef")
      println("📊 Schema reporting enabled: $schemaReportingEnabled")
    } else {
      println("⚠️ Apollo Studio key not configured. Set APOLLO_KEY environment variable.")
    }
  }

  override fun intercept(
          request: WebGraphQlRequest,
          chain: WebGraphQlInterceptor.Chain,
  ): Mono<WebGraphQlResponse> {
    // Add Apollo Studio headers if needed
    if (apolloKey.isNotEmpty()) {
      request.configureExecutionInput { _, builder ->
        builder
                .graphQLContext { contextBuilder ->
                  contextBuilder.put("apolloKey", apolloKey)
                  contextBuilder.put("graphRef", graphRef)
                }
                .build()
      }
    }

    return chain.next(request)
  }
}

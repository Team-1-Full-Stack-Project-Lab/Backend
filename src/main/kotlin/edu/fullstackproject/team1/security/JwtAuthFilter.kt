package edu.fullstackproject.team1.security

import edu.fullstackproject.team1.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
	private val jwtService: JwtService,
	private val userDetailsService: UserDetailsService
): OncePerRequestFilter() {
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		val authHeader = request.getHeader("Authorization")
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response)
			return
		}

         try {
             val token = authHeader.substring(7)
             val username = jwtService.extractUsername(token)

             if (SecurityContextHolder.getContext().authentication == null) {
                 val userDetails = userDetailsService.loadUserByUsername(username)

                 if (jwtService.validateToken(token, userDetails)) {
                     val authToken = UsernamePasswordAuthenticationToken(
						 userDetails,
						 null,
						 userDetails.authorities
					 )
                     authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                     SecurityContextHolder.getContext().authentication = authToken
                 }
             }
         } catch (e: Exception) {
             logger.error("Error processing JWT token: ${e.message}")
         }

        filterChain.doFilter(request, response)
	}
}

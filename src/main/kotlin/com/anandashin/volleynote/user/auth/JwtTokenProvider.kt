package com.anandashin.volleynote.user.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.Date

object JwtTokenProvider{
    private val key = System.getenv("JWT_SECRET_KEY")?.let{
        Keys.hmacShaKeyFor(it.toByteArray(StandardCharsets.UTF_8))
    } ?: throw IllegalStateException("JWT_SECRET_KEY is not set!")
    private val expiration = System.getenv("JWT_EXPIRATION")?.toLong()
        ?: (1000 * 60 * 60 * 2) // 2 hrs

    fun createJwtToken(userId: Long): String =
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

    fun validateToken(token: String): Boolean = try {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        true
    } catch (e: Exception) {
        false
    }

    fun getUserIdFromToken(token: String): Long =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject.toLong()

    fun validateTokenAndGetUserId(token: String): Long? {
        return try {
            val claims =
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            if (claims.expiration.before(Date())) return null else claims.subject.toLong()
        } catch (e: Exception) {
            println("JWT token validation FAILED")
            null
        }
    }
}

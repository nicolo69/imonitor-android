package com.cerotek.imonitor.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Mock Interceptor per testare l'app senza backend reale
 * 
 * Abilita/disabilita modificando MockInterceptor.ENABLED
 */
class MockInterceptor : Interceptor {
    
    companion object {
        // Cambia a true per abilitare il mock
        var ENABLED = true  // ← CAMBIATO DA false A true
        
        // Se true, accetta qualsiasi credenziale (per debug)
        var ACCEPT_ANY_CREDENTIALS = true
        
        // Credenziali mock (usate solo se ACCEPT_ANY_CREDENTIALS = false)
        private const val MOCK_USERNAME = "Testdemo0"
        private const val MOCK_PASSWORD = "Testdemo0"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        
        // Log per debug
        android.util.Log.d("MockInterceptor", "ENABLED: $ENABLED, Path: $path")
        
        if (!ENABLED) {
            return chain.proceed(request)
        }
        
        return when {
            // Mock Login - accetta qualsiasi endpoint con "login" o "signin"
            path.contains("login", ignoreCase = true) || 
            path.contains("signin", ignoreCase = true) -> {
                android.util.Log.d("MockInterceptor", "Intercepting login request")
                mockLoginResponse(chain, request)
            }
            
            // Mock Measurements
            path.contains("/measurements") -> {
                android.util.Log.d("MockInterceptor", "Intercepting measurements request")
                mockMeasurementsResponse(chain)
            }
            
            // Mock Assistee
            path.contains("/assistees") -> {
                android.util.Log.d("MockInterceptor", "Intercepting assistee request")
                mockAssisteeResponse(chain)
            }
            
            // Default: passa al server reale
            else -> {
                android.util.Log.d("MockInterceptor", "Passing through to real server")
                chain.proceed(request)
            }
        }
    }
    
    private fun mockLoginResponse(chain: Interceptor.Chain, request: okhttp3.Request): Response {
        // Simula un piccolo delay
        Thread.sleep(500)
        
        // Leggi il body della richiesta per verificare le credenziali
        val requestBody = request.body
        val buffer = okio.Buffer()
        requestBody?.writeTo(buffer)
        val bodyString = buffer.readUtf8()
        
        android.util.Log.d("MockInterceptor", "Request body: $bodyString")
        
        // Verifica credenziali
        val isValidCredentials = if (ACCEPT_ANY_CREDENTIALS) {
            android.util.Log.d("MockInterceptor", "ACCEPT_ANY_CREDENTIALS=true, accepting login")
            true
        } else {
            bodyString.contains(MOCK_USERNAME, ignoreCase = true) && 
            bodyString.contains(MOCK_PASSWORD, ignoreCase = true)
        }
        
        android.util.Log.d("MockInterceptor", "Valid credentials: $isValidCredentials")
        
        return if (isValidCredentials) {
            // Login SUCCESS
            val mockResponse = """
                {
                    "token": "mock_jwt_token_${System.currentTimeMillis()}",
                    "user": {
                        "id": "mock_user_123",
                        "username": "MockUser",
                        "email": "test@mock.com",
                        "firstName": "Andrea",
                        "lastName": "",
                        "age": 65,
                        "pathology": "Ipertensione"
                    }
                }
            """.trimIndent()
            
            android.util.Log.d("MockInterceptor", "Returning SUCCESS response")
            
            Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(mockResponse.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        } else {
            // Login FAILED
            val errorResponse = """
                {
                    "error": "Invalid credentials",
                    "message": "Username or password incorrect. Expected: $MOCK_USERNAME/$MOCK_PASSWORD"
                }
            """.trimIndent()
            
            android.util.Log.d("MockInterceptor", "Returning FAILED response")
            
            Response.Builder()
                .code(401)
                .message("Unauthorized")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(errorResponse.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        }
    }
    
    private fun mockMeasurementsResponse(chain: Interceptor.Chain): Response {
        Thread.sleep(300)
        
        val mockResponse = """
            {
                "success": true,
                "message": "Measurements saved successfully"
            }
        """.trimIndent()
        
        return Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(chain.request())
            .body(mockResponse.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
    
    private fun mockAssisteeResponse(chain: Interceptor.Chain): Response {
        Thread.sleep(300)
        
        val mockResponse = """
            {
                "id": "mock_assistee_456",
                "name": "Test Patient",
                "age": 65,
                "gender": "M"
            }
        """.trimIndent()
        
        return Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(chain.request())
            .body(mockResponse.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}

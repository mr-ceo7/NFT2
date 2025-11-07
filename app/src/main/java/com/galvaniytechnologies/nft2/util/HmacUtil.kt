package com.galvaniytechnologies.nft2.util

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacUtil {
    private const val HMAC_ALGORITHM = "HmacSHA256"
    private const val SECRET_KEY = "your-experimental-secret"

    fun generateHmac(data: String): String {
        val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(), HMAC_ALGORITHM)
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(secretKeySpec)
        val hmacBytes = mac.doFinal(data.toByteArray())
        return Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
    }
}
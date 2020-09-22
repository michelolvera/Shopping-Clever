package dev.michelolvera.shoppingclever.utils

import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class KeyHashUtils {

    fun generateKeyHash (packageManager: PackageManager): String? {
        try {
            val info = packageManager.getPackageInfo(
                "dev.michelolvera.shoppingclever",  //Insert your own package name.
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", keyHash)
                return keyHash
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
        return null
    }

}
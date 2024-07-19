package com.x8bit.bitwarden.data.autofill.fido2.util

import android.content.Intent
import android.content.pm.SigningInfo
import android.os.Build
import android.os.Parcelable
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.provider.CallingAppInfo
import androidx.credentials.provider.PendingIntentHandler
import com.x8bit.bitwarden.data.autofill.fido2.model.Fido2CredentialRequest
import com.x8bit.bitwarden.data.platform.annotation.OmitFromCoverage
import com.x8bit.bitwarden.data.platform.util.isBuildVersionBelow
import com.x8bit.bitwarden.ui.platform.manager.intent.EXTRA_KEY_CIPHER_ID
import com.x8bit.bitwarden.ui.platform.manager.intent.EXTRA_KEY_CREDENTIAL_ID
import com.x8bit.bitwarden.ui.platform.manager.intent.EXTRA_KEY_USER_ID
import kotlinx.parcelize.Parcelize

/**
 * Checks if this [Intent] contains a [Fido2CredentialRequest] related to an ongoing FIDO 2
 * credential creation process.
 */
@OmitFromCoverage
fun Intent.getFido2CredentialRequestOrNull(): Fido2CredentialRequest? {
    if (isBuildVersionBelow(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) return null

    val systemRequest = PendingIntentHandler.retrieveProviderCreateCredentialRequest(this)
        ?: return null

    val createPublicKeyRequest =
        systemRequest.callingRequest as? CreatePublicKeyCredentialRequest
            ?: return null

    val userId = getStringExtra(EXTRA_KEY_USER_ID)
        ?: return null

    return Fido2CredentialRequest(
        userId = userId,
        requestJson = createPublicKeyRequest.requestJson,
        packageName = systemRequest.callingAppInfo.packageName,
        signingInfo = systemRequest.callingAppInfo.signingInfo,
        origin = systemRequest.callingAppInfo.origin,
    )
}

fun Intent.getFido2AssertionRequestOrNull(): Fido2CredentialAssertionRequest? {
    if (isBuildVersionBelow(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)) return null

    val systemRequest = PendingIntentHandler.retrieveProviderGetCredentialRequest(this)
        ?: return null

    val option = systemRequest
        .credentialOptions
        .firstOrNull { it.type == PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL }
        as? GetPublicKeyCredentialOption
        ?: return null

    val credentialId = getStringExtra(EXTRA_KEY_CREDENTIAL_ID)
        ?: return null

    val cipherId = getStringExtra(EXTRA_KEY_CIPHER_ID)

    return Fido2CredentialAssertionRequest(
        cipherId = cipherId,
        credentialId = credentialId,
        requestJson = option.requestJson,
        clientDataHash = option.clientDataHash,
        packageName = systemRequest.callingAppInfo.packageName,
        signingInfo = systemRequest.callingAppInfo.signingInfo,
        origin = systemRequest.callingAppInfo.origin,
    )
}

@Parcelize
data class Fido2CredentialAssertionRequest(
    val cipherId: String?,
    val credentialId: String,
    val requestJson: String,
    val clientDataHash: ByteArray?,
    val packageName: String,
    val signingInfo: SigningInfo,
    val origin: String?,
) : Parcelable {
    val callingAppInfo: CallingAppInfo
        get() = CallingAppInfo(packageName, signingInfo, origin)
}

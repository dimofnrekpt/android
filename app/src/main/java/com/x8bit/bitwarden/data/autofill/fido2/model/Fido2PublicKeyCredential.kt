package com.x8bit.bitwarden.data.autofill.fido2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fido2PublicKeyCredential(
    @SerialName("id")
    val id: String,
    @SerialName("rawId")
    val rawId: String,
    @SerialName("type")
    val type: String,
    @SerialName("authenticatorAttachment")
    val authenticatorAttachment: String,
    @SerialName("response")
    val response: Fido2AssertionResponse,
    @SerialName("clientExtensionResults")
    val clientExtensionResults: ClientExtensionResults,
) {

    @Serializable
    data class Fido2AssertionResponse(
        @SerialName("clientDataJSON")
        val clientDataJson: String?,
        @SerialName("authenticatorData")
        val authenticatorData: String,
        @SerialName("signature")
        val signature: String,
        @SerialName("userHandle")
        val userHandle: String?,
    )

    @Serializable
    data class ClientExtensionResults(
        @SerialName("credProps")
        val credentialProperties: CredentialProperties,
    ) {
        @Serializable
        data class CredentialProperties(
            @SerialName("rk")
            val residentKey: Boolean?,
        )
    }
}

package com.x8bit.bitwarden.data.autofill.fido2.model

sealed class Fido2AuthenticateCredentialResult {

    data class Success(val responseJson: String) : Fido2AuthenticateCredentialResult()

    data object Error : Fido2AuthenticateCredentialResult()

    data object Cancelled : Fido2AuthenticateCredentialResult()
}

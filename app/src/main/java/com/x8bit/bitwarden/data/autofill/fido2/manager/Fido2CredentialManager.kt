package com.x8bit.bitwarden.data.autofill.fido2.manager

import com.bitwarden.vault.CipherView
import com.x8bit.bitwarden.data.autofill.fido2.model.Fido2AuthenticateCredentialResult
import com.x8bit.bitwarden.data.autofill.fido2.model.Fido2CredentialRequest
import com.x8bit.bitwarden.data.autofill.fido2.model.Fido2RegisterCredentialResult
import com.x8bit.bitwarden.data.autofill.fido2.model.Fido2ValidateOriginResult
import com.x8bit.bitwarden.data.autofill.fido2.model.PublicKeyCredentialCreationOptions
import com.x8bit.bitwarden.data.autofill.fido2.util.Fido2CredentialAssertionRequest

/**
 * Responsible for managing FIDO 2 credential registration and authentication.
 */
interface Fido2CredentialManager {

    /**
     * Returns true when the user has performed an explicit verification action. E.g., biometric
     * verification, device credential verification, or vault unlock.
     */
    var isUserVerified: Boolean

    /**
     * Attempt to validate the RP and origin of the provided [fido2CredentialRequest].
     */
    suspend fun validateOrigin(
        fido2CredentialRequest: Fido2CredentialRequest,
    ): Fido2ValidateOriginResult

    /**
     * Attempt to extract FIDO 2 passkey creation options from the system [requestJson], or null.
     */
    fun getPasskeyCreateOptionsOrNull(
        requestJson: String,
    ): PublicKeyCredentialCreationOptions?

    /**
     * Register a new FIDO 2 credential to a users vault.
     */
    suspend fun registerFido2Credential(
        userId: String,
        fido2CredentialRequest: Fido2CredentialRequest,
        selectedCipherView: CipherView,
    ): Fido2RegisterCredentialResult

    /**
     * Authenticate a FIDO credential against a cipher in the users vault.
     */
    suspend fun authenticateFido2Credential(
        userId: String,
        request: Fido2CredentialAssertionRequest,
        selectedCipherView: CipherView,
    ): Fido2AuthenticateCredentialResult
}

package com.x8bit.bitwarden.data.auth.datasource.sdk

import com.bitwarden.core.AuthRequestResponse
import com.bitwarden.core.MasterPasswordPolicyOptions
import com.bitwarden.core.RegisterKeyResponse
import com.bitwarden.crypto.HashPurpose
import com.bitwarden.crypto.Kdf
import com.bitwarden.crypto.TrustDeviceResponse
import com.x8bit.bitwarden.data.auth.datasource.sdk.model.PasswordStrength

/**
 * Source of authentication information and functionality from the Bitwarden SDK.
 */
interface AuthSdkSource {
    /**
     * Gets the data to authenticate with trusted device encryption.
     */
    suspend fun getTrustDevice(): Result<TrustDeviceResponse>

    /**
     * Gets the data needed to create a new auth request.
     */
    suspend fun getNewAuthRequest(
        email: String,
    ): Result<AuthRequestResponse>

    /**
     * Gets the fingerprint phrase for this [email] and [publicKey].
     */
    suspend fun getUserFingerprint(
        email: String,
        publicKey: String,
    ): Result<String>

    /**
     * Creates a hashed password provided the given [email], [password], [kdf], and [purpose].
     */
    suspend fun hashPassword(
        email: String,
        password: String,
        kdf: Kdf,
        purpose: HashPurpose,
    ): Result<String>

    /**
     * Creates a set of encryption key information for registration.
     */
    suspend fun makeRegisterKeys(
        email: String,
        password: String,
        kdf: Kdf,
    ): Result<RegisterKeyResponse>

    /**
     * Checks the password strength for the given [email] and [password] combination, along with
     * some [additionalInputs].
     */
    suspend fun passwordStrength(
        email: String,
        password: String,
        additionalInputs: List<String> = emptyList(),
    ): Result<PasswordStrength>

    /**
     * Checks that the given [password] with the given [passwordStrength] satisfies the given
     * [policy]. Returns `true` if so and `false` otherwise.
     */
    suspend fun satisfiesPolicy(
        password: String,
        passwordStrength: PasswordStrength,
        policy: MasterPasswordPolicyOptions,
    ): Result<Boolean>
}

package com.x8bit.bitwarden.ui.vault.feature.addedit.util

import com.x8bit.bitwarden.data.autofill.fido2.model.PublicKeyCredentialCreationOptions
import com.x8bit.bitwarden.data.autofill.fido2.model.PublicKeyCredentialDescriptor

/**
 * Returns a mock FIDO 2 [PublicKeyCredentialCreationOptions] object to simulate a credential
 * creation request.
 */
@Suppress("MaxLineLength")
fun createMockPublicKeyCredentialCreationOptions(
    number: Int,
    userVerificationRequirement: PublicKeyCredentialCreationOptions.AuthenticatorSelectionCriteria.UserVerificationRequirement? = null,
) = PublicKeyCredentialCreationOptions(
    authenticatorSelection = PublicKeyCredentialCreationOptions
        .AuthenticatorSelectionCriteria(userVerification = userVerificationRequirement),
    challenge = "mockPublicKeyCredentialCreationOptionsChallenge-$number",
    excludeCredentials = listOf(
        PublicKeyCredentialDescriptor(
            type = "mockPublicKeyCredentialDescriptorType-$number",
            id = "mockPublicKeyCredentialDescriptorId-$number",
            transports = listOf("mockPublicKeyCredentialDescriptorTransports-$number"),
        ),
    ),
    pubKeyCredParams = listOf(
        PublicKeyCredentialCreationOptions.PublicKeyCredentialParameters(
            type = "PublicKeyCredentialParametersType-$number",
            alg = number.toLong(),
        ),
    ),
    relyingParty = PublicKeyCredentialCreationOptions.PublicKeyCredentialRpEntity(
        name = "mockPublicKeyCredentialRpEntityName-$number",
        id = "mockPublicKeyCredentialRpEntity-$number",
    ),
    user = PublicKeyCredentialCreationOptions.PublicKeyCredentialUserEntity(
        name = "mockPublicKeyCredentialUserEntityName-$number",
        id = "mockPublicKeyCredentialUserEntityId-$number",
        displayName = "mockPublicKeyCredentialUserEntityDisplayName-$number",
    ),
)

// dictionary PublicKeyCredentialRequestOptions {
//    required BufferSource                challenge;
//    unsigned long                        timeout;
//    USVString                            rpId;
//    sequence<PublicKeyCredentialDescriptor> allowCredentials = [];
//    DOMString                            userVerification = "preferred";
//    AuthenticationExtensionsClientInputs extensions;
//};

// dictionary PublicKeyCredentialCreationOptions {
//    required PublicKeyCredentialRpEntity         rp;
//    required PublicKeyCredentialUserEntity       user;
//
//    required BufferSource                             challenge;
//    required sequence<PublicKeyCredentialParameters>  pubKeyCredParams;
//
//    unsigned long                                timeout;
//    sequence<PublicKeyCredentialDescriptor>      excludeCredentials = [];
//    AuthenticatorSelectionCriteria               authenticatorSelection;
//    DOMString                                    attestation = "none";
//    AuthenticationExtensionsClientInputs         extensions;
//};

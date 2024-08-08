package com.x8bit.bitwarden.ui.auth.feature.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.x8bit.bitwarden.ui.auth.feature.createaccount.createAccountDestination
import com.x8bit.bitwarden.ui.auth.feature.createaccount.navigateToCreateAccount
import com.x8bit.bitwarden.ui.auth.feature.enterprisesignon.enterpriseSignOnDestination
import com.x8bit.bitwarden.ui.auth.feature.enterprisesignon.navigateToEnterpriseSignOn
import com.x8bit.bitwarden.ui.auth.feature.environment.environmentDestination
import com.x8bit.bitwarden.ui.auth.feature.environment.navigateToEnvironment
import com.x8bit.bitwarden.ui.auth.feature.landing.LANDING_ROUTE
import com.x8bit.bitwarden.ui.auth.feature.landing.landingDestination
import com.x8bit.bitwarden.ui.auth.feature.landing.navigateToLanding
import com.x8bit.bitwarden.ui.auth.feature.login.loginDestination
import com.x8bit.bitwarden.ui.auth.feature.login.navigateToLogin
import com.x8bit.bitwarden.ui.auth.feature.loginwithdevice.loginWithDeviceDestination
import com.x8bit.bitwarden.ui.auth.feature.loginwithdevice.model.LoginWithDeviceType
import com.x8bit.bitwarden.ui.auth.feature.loginwithdevice.navigateToLoginWithDevice
import com.x8bit.bitwarden.ui.auth.feature.masterpasswordhint.masterPasswordHintDestination
import com.x8bit.bitwarden.ui.auth.feature.masterpasswordhint.navigateToMasterPasswordHint
import com.x8bit.bitwarden.ui.auth.feature.setpassword.navigateToSetPassword
import com.x8bit.bitwarden.ui.auth.feature.setpassword.setPasswordDestination
import com.x8bit.bitwarden.ui.auth.feature.startregistration.navigateToStartRegistration
import com.x8bit.bitwarden.ui.auth.feature.startregistration.startRegistrationDestination
import com.x8bit.bitwarden.ui.auth.feature.twofactorlogin.navigateToTwoFactorLogin
import com.x8bit.bitwarden.ui.auth.feature.twofactorlogin.twoFactorLoginDestination
import com.x8bit.bitwarden.ui.auth.feature.welcome.welcomeDestination

const val AUTH_GRAPH_ROUTE: String = "auth_graph"

/**
 * Add auth destinations to the nav graph.
 */
@Suppress("LongMethod")
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = LANDING_ROUTE,
        route = AUTH_GRAPH_ROUTE,
    ) {
        createAccountDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToLogin = { emailAddress, captchaToken ->
                navController.navigateToLogin(
                    emailAddress = emailAddress,
                    captchaToken = captchaToken,
                    navOptions = navOptions {
                        popUpTo(LANDING_ROUTE)
                    },
                )
            },
        )
        startRegistrationDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCompleteRegistration = { emailAddress, verificationToken ->
                // TODO ADD NAVIGATION TO COMPLETE REGISTRATION
//                navController.navigateToCompleteRegistration(
//                    emailAddress = emailAddress,
//                    verificationToken = verificationToken,
//                    fromEmail = false
//                )
            },
            onNavigateToCheckEmail = { emailAddress ->
                // TODO ADD NAVIGATION TO CHECK EMAIL
                // navController.navigateToCheckEmail(emailAddress)
            },
            onNavigateToEnvironment = { navController.navigateToEnvironment() },
        )
        enterpriseSignOnDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToSetPassword = { navController.navigateToSetPassword() },
            onNavigateToTwoFactorLogin = { emailAddress ->
                navController.navigateToTwoFactorLogin(
                    emailAddress = emailAddress,
                    password = null,
                )
            },
        )
        setPasswordDestination()
        landingDestination(
            onNavigateToCreateAccount = { navController.navigateToCreateAccount() },
            onNavigateToLogin = { emailAddress ->
                navController.navigateToLogin(
                    emailAddress = emailAddress,
                    captchaToken = null,
                )
            },
            onNavigateToEnvironment = {
                navController.navigateToEnvironment()
            },
            onNavigateToStartRegistration = { navController.navigateToStartRegistration() },
        )
        welcomeDestination(
            onNavigateToCreateAccount = { navController.navigateToCreateAccount() },
            onNavigateToLogin = { navController.navigateToLanding() },
        )
        loginDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToMasterPasswordHint = { emailAddress ->
                navController.navigateToMasterPasswordHint(
                    emailAddress = emailAddress,
                )
            },
            onNavigateToEnterpriseSignOn = { emailAddress ->
                navController.navigateToEnterpriseSignOn(
                    emailAddress = emailAddress,
                )
            },
            onNavigateToLoginWithDevice = { emailAddress ->
                navController.navigateToLoginWithDevice(
                    emailAddress = emailAddress,
                    loginType = LoginWithDeviceType.OTHER_DEVICE,
                )
            },
            onNavigateToTwoFactorLogin = { emailAddress, password ->
                navController.navigateToTwoFactorLogin(
                    emailAddress = emailAddress,
                    password = password,
                )
            },
        )
        loginWithDeviceDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToTwoFactorLogin = {
                navController.navigateToTwoFactorLogin(
                    emailAddress = it,
                    password = null,
                )
            },
        )
        environmentDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        masterPasswordHintDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        twoFactorLoginDestination(
            onNavigateBack = { navController.popBackStack() },
        )
    }
}

/**
 * Navigate to the auth screen. Note this will only work if auth destination was added
 * via [authGraph].
 */
fun NavController.navigateToAuthGraph(
    navOptions: NavOptions? = null,
) {
    navigate(AUTH_GRAPH_ROUTE, navOptions)
}

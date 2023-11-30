package com.x8bit.bitwarden.ui.vault.feature.vault

import app.cash.turbine.test
import com.x8bit.bitwarden.data.auth.repository.AuthRepository
import com.x8bit.bitwarden.data.auth.repository.model.UserState
import com.x8bit.bitwarden.data.auth.repository.model.UserState.SpecialCircumstance
import com.x8bit.bitwarden.data.platform.repository.model.DataState
import com.x8bit.bitwarden.data.vault.datasource.sdk.model.createMockCipherView
import com.x8bit.bitwarden.data.vault.datasource.sdk.model.createMockFolderView
import com.x8bit.bitwarden.data.vault.repository.VaultRepository
import com.x8bit.bitwarden.data.vault.repository.model.VaultData
import com.x8bit.bitwarden.ui.platform.base.BaseViewModelTest
import com.x8bit.bitwarden.ui.platform.base.util.asText
import com.x8bit.bitwarden.ui.platform.components.model.AccountSummary
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VaultViewModelTest : BaseViewModelTest() {

    private val mutableUserStateFlow =
        MutableStateFlow<UserState?>(DEFAULT_USER_STATE)

    private val mutableVaultDataStateFlow =
        MutableStateFlow<DataState<VaultData>>(DataState.Loading)

    private val authRepository: AuthRepository =
        mockk {
            every { userStateFlow } returns mutableUserStateFlow
            every { specialCircumstance } returns null
            every { specialCircumstance = any() } just runs
        }

    private val vaultRepository: VaultRepository =
        mockk {
            every { vaultDataStateFlow } returns mutableVaultDataStateFlow
            every { sync() } returns Unit
        }

    @Test
    fun `initial state should be correct`() {
        val viewModel = createViewModel()
        assertEquals(DEFAULT_STATE, viewModel.stateFlow.value)
    }

    @Test
    fun `UserState updates with a null value should do nothing`() {
        val viewModel = createViewModel()
        assertEquals(DEFAULT_STATE, viewModel.stateFlow.value)

        mutableUserStateFlow.value = null

        assertEquals(DEFAULT_STATE, viewModel.stateFlow.value)
    }

    @Test
    fun `UserState updates with a non-null value update the account information in the state`() {
        val viewModel = createViewModel()
        assertEquals(DEFAULT_STATE, viewModel.stateFlow.value)

        mutableUserStateFlow.value = DEFAULT_USER_STATE.copy(
            accounts = listOf(
                UserState.Account(
                    userId = "activeUserId",
                    name = "Other User",
                    email = "active@bitwarden.com",
                    avatarColorHex = "#00aaaa",
                    isPremium = true,
                    isVaultUnlocked = true,
                ),
            ),
        )

        assertEquals(
            DEFAULT_STATE.copy(
                avatarColorString = "#00aaaa",
                initials = "OU",
                accountSummaries = listOf(
                    AccountSummary(
                        userId = "activeUserId",
                        name = "Other User",
                        email = "active@bitwarden.com",
                        avatarColorHex = "#00aaaa",
                        status = AccountSummary.Status.ACTIVE,
                    ),
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `on AccountSwitchClick for the active account should do nothing`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(
                VaultAction.AccountSwitchClick(
                    accountSummary = mockk {
                        every { status } returns AccountSummary.Status.ACTIVE
                    },
                ),
            )
            expectNoEvents()
        }
    }

    @Test
    fun `on AccountSwitchClick for a locked account emit NavigateToVaultUnlockScreen`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(
                VaultAction.AccountSwitchClick(
                    accountSummary = mockk {
                        every { status } returns AccountSummary.Status.LOCKED
                    },
                ),
            )
            assertEquals(VaultEvent.NavigateToVaultUnlockScreen, awaitItem())
        }
    }

    @Test
    fun `on AccountSwitchClick for an unlocked account emit ShowToast`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(
                VaultAction.AccountSwitchClick(
                    accountSummary = mockk {
                        every { status } returns AccountSummary.Status.UNLOCKED
                    },
                ),
            )
            assertEquals(VaultEvent.ShowToast("Not yet implemented."), awaitItem())
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `on AddAccountClick should update the SpecialCircumstance of the AuthRepository to PendingAccountAddition`() {
        val viewModel = createViewModel()
        viewModel.trySendAction(VaultAction.AddAccountClick)
        verify {
            authRepository.specialCircumstance = SpecialCircumstance.PendingAccountAddition
        }
    }

    @Test
    fun `vaultDataStateFlow Loaded with items should update state to Content`() = runTest {
        mutableVaultDataStateFlow.tryEmit(
            value = DataState.Loaded(
                data = VaultData(
                    cipherViewList = listOf(createMockCipherView(number = 1)),
                    folderViewList = listOf(createMockFolderView(number = 1)),
                ),
            ),
        )

        val viewModel = createViewModel()

        assertEquals(
            createMockVaultState(
                viewState = VaultState.ViewState.Content(
                    loginItemsCount = 1,
                    cardItemsCount = 0,
                    identityItemsCount = 0,
                    secureNoteItemsCount = 0,
                    favoriteItems = listOf(),
                    folderItems = listOf(
                        VaultState.ViewState.FolderItem(
                            id = "mockId-1",
                            name = "mockName-1".asText(),
                            itemCount = 1,
                        ),
                    ),
                    noFolderItems = listOf(),
                    trashItemsCount = 0,
                ),
            ),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `vaultDataStateFlow Loaded with empty items should update state to NoItems`() = runTest {
        mutableVaultDataStateFlow.tryEmit(
            value = DataState.Loaded(
                data = VaultData(
                    cipherViewList = emptyList(),
                    folderViewList = emptyList(),
                ),
            ),
        )
        val viewModel = createViewModel()
        assertEquals(
            createMockVaultState(viewState = VaultState.ViewState.NoItems),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `vaultDataStateFlow Loading should update state to Loading`() = runTest {
        mutableVaultDataStateFlow.tryEmit(value = DataState.Loading)

        val viewModel = createViewModel()

        assertEquals(
            createMockVaultState(viewState = VaultState.ViewState.Loading),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `vaultDataStateFlow Error should show toast and update state to NoItems`() = runTest {
        mutableVaultDataStateFlow.tryEmit(
            value = DataState.Error(
                error = IllegalStateException(),
            ),
        )

        val viewModel = createViewModel()

        viewModel.eventFlow.test {
            assertEquals(
                VaultEvent.ShowToast("Vault error state not yet implemented"),
                awaitItem(),
            )
        }
        assertEquals(
            createMockVaultState(viewState = VaultState.ViewState.NoItems),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `vaultDataStateFlow NoNetwork should show toast and update state to NoItems`() = runTest {
        mutableVaultDataStateFlow.tryEmit(
            value = DataState.NoNetwork(),
        )

        val viewModel = createViewModel()

        viewModel.eventFlow.test {
            assertEquals(
                VaultEvent.ShowToast("Vault no network state not yet implemented"),
                awaitItem(),
            )
        }
        assertEquals(
            createMockVaultState(viewState = VaultState.ViewState.NoItems),
            viewModel.stateFlow.value,
        )
    }

    @Test
    fun `AddItemClick should emit NavigateToAddItemScreen`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.AddItemClick)
            assertEquals(VaultEvent.NavigateToAddItemScreen, awaitItem())
        }
    }

    @Test
    fun `CardGroupClick should emit NavigateToCardGroup`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.CardGroupClick)
            assertEquals(VaultEvent.NavigateToCardGroup, awaitItem())
        }
    }

    @Test
    fun `FolderClick should emit NavigateToFolder with correct folder ID`() = runTest {
        val viewModel = createViewModel()
        val folderId = "12345"
        val folder = mockk<VaultState.ViewState.FolderItem> {
            every { id } returns folderId
        }
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.FolderClick(folder))
            assertEquals(VaultEvent.NavigateToFolder(folderId), awaitItem())
        }
    }

    @Test
    fun `IdentityGroupClick should emit NavigateToIdentityGroup`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.IdentityGroupClick)
            assertEquals(VaultEvent.NavigateToIdentityGroup, awaitItem())
        }
    }

    @Test
    fun `LoginGroupClick should emit NavigateToLoginGroup`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.LoginGroupClick)
            assertEquals(VaultEvent.NavigateToLoginGroup, awaitItem())
        }
    }

    @Test
    fun `SearchIconClick should emit NavigateToVaultSearchScreen`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.SearchIconClick)
            assertEquals(VaultEvent.NavigateToVaultSearchScreen, awaitItem())
        }
    }

    @Test
    fun `SecureNoteGroupClick should emit NavigateToSecureNotesGroup`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.SecureNoteGroupClick)
            assertEquals(VaultEvent.NavigateToSecureNotesGroup, awaitItem())
        }
    }

    @Test
    fun `TrashClick should emit NavigateToTrash`() = runTest {
        val viewModel = createViewModel()
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.TrashClick)
            assertEquals(VaultEvent.NavigateToTrash, awaitItem())
        }
    }

    @Test
    fun `VaultItemClick should emit NavigateToVaultItem with the correct item ID`() = runTest {
        val viewModel = createViewModel()
        val itemId = "54321"
        val item = mockk<VaultState.ViewState.VaultItem> {
            every { id } returns itemId
        }
        viewModel.eventFlow.test {
            viewModel.trySendAction(VaultAction.VaultItemClick(item))
            assertEquals(VaultEvent.NavigateToVaultItem(itemId), awaitItem())
        }
    }

    private fun createViewModel(): VaultViewModel =
        VaultViewModel(
            authRepository = authRepository,
            vaultRepository = vaultRepository,
        )
}

private val DEFAULT_STATE: VaultState =
    createMockVaultState(viewState = VaultState.ViewState.Loading)

private val DEFAULT_USER_STATE = UserState(
    activeUserId = "activeUserId",
    accounts = listOf(
        UserState.Account(
            userId = "activeUserId",
            name = "Active User",
            email = "active@bitwarden.com",
            avatarColorHex = "#aa00aa",
            isPremium = true,
            isVaultUnlocked = true,
        ),
    ),
)

private fun createMockVaultState(viewState: VaultState.ViewState): VaultState =
    VaultState(
        avatarColorString = "#aa00aa",
        initials = "AU",
        accountSummaries = listOf(
            AccountSummary(
                userId = "activeUserId",
                name = "Active User",
                email = "active@bitwarden.com",
                avatarColorHex = "#aa00aa",
                status = AccountSummary.Status.ACTIVE,
            ),
        ),
        viewState = viewState,
    )

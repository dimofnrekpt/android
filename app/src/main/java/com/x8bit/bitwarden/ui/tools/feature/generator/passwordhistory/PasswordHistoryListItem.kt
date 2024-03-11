package com.x8bit.bitwarden.ui.tools.feature.generator.passwordhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.ui.platform.base.util.withLineBreaksAtWidth
import com.x8bit.bitwarden.ui.platform.base.util.withVisualTransformation
import com.x8bit.bitwarden.ui.platform.components.util.nonLetterColorVisualTransformation
import com.x8bit.bitwarden.ui.platform.theme.BitwardenTheme
import com.x8bit.bitwarden.ui.platform.theme.LocalNonMaterialTypography

/**
 * A composable function for displaying a password history list item.
 *
 * @param label The primary text to be displayed in the list item.
 * @param supportingLabel A secondary text displayed below the primary label.
 * @param onCopyClick The lambda function to be invoked when the list items icon is clicked.
 * @param modifier The [Modifier] to be applied to the list item.
 */
@Composable
fun PasswordHistoryListItem(
    label: String,
    supportingLabel: String,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            var widthPx by remember(label) { mutableIntStateOf(0) }
            val textStyle = LocalNonMaterialTypography.current.sensitiveInfoMedium
            val formattedText = label.withLineBreaksAtWidth(
                widthPx = widthPx.toFloat(),
                monospacedTextStyle = textStyle,
            )
            Text(
                text = formattedText.withVisualTransformation(
                    visualTransformation = nonLetterColorVisualTransformation(),
                ),
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .semantics { testTag = "GeneratedPasswordValue" }
                    .fillMaxWidth()
                    .onGloballyPositioned { widthPx = it.size.width },
            )

            Text(
                text = supportingLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics { testTag = "GeneratedPasswordDateLabel" },
            )
        }

        IconButton(
            onClick = onCopyClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.semantics { testTag = "CopyPasswordValueButton" },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_copy),
                contentDescription = stringResource(id = R.string.copy),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordHistoryListItem_preview() {
    BitwardenTheme {
        PasswordHistoryListItem(
            label = "8gr6uY8CLYQwzr#",
            supportingLabel = "8/24/2023 11:07 AM",
            onCopyClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

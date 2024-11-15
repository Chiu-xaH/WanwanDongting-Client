package com.chiuxah.wanwandongting.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chiuxah.wanwandongting.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LittleDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String? = null,
    conformtext : String = stringResource(id = R.string.ok),
    dismisstext : String = stringResource(id = R.string.cancel)
) {
        AlertDialog(
            // icon = { Icon(icon, contentDescription = "Example Icon") },
            title = { Text(text = dialogTitle) },
            text = { dialogText?.let { Text(text = it) } },
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                Button(onClick = { onConfirmation() }
                ) { Text(conformtext) }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { onDismissRequest() }
                ) { Text(dismisstext) }
            },
        )
}

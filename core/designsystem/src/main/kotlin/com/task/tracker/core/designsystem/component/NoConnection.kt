package com.task.tracker.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.task.tracker.core.designsystem.R
import com.task.tracker.core.designsystem.theme.Theme

@Composable
fun NoConnection(isOffline: Boolean) {
    AnimatedVisibility(
        visible = isOffline,
        exit = shrinkOut(shrinkTowards = Alignment.TopStart) + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.errorContainer),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.no_connection),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoConnectionLightPreview() {
    Theme(darkTheme = false) {
        NoConnection(isOffline = true)
    }
}

@Preview(showBackground = true)
@Composable
fun NoConnectionDarkPreview() {
    Theme(darkTheme = true) {
        NoConnection(isOffline = true)
    }
}

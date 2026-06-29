package com.task.tracker.feature.task.impl.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.task.tracker.core.designsystem.component.Loader
import com.task.tracker.core.designsystem.icon.Icon
import com.task.tracker.core.designsystem.theme.Ratios
import com.task.tracker.core.designsystem.theme.Theme
import com.task.tracker.core.designsystem.theme.dimensions
import com.task.tracker.core.ui.utils.UiText
import com.task.tracker.core.ui.utils.getTimeInStringDefault
import com.task.tracker.feature.task.impl.R
import com.task.tracker.feature.task.impl.presentation.tasks.model.UiTask
import com.task.tracker.feature.task.impl.presentation.tasks.model.UiTaskStatus
import kotlin.time.Instant

@Composable
internal fun TaskScreen(
    showSnackBar: suspend (String) -> Boolean,
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TaskEffect.ShowToast -> showSnackBar(effect.message)
            }
        }
    }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.handle(action = TaskAction.RefreshTasks) },
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.dimensions.spacingSm)
        ) {
            items(
                count = state.tasks.size,
                key = { index -> state.tasks[index].id }
            ) { index ->
                val task = state.tasks[index]
                TaskCard(
                    task = task,
                    statuses = state.statuses,
                    onTaskClicked = { id, status, updatedAt ->
                        viewModel.handle(
                            action = TaskAction.OnTaskClicked(
                                id = id,
                                status = status,
                                updatedAt = updatedAt,
                            )
                        )
                    },
                    onTaskSyncClicked = { id, updatedAt ->
                        viewModel.handle(
                            action = TaskAction.OnTaskSyncClicked(
                                id = id,
                                updatedAt = updatedAt,
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: UiTask,
    statuses: List<UiTaskStatus>,
    onTaskClicked: (id: Long, status: UiTaskStatus, updatedAt: Instant) -> Unit,
    onTaskSyncClicked: (id: Long, updatedAt: Instant) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = Ratios.CARD4X3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimensions.spacingMd)
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = task.title.stringValue(),
                maxLines = MAX_ROWS_COUNT,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.weight(weight = 1f))

            TaskStatuses(
                id = task.id,
                isSyncRequired = task.isSyncRequired,
                isSyncing = task.isSyncing,
                statuses = statuses,
                status = task.status,
                updatedAt = task.updatedAt,
                onTaskClicked = onTaskClicked,
                onTaskSyncClicked = onTaskSyncClicked
            )
        }
    }
}

@Composable
private fun TaskStatuses(
    id: Long,
    isSyncRequired: Boolean,
    isSyncing: Boolean,
    statuses: List<UiTaskStatus>,
    status: UiTaskStatus,
    updatedAt: Instant,
    onTaskClicked: (id: Long, status: UiTaskStatus, updatedAt: Instant) -> Unit,
    onTaskSyncClicked: (id: Long, updatedAt: Instant) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimensions.spacingXs),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                statuses.forEach {
                    val isCurrent = status == it
                    AssistChip(
                        onClick = { onTaskClicked(id, it, updatedAt) },
                        label = {
                            Text(
                                text = stringResource(id = it.label),
                                color = if (isCurrent) it.labelColor else Color.Unspecified,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isCurrent) it.color else Color.Unspecified,
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(MaterialTheme.dimensions.spacingXs))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                isSyncing -> Loader(
                    modifier = Modifier.size(size = MaterialTheme.dimensions.spacingLg)
                )

                isSyncRequired -> {
                    Button(
                        onClick = { onTaskSyncClicked(id, updatedAt) }
                    ) {
                        Text(
                            text = stringResource(id = R.string.sync_indicator),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                else -> {
                    Icon(
                        imageVector = Icon.doneAllFilled,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.weight(weight = 1f))
            Text(
                text = updatedAt.getTimeInStringDefault(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LightTaskCardPreview() {
    Theme(darkTheme = false) {
        TaskCard(
            task = UiTask(
                id = -1L,
                title = UiText.Value("Some text"),
                status = UiTaskStatus.TODO,
                isSyncing = false,
                isSyncRequired = false,
                updatedAt = Instant.fromEpochMilliseconds(1782422166000L)
            ),
            statuses = UiTaskStatus.entries,
            onTaskClicked = { _, _, _ -> },
            onTaskSyncClicked = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DarkTaskCardPreview() {
    Theme(darkTheme = true) {
        TaskCard(
            task = UiTask(
                id = -1L,
                title = UiText.Value("Some text"),
                status = UiTaskStatus.DONE,
                isSyncing = false,
                isSyncRequired = false,
                updatedAt = Instant.fromEpochMilliseconds(1782422166000L)
            ),
            statuses = UiTaskStatus.entries,
            onTaskClicked = { _, _, _ -> },
            onTaskSyncClicked = { _, _ -> },
        )
    }
}

private const val MAX_ROWS_COUNT = 3

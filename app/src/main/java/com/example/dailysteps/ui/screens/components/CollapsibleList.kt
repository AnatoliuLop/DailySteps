package com.example.dailysteps.ui.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier


@Composable
fun <T> CollapsibleList(
    items: List<T>,
    threshold: Int = 4,
    modifier: Modifier = Modifier,
    collapseLabel: String = "",
    listState: LazyListState = rememberLazyListState(),
    itemContent: @Composable (T) -> Unit
) {
    var expanded by rememberSaveable  { mutableStateOf(false) }
    val visibleItems = if (!expanded && items.size > threshold) items.take(threshold) else items

    LazyColumn(modifier = modifier) {
        items(visibleItems, key = { it.hashCode() }) { it ->
            itemContent(it)
        }
        if (!expanded && items.size > threshold) {
            item {
                TextButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("$collapseLabel (${items.size})")
                }
            }
        }
    }
}
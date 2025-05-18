package com.example.dailysteps.ui.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Показывает первые [threshold] элементов списка [items],
 * если их больше — выводит кнопку «Показать все (N)» внизу,
 * при клике разворачивает весь список.
 *
 * @param items        исходный список
 * @param threshold    сколько первых элементов показывать до разворачивания
 * @param modifier     модификатор для LazyColumn
 * @param collapseLabel текст на кнопке (по умолчанию «Показать все»)
 * @param itemContent  @Composable-ленда для рисования одного элемента
 */
@Composable
fun <T> CollapsibleList(
    items: List<T>,
    threshold: Int = 4,
    modifier: Modifier = Modifier,
    collapseLabel: String = "Показать все",
    itemContent: @Composable (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
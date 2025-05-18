// app/src/main/java/com/example/dailysteps/domain/usecase/daynote/GetDayNoteUseCase.kt
package com.example.dailysteps.domain.usecase.daynote

import com.example.dailysteps.data.model.DailyDayNote
import com.example.dailysteps.data.preferences.PreferencesManager
import com.example.dailysteps.data.repository.DailyDayNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetDayNoteUseCase(
    private val repo: DailyDayNoteRepository,
    private val prefs: PreferencesManager
) {
    private val fmt = DateTimeFormatter.ISO_DATE

    /** Теперь берет дату из prefs.lastDate, а не из LocalDate.now() */
    operator fun invoke(dateIso: String? = null): Flow<DailyDayNote?> {
        return if (dateIso != null) {
            repo.getNote(dateIso)
        } else {
            prefs.lastDate
                .map { iso -> iso.takeIf(String::isNotBlank) ?: LocalDate.now().format(fmt) }
                .flatMapLatest { iso -> repo.getNote(iso) }
        }
    }
}
package com.studentmanagement.data

data class StudentQuery(
    val search: String = "",
    val sort: SortOption = SortOption.NAME_ASC,
    val filter: StudentFilter = StudentFilter.All
) {

    val isDefault: Boolean
        get() = search.isBlank() && sort == SortOption.NAME_ASC && filter is StudentFilter.All
}

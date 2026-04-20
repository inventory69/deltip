package dev.dettmer.deltip.model

data class MultiRow(
    val id: Long,
    val input: String = "",
    val result: CalculationResult? = null,
)

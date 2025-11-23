package com.ahmadshahwaiz.kotlinruleengine.model

// Rule model
data class RuleModel(
    val name: String,
    val description: String,
    val conditions: ConditionModel,
    val cases: List<Map<String, Any>>? = null   // NEW iOS format
)


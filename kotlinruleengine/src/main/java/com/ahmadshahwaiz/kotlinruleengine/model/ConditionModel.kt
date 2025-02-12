package com.ahmadshahwaiz.kotlinruleengine.model;

import com.google.gson.JsonElement

// Condition model
data class ConditionModel(
        val path: String,
        val value: JsonElement,
        val operator: String,
        val all: List<ConditionModel>? = null,
        val any: List<ConditionModel>? = null
)

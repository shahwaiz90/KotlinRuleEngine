package com.ahmadshahwaiz.kotlinruleengine

import com.ahmadshahwaiz.kotlinruleengine.model.ConditionModel
import com.ahmadshahwaiz.kotlinruleengine.model.Operator
import com.ahmadshahwaiz.kotlinruleengine.model.RuleModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.TestOnly


// Rule Engine
class RuleEngine(private val rules: List<RuleModel>) {

    fun evaluate(data: Map<String, Any>): List<RuleModel> {
        return rules.filter { evaluateCondition(it.conditions, data) }
    }

    // Evaluate a specific rule by name
    fun evaluateRule(ruleName: String, data: Map<String, Any>): Boolean {
        val rule = rules.find { it.name == ruleName } ?: return false
        return evaluateCondition(rule.conditions, data)
    }

    private fun evaluateCondition(condition: ConditionModel, data: Map<String, Any>): Boolean {
        return when {
            condition.all != null -> condition.all.all { evaluateCondition(it, data) }
            condition.any != null -> condition.any.any { evaluateCondition(it, data) }
            else -> evaluateSingleCondition(condition, data)
        }
    }

    private fun evaluateSingleCondition(condition: ConditionModel, data: Map<String, Any>): Boolean {
        val valueFromData = extractValueFromPath(condition.path, data) ?: return false
        val conditionValue = condition.value.asString
        val operator = Operator.valueOf(condition.operator.uppercase())

        return when (operator) {
            Operator.EQUAL -> valueFromData == conditionValue
            Operator.NOT_EQUAL -> valueFromData != conditionValue
            Operator.CONTAINS -> valueFromData.contains(conditionValue)
            Operator.NOT_CONTAINS -> !valueFromData.contains(conditionValue)
            Operator.GREATER_THAN -> valueFromData.toDoubleOrNull()?.let { it > conditionValue.toDouble() } ?: false
            Operator.LESS_THAN -> valueFromData.toDoubleOrNull()?.let { it < conditionValue.toDouble() } ?: false
            Operator.STARTS_WITH -> valueFromData.startsWith(conditionValue)
            Operator.ENDS_WITH -> valueFromData.endsWith(conditionValue)
        }
    }

    private fun extractValueFromPath(path: String, data: Map<String, Any>): String? {
        val keys = path.removePrefix("$.").split(".")
        var current: Any? = data

        for (key in keys) {
            if (current is Map<*, *>) {
                current = current[key]
            } else return null
        }
        return current?.toString()
    }
}

// Load rules from JSON
fun loadRulesFromJson(json: String): List<RuleModel> {
    val ruleType = object : TypeToken<List<RuleModel>>() {}.type
    return Gson().fromJson(json, ruleType)
}

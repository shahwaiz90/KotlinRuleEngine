package com.ahmadshahwaiz.kotlinruleengine

import com.ahmadshahwaiz.kotlinruleengine.model.ConditionModel
import com.ahmadshahwaiz.kotlinruleengine.model.Operator
import com.ahmadshahwaiz.kotlinruleengine.model.RuleModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * RuleEngine is responsible for evaluating business rules against provided data.
 *
 * This engine processes a list of predefined rules and determines whether they apply
 * based on conditions specified in JSON format. It supports multiple condition types
 * such as "all", "any", and various comparison operators.
 *
 * @param rules List of rules to be evaluated.
 */
class RuleEngine(private val rules: List<RuleModel>) {

    /**
     * Evaluates all rules against the provided data and returns a list of matching rules.
     *
     * @param data The input data to be evaluated against the rules.
     * @return A list of rules that match the given data.
     */
    fun evaluate(data: Map<String, Any>): List<RuleModel> {
        return rules.filter { evaluateCondition(it.conditions, data) }
    }

    /**
     * Evaluates a specific rule by its name.
     *
     * @param ruleName The name of the rule to evaluate.
     * @param data The input data for evaluation.
     * @return `true` if the rule matches the data, otherwise `false`.
     */
    fun evaluateRule(ruleName: String, data: Map<String, Any>): Boolean {
        val rule = rules.find { it.name == ruleName } ?: return false
        return evaluateCondition(rule.conditions, data)
    }

    /**
     * Recursively evaluates a condition by checking "all" or "any" conditions.
     *
     * - "all" means all subconditions must be true.
     * - "any" means at least one subcondition must be true.
     *
     * If no "all" or "any" is present, it evaluates a single condition.
     *
     * @param condition The condition to evaluate.
     * @param data The input data used for evaluation.
     * @return `true` if the condition is met, otherwise `false`.
     */
    private fun evaluateCondition(condition: ConditionModel, data: Map<String, Any>): Boolean {
        return when {
            condition.all != null -> condition.all.all { evaluateCondition(it, data) }
            condition.any != null -> condition.any.any { evaluateCondition(it, data) }
            else -> evaluateSingleCondition(condition, data)
        }
    }

    /**
     * Evaluates a single condition using the specified operator.
     *
     * This function extracts the value from the given data using the provided path
     * and applies the specified comparison operation.
     *
     * @param condition The condition to evaluate.
     * @param data The input data for evaluation.
     * @return `true` if the condition matches the data, otherwise `false`.
     */
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

    /**
     * Extracts a value from the provided data map based on a JSONPath-like syntax.
     *
     * Example:
     * ```
     * val data = mapOf("product" to mapOf("category" to "Addons"))
     * extractValueFromPath("$.product.category", data) // returns "Addons"
     * ```
     *
     * @param path The JSON-like path to retrieve the value (e.g., `$.product.category`).
     * @param data The input data map.
     * @return The extracted value as a string, or `null` if the path is invalid.
     */
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

/**
 * Parses JSON rules into a list of RuleModel objects.
 *
 * @param json The JSON string containing rule definitions.
 * @return A list of parsed RuleModel objects.
 */
fun loadRulesFromJson(json: String): List<RuleModel> {
    val ruleType = object : TypeToken<List<RuleModel>>() {}.type
    return Gson().fromJson(json, ruleType)
}

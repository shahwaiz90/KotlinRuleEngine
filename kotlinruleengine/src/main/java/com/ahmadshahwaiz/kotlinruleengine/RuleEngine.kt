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
     * Evaluates a rule using the iOS-style "cases" format and returns the result
     * of the first case whose `when` block evaluates to `true`.
     *
     * This function mirrors the behavior of the Swift implementation:
     * `MBRuleEngine.shared.firstMatchResult(ruleName:objects:)`
     *
     * ### How It Works
     * - Locates the rule by `ruleName`
     * - Reads its `cases` array, where each case contains:
     *   - a `"when"` block defining matching conditions
     *   - a `"result"` block containing the output for that case
     * - For each case in order:
     *   - Converts the `"when"` JSON into a temporary `ConditionModel`
     *   - Builds a temporary `RuleModel` containing only that condition
     *   - Evaluates the condition using a throwaway `RuleEngine`
     *   - Returns the `"result"` of the first matching case
     *
     * ### Behavior
     * - **First-match-wins strategy**: evaluation stops as soon as a case matches
     * - Returns `null` if:
     *   - the rule name is not found
     *   - no cases are present
     *   - none of the cases match
     *
     * ### Parameters
     * @param ruleName  The name of the rule to evaluate
     * @param data      The merged evaluation data (context + product maps)
     *
     * @return The `"result"` map of the first matching case, or `null` if none match
     */
    fun firstMatchResult(ruleName: String, data: Map<String, Any>): Map<String, Any> {
        val rule = rules.find { it.name == ruleName } ?: return mapOf("actions" to emptyList<Any>())
        val cases = rule.cases ?: return mapOf("actions" to emptyList<Any>())

        for (caseMap in cases) {
            val whenMap = caseMap["when"] as? Map<String, Any> ?: continue

            val whenJson = Gson().toJsonTree(whenMap)
            val whenCondition = Gson().fromJson(whenJson, ConditionModel::class.java)

            if (evaluateCondition(whenCondition, data)) {
                return caseMap["result"] as? Map<String, Any> ?: mapOf("actions" to emptyList<Any>())
            }
        }

        // iOS behavior: return empty actions if no case matched
        return mapOf("actions" to emptyList<Any>())
    }

    fun extractActions(result: Map<String, Any>?): List<Map<String, String>> {
        return (result?.get("actions") as? List<Map<String, String>>) ?: emptyList()
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
     * Evaluates a single leaf-level condition (i.e., a condition containing
     * a path, value, and operator) against the provided data map.
     *
     * This method supports comparison of Boolean, numeric, and string
     * values by intelligently coercing both the extracted data value
     * and the expected JSON value into their appropriate Kotlin types.
     *
     * ## Behavior
     * 1. Extracts the actual runtime value from the data using the JSON path
     *    defined in the condition (e.g., `"$.context.isPrepaid"`).
     *
     * 2. Converts the extracted value into one of:
     *    - `Boolean` (when `"true"` / `"false"`)
     *    - `Double`  (when numeric)
     *    - `String`  (fallback)
     *
     * 3. Converts the expected value (from the JSON rule) into:
     *    - `Boolean`
     *    - `Double`
     *    - `String`
     *    depending on the JSON type.
     *
     * 4. Applies the comparison operator (`equal`, `not_equal`, `greater_than`,
     *    `contains`, etc.) using the correctly coerced types.
     *
     * ## Supported Operators
     * - `EQUAL` / `NOT_EQUAL`          → Boolean, Number, or String equality
     * - `GREATER_THAN` / `LESS_THAN`  → Numeric comparison
     * - `CONTAINS` / `NOT_CONTAINS`   → String containment
     * - `STARTS_WITH` / `ENDS_WITH`   → String prefix/suffix matching
     *
     * ## Error Handling
     * - Returns `false` if:
     *   - The path does not exist
     *   - The condition value is null
     *   - The operator is missing or invalid
     *   - Type coercion fails (e.g., numeric comparison on non-numeric data)
     *
     * ## Example
     * For a condition:
     * ```
     * { "path": "$.context.isPrepaid", "value": true, "operator": "equal" }
     * ```
     * and data:
     * ```
     * { "context": { "isPrepaid": true } }
     * ```
     * this method returns `true`.
     *
     * @param condition The single condition node containing path, value, and operator.
     * @param data The evaluation data map (context + product) used to extract actual values.
     * @return `true` if the condition is satisfied, otherwise `false`.
     */

    private fun evaluateSingleCondition(condition: ConditionModel, data: Map<String, Any>): Boolean {
        val rawValue = extractValueFromPath(condition.path, data)
        val operator = Operator.valueOf(condition.operator.uppercase())

        // iOS behavior: missing value →
        // EQUAL → false
        // NOT_EQUAL → true
        if (rawValue == null) {
            return when (operator) {
                Operator.EQUAL -> false
                Operator.NOT_EQUAL -> true
                else -> false  // other operators can't work with null
            }
        }
        val expectedJson = condition.value

        val actual: Any = when {
            rawValue == "true" -> true
            rawValue == "false" -> false
            rawValue.toDoubleOrNull() != null -> rawValue.toDouble()
            else -> rawValue
        }

        val expected: Any = when {
            expectedJson.isJsonPrimitive && expectedJson.asJsonPrimitive.isBoolean ->
                expectedJson.asBoolean

            expectedJson.isJsonPrimitive && expectedJson.asJsonPrimitive.isNumber ->
                expectedJson.asDouble

            else ->
                expectedJson.asString
        }

        return when (operator) {
            Operator.EQUAL -> actual == expected
            Operator.NOT_EQUAL -> actual != expected

            Operator.CONTAINS -> actual is String && expected is String && actual.contains(expected)
            Operator.NOT_CONTAINS -> actual is String && expected is String && !actual.contains(expected)

            Operator.GREATER_THAN -> (actual as? Double)?.let { it > (expected as Double) } ?: false
            Operator.LESS_THAN -> (actual as? Double)?.let { it < (expected as Double) } ?: false

            Operator.STARTS_WITH -> actual is String && expected is String && actual.startsWith(expected)
            Operator.ENDS_WITH -> actual is String && expected is String && actual.endsWith(expected)
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

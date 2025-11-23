package com.ahmadshahwaiz.kotlinruleengine

import com.ahmadshahwaiz.kotlinruleengine.RuleEngine
import com.ahmadshahwaiz.kotlinruleengine.loadRulesFromJson
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test class for the RuleEngine.
 *
 * This class tests the "PCA" rule using various conditions
 * to ensure that the rule engine evaluates conditions correctly.
 */
class PCARuleEngineTest {

    private lateinit var ruleEngine: RuleEngine

    /**
     * Initializes the RuleEngine with predefined rules before running tests.
     *
     * The rule used for testing is "PCA," which determines
     * whether the operator is STC, STCNOTREADY, AV
     */
    @Before
    fun setup() {
        val jsonRules = """[
            {
                "name": "hasEligibleRatePlanOrLimitedCoverage",
                "description": "  Determines whether the user has an eligible rate plan or falls under a limited coverage area.  This function checks the following conditions:
          - The user has a valid `eligibleRatePlan`, or
          - The service category is `NR`, or
          - The `pcaResponse` is not null and:
          - The coverage type is either `OLO` or `NA`, and
          - Both 5G and 4G are unavailable.
            This ensures that the system correctly identifies users who either have an eligible rate plan or are in areas with limited network coverage.",
                "conditions": {
                    "any": [
                        {
                            "path": "$.pcaResponse.serviceCategory",
                            "value": "NR",
                            "operator": "equal"
                        },
                        {
                            "path": "$.pcaResponse.coverageType",
                             "value": "OLO",
                             "operator": "equal"
                         },
                         {
                            "path": "$.pcaResponse.coverageType",
                             "value": "NA",
                             "operator": "equal"
                         },
                        {
                        "all": [
                          {
                            "path": "$.is5GAvailable",
                            "value": "false",
                            "operator": "equal"
                            },
                         {
                             "path": "$.is4GAvailable",
                             "value": "false",
                             "operator": "equal"
                                }
                         ]
                        }
                    ]
                }
            },
            {
                "name": "IsCoverageTypeSTC",
                "description": "check the coverage type STC",
                "conditions": {
                    "all": [
                        {
                            "path": "$.pcaResponse.obligationAreaOperator",
                            "value": "STC",
                            "operator": "equal"
                        }
                    ]
                    }},
            {
                "name": "isNonFiberAndNotObligationOperatorOrNr",
                "description": "check the technology is not fiber and (obligationAreaOperator is not STC and AV or serviceCategory is NR)",
                "conditions": {
                    "all": [
                        {
                            "path": "$.technology",
                            "value": "ffth",
                            "operator": "not_equal"
                        },
                        {
                            "path": "$.pcaResponse.obligationAreaOperator",
                            "value": "STC",
                            "operator": "not_equal"
                        },
                        {
                            "path": "$.pcaResponse.obligationAreaOperator",
                            "value": "AV",
                            "operator": "not_equal"
                        }
                    ]
                    }}
        ]"""
        val rules = loadRulesFromJson(jsonRules)
        ruleEngine = RuleEngine(rules)
    }


    @Test
    fun testShowButtonDisableWhenServiceCategoryNR() {
        val testData = mapOf(
            "pcaResponse" to mapOf(
                "serviceCategory" to "NR",
                "coverageType" to "STC"
            ),
            "is5GAvailable" to  true,
            "is4GAvailable" to  true,

        )

        val result = ruleEngine.evaluateRule("hasEligibleRatePlanOrLimitedCoverage", testData)
        assertTrue("Expected 'hasEligibleRatePlanOrLimitedCoverage' rule to return true", result)
    }


    @Test
    fun testShowButtonDisableWhenCoverageTypeIsOLO() {
        val testData = mapOf(
            "eligibleRatePlan" to "somePlan",
            "pcaResponse" to mapOf(
                "serviceCategory" to "STC",
                "coverageType" to "OLO"
            ),
            "is5GAvailable" to true,
            "is4GAvailable" to true
        )

        val result = ruleEngine.evaluateRule("hasEligibleRatePlanOrLimitedCoverage", testData)
        assertTrue("Expected 'hasEligibleRatePlanOrLimitedCoverage' to return true (button should be disabled)", result)
    }

    @Test
    fun testShowButtonDisableWhenCoverageTypeIsNA() {
        val testData = mapOf(
            "pcaResponse" to mapOf(
                "serviceCategory" to "STC",
                "coverageType" to "NA"
            ),
            "is5GAvailable" to true,
            "is4GAvailable" to true
        )

        val result = ruleEngine.evaluateRule("hasEligibleRatePlanOrLimitedCoverage", testData)
        assertTrue("Expected 'hasEligibleRatePlanOrLimitedCoverage' to return true (button should be disabled)", result)
    }

    @Test
    fun testShowButtonDisableWhenBoth4GAnd5GUnavailable() {
        val testData = mapOf(
            "pcaResponse" to mapOf(
                "serviceCategory" to "NR",
                "coverageType" to "STC"
            ),
            "is5GAvailable" to false,
            "is4GAvailable" to false
        )
        println("Evaluating rule with data: $testData")
        val result = ruleEngine.evaluateRule("hasEligibleRatePlanOrLimitedCoverage", testData)
        assertTrue("Expected 'hasEligibleRatePlanOrLimitedCoverage' to return true (button should be disabled)", result)
    }

    @Test
    fun testShowButtonEnableWhenAllConditionsFail() {
        val testData = mapOf(
            "pcaResponse" to mapOf(
                "serviceCategory" to "STC",
                "coverageType" to "STC"
            ),
            "is5GAvailable" to true,
            "is4GAvailable" to true
        )

        val result = ruleEngine.evaluateRule("hasEligibleRatePlanOrLimitedCoverage", testData)
        assertFalse("Expected 'hasEligibleRatePlanOrLimitedCoverage' to return false (button should be enabled)", result)
    }
}

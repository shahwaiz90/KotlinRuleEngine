import com.ahmadshahwaiz.kotlinruleengine.RuleEngine
import com.ahmadshahwaiz.kotlinruleengine.loadRulesFromJson
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test class for the RuleEngine.
 *
 * This class tests the "ShowWithoutVAT" rule using various conditions
 * to ensure that the rule engine evaluates conditions correctly.
 */
class RuleEngineTest {

    private lateinit var ruleEngine: RuleEngine

    /**
     * Initializes the RuleEngine with predefined rules before running tests.
     *
     * The rule used for testing is "ShowWithoutVAT," which determines
     * whether VAT should be displayed in subscriptions based on product category
     * and service type.
     */
    @Before
    fun setup() {
        val jsonRules = """[
            {
                "name": "ShowWithoutVAT",
                "description": "show without VAT in subscriptions",
                "conditions": {
                    "all": [
                        {
                            "path": "$.product.category",
                            "value": "RATEPLANS",
                            "operator": "not_contains"
                        },
                        {
                            "any": [
                                {
                                    "path": "$.context.serviceType",
                                    "value": "prepaid",
                                    "operator": "equal"
                                },
                                {
                                    "path": "$.context.serviceType",
                                    "value": "quicknet_prepaid",
                                    "operator": "equal"
                                },
                                {
                                    "all": [
                                        {
                                            "path": "$.context.serviceType",
                                            "value": "flex",
                                            "operator": "equal"
                                        },
                                        {
                                            "path": "$.product.isEndUserControl",
                                            "value": true,
                                            "operator": "equal"
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            }
        ]"""

        val rules = loadRulesFromJson(jsonRules)
        ruleEngine = RuleEngine(rules)
    }

    /**
     * Test case: The rule should return `true` when:
     * - The product category is **not "RATEPLANS"**
     * - The service type is **"flex"**
     * - The product is **end-user controlled**
     */
    @Test
    fun testShowWithoutVAT_ShouldReturnTrue_WhenCategoryIsNotRatePlans_AndServiceTypeIsFlex() {
        val testData = mapOf(
            "product" to mapOf(
                "category" to "Addons",
                "isEndUserControl" to true
            ),
            "context" to mapOf(
                "serviceType" to "flex"
            )
        )

        val result = ruleEngine.evaluateRule("ShowWithoutVAT", testData)
        assertTrue("Expected 'ShowWithoutVAT' rule to return true", result)
    }

    /**
     * Test case: The rule should return `false` when:
     * - The product category **contains "RATEPLANS"** (which violates the rule condition)
     */
    @Test
    fun testShowWithoutVAT_ShouldReturnFalse_WhenCategoryContainsRatePlans() {
        val testData = mapOf(
            "product" to mapOf(
                "category" to "RATEPLANS",
                "isEndUserControl" to true
            ),
            "context" to mapOf(
                "serviceType" to "flex"
            )
        )

        val result = ruleEngine.evaluateRule("ShowWithoutVAT", testData)
        assertFalse("Expected 'ShowWithoutVAT' rule to return false", result)
    }

    /**
     * Test case: The rule should return `true` when:
     * - The service type is **"prepaid"**
     * - The product category **does not contain "RATEPLANS"**
     */
    @Test
    fun testShowWithoutVAT_ShouldReturnTrue_WhenServiceTypeIsPrepaid() {
        val testData = mapOf(
            "product" to mapOf(
                "category" to "DATA"
            ),
            "context" to mapOf(
                "serviceType" to "prepaid"
            )
        )

        val result = ruleEngine.evaluateRule("ShowWithoutVAT", testData)
        assertTrue("Expected 'ShowWithoutVAT' rule to return true", result)
    }
}

package com.ahmadshahwaiz.kotlinruleengine

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit test class for the RuleEngine.
 *
 * This class tests the "PCA" rule using various conditions
 * to ensure that the rule engine evaluates conditions correctly.
 */
class SubscriptionActionButtonsTest {

    private lateinit var ruleEngine: RuleEngine

    @Before
    fun setup() {
        val jsonRules = """[{
                
      "name": "SubscriptionActionRule",
      "strategy": "first-match",
      "cases": [
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [ { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" }, { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": false, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "purchase" } ] }
        },

        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [ { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" }, { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": true, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": false, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "subscribe" } ] }
        },

        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [ { "key": "purchaseAgain" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [ { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" }, { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": false, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "purchase" } ] }
        },

        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [ { "key": "subscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": false, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            {
              "any": [
                { "all": [
                  { "path": "$.context.hasRenewable", "value": true, "operator": "equal" },
                  { "path": "$.context.renewable", "value": false, "operator": "equal" }
                ]},
                { "path": "$.context.hasRenewable", "value": false, "operator": "equal" }
              ]
            }
          ]},
          "result": { "actions": [ { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": true, "operator": "equal" },
            { "path": "$.context.isUnsubscribable", "value": true, "operator": "equal" },
            { "path": "$.context.renewable", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "renew" }, { "key": "unsubscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isPrepaid", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "equal" },
            { "path": "$.context.isSubscribed", "value": false, "operator": "equal" }
          ]},
          "result": { "actions": [ { "key": "subscribe" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.isSubscribed", "value": false, "operator": "equal" },
            { "path": "$.product.subscriptionType", "value": "ONETIME", "operator": "not_equal" },
            { "path": "$.product.subscriptionType", "value": "RECURRING", "operator": "not_equal" }
          ]},
          "result": { "actions": [ { "key": "changePackage" } ] }
        },
        {
          "when": { "all": [
            { "path": "$.context.always", "value": true, "operator": "equal" }
          ]},
          "result": { "actions": [] }
        }
      ]
    }
]""".trimIndent()
        val rules = loadRulesFromJson(jsonRules)
        ruleEngine = RuleEngine(rules)
    }

    @Test
    fun case01_prepaid_onetime_subscribed_notUnsub_hasRenew_true_renewFalse_noCTA() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertTrue(actions.isEmpty())
    }

    @Test
    fun case02_prepaid_onetime_subscribed_notUnsub_hasRenew_false_noCTA() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertTrue(actions.isEmpty())
    }

    @Test
    fun case03_prepaid_onetime_subscribed_notUnsub_renewable_true_Renew() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", actions[0]["key"])
    }

    @Test
    fun case04_prepaid_onetime_subscribed_isUnsub_true_hasRenew_true_renewFalse_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", actions[0]["key"])
    }

    @Test
    fun case05_prepaid_onetime_subscribed_isUnsub_true_hasRenew_false_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", actions[0]["key"])
    }

    @Test
    fun case06_prepaid_onetime_subscribed_isUnsub_true_renewable_true_twoCTAs() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
        assertEquals("unsubscribe", acts[1]["key"])
    }

    @Test
    fun case07_prepaid_onetime_notSubscribed_purchase() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("purchase", actions[0]["key"])
    }

    @Test
    fun case08_prepaid_recurring_subscribed_notUnsub_hasRenew_true_renewFalse_noCTA() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertTrue(acts.isEmpty())
    }

    @Test
    fun case09_prepaid_recurring_subscribed_notUnsub_hasRenew_false_noCTA() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val actions = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertTrue(actions.isEmpty())
    }

    @Test
    fun case10_prepaid_recurring_subscribed_notUnsub_renewable_true_renew() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
    }

    @Test
    fun case11_prepaid_recurring_subscribed_isUnsub_true_hasRenew_true_renewFalse_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", acts[0]["key"])
    }

    @Test
    fun case12_prepaid_recurring_subscribed_isUnsub_true_hasRenew_false_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", acts[0]["key"])
    }

    @Test
    fun case13_prepaid_recurring_subscribed_isUnsub_true_renewable_true_twoCTAs() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
        assertEquals("unsubscribe", acts[1]["key"])
    }

    @Test
    fun case14_prepaid_recurring_notSubscribed_subscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to true,
                "isSubscribed" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("subscribe", acts[0]["key"])
    }

    @Test
    fun case15_postpaid_onetime_subscribed_notUnsub_hasRenew_true_renewFalse_purchaseAgain() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("purchaseAgain", acts[0]["key"])
    }

    @Test
    fun case16_postpaid_onetime_subscribed_notUnsub_hasRenew_false_purchaseAgain() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("purchaseAgain", acts[0]["key"])
    }

    @Test
    fun case17_postpaid_onetime_subscribed_notUnsub_renewable_true_renew() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
    }

    @Test
    fun case18_postpaid_onetime_subscribed_isUnsub_true_hasRenew_true_renewFalse_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", acts[0]["key"])
    }

    @Test
    fun case19_postpaid_onetime_subscribed_isUnsub_true_hasRenew_false_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", acts[0]["key"])
    }

    @Test
    fun case20_postpaid_onetime_subscribed_isUnsub_true_renewable_true_twoCTAs() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
        assertEquals("unsubscribe", acts[1]["key"])
    }

    @Test
    fun case21_postpaid_onetime_notSubscribed_purchase() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to false
            ),
            "product" to mapOf("subscriptionType" to "ONETIME")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("purchase", acts[0]["key"])
    }

    @Test
    fun case22_postpaid_recurring_subscribed_notUnsub_hasRenew_true_renewFalse_subscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("subscribe", acts[0]["key"])
    }

    @Test
    fun case23_postpaid_recurring_subscribed_notUnsub_hasRenew_false_subscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("subscribe", acts[0]["key"])
    }

    @Test
    fun case24_postpaid_recurring_subscribed_notUnsub_renewable_true_renew() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to false,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
    }

    @Test
    fun case25_postpaid_recurring_subscribed_isUnsub_true_hasRenew_true_renewFalse_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", acts[0]["key"])
    }

    @Test
    fun case26_postpaid_recurring_subscribed_isUnsub_true_hasRenew_false_unsubscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("unsubscribe", acts[0]["key"])
    }

    @Test
    fun case27_postpaid_recurring_subscribed_isUnsub_true_renewable_true_twoCTAs() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to true,
                "isUnsubscribable" to true,
                "hasRenewable" to true,
                "renewable" to true
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("renew", acts[0]["key"])
        assertEquals("unsubscribe", acts[1]["key"])
    }

    @Test
    fun case28_postpaid_recurring_notSubscribed_subscribe() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to false
            ),
            "product" to mapOf("subscriptionType" to "RECURRING")
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("subscribe", acts[0]["key"])
    }

    @Test
    fun case29_postpaid_noType_notSubscribed_changePackage() {
        val data = mapOf(
            "context" to mapOf(
                "isPrepaid" to false,
                "isSubscribed" to false
            )
            // subscriptionType intentionally omitted
        )

        val acts = ruleEngine.extractActions(ruleEngine.firstMatchResult("SubscriptionActionRule", data))
        assertEquals("changePackage", acts[0]["key"])
    }


}

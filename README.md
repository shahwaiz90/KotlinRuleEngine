# Rule Engine for Android (Kotlin)

## 📌 Overview
This project is a **Rule Engine** implemented in Kotlin for Android applications. It evaluates business rules defined in JSON format against provided data. The engine supports complex conditions using **all** and **any** logic.

## 🚀 Features
- Load rules from JSON
- Evaluate all rules against given data
- Evaluate a specific rule by name
- Supports multiple operators (equal, not_equal, contains, etc.)
- Handles nested conditions (all/any)

## 📂 Project Structure
```
|-- src/
|   |-- RuleEngine.kt         # Core rule evaluation logic
|   |-- RuleModel.kt          # Data model for rules
|   |-- Operator.kt           # Supported operators
|   |-- RuleEngineTest.kt     # Unit tests for rule evaluation
|-- README.md                 # Project documentation 
```
## Library installation
 ```implementation 'com.github.shahwaiz90:KotlinRuleEngine:0.1.3'```
## 📥 Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/shahwaiz90/KotlinRuleEngine.git
   ```
2. Open the project in **Android Studio**.
3. Ensure you have **Kotlin** and **Gson** dependencies installed:
   ```kotlin
   implementation("com.google.code.gson:gson:2.8.9")
   ```

## 🔧 Usage
### **1. Define Rules (JSON Format)**
Create a `rules.json` file with rules:
```json
[
    {
        "name": "ShowWithoutVAT",
        "description": "Show without VAT in subscriptions",
        "conditions": {
            "all": [
                { "path": "$.product.category", "value": "RATEPLANS", "operator": "not_contains" },
                { "any": [
                    { "path": "$.context.serviceType", "value": "prepaid", "operator": "equal" },
                    { "path": "$.context.serviceType", "value": "quicknet_prepaid", "operator": "equal" }
                ]}
            ]
        }
    }
]
```

### **2. Load Rules into Rule Engine**
```kotlin
val jsonRules = File("rules.json").readText()
val rules = loadRulesFromJson(jsonRules)
val ruleEngine = RuleEngine(rules)
```

### **3. Evaluate Rules Against Data**
```kotlin
val testData = mapOf(
    "product" to mapOf("category" to "Addons"),
    "context" to mapOf("serviceType" to "prepaid")
)
val result = ruleEngine.evaluateRule("ShowWithoutVAT", testData)
println("Result: $result") // Expected: true
```

## ✅ Running Tests
Run unit tests using:
```sh
./gradlew test
```
Example test case:
```kotlin
@Test
fun testShowWithoutVAT_ShouldReturnTrue_WhenServiceTypeIsPrepaid() {
    val testData = mapOf(
        "product" to mapOf("category" to "DATA"),
        "context" to mapOf("serviceType" to "prepaid")
    )
    val result = ruleEngine.evaluateRule("ShowWithoutVAT", testData)
    assertTrue(result)
}
```
<img width="837" alt="Screenshot 2025-02-12 at 3 29 35 PM" src="https://github.com/user-attachments/assets/9569a56e-bf87-4a51-b188-aaf7f261f8aa" />


## 🤝 Contributing
1. Fork the repo
2. Create a feature branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to your branch (`git push origin feature-name`)
5. Open a Pull Request 🚀

## 📜 License
This project is licensed under the MIT License.

## ⭐ Support
If you like this project, consider giving it a **⭐ on GitHub!** 😊


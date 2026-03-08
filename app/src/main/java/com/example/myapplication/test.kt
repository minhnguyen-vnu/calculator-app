package com.example.myapplication

/**
 * A comprehensive Kotlin class example.
 * * 1. Primary Constructor: 'name' is required (Parentheses)
 * 2. 'val' name: Automatically creates a private field and a public getter.
 */
class FootballPlayer(val name: String) {

    // --- 3. Internal State (Brackets) ---
    // This variable is NOT in the constructor. It has a default value.
    // It uses a PROXY strategy with 'field'.
    var goals: Int = 0
        get() {
            println("Proxy: Checking stats for $name...")
            return field // Accesses the raw memory slot
        }
        set(value) {
            if (value >= field) { // Logic: You can't "un-score" a goal
                println("Proxy: Updating goals from $field to $value")
                field = value // Updates the raw memory slot
            } else {
                println("Proxy: Error! Goals cannot decrease.")
            }
        }

    // --- 4. Custom Computed Property ---
    // This does NOT have a 'field' because it calculates on-the-fly.
    val isLegend: Boolean
        get() = goals > 500

    // --- 5. Secondary Constructor ---
    // Overloading: Allows creating a player with a starting goal count.
    // It MUST call the primary constructor using 'this(name)'.
    constructor(name: String, startingGoals: Int) : this(name) {
        this.goals = startingGoals // Using 'this' to access the property
    }

    // --- 6. Regular Function ---
    fun scoreGoal(newGoals: Int) {
        // Here, 'newGoals' is the local parameter.
        // We use 'this.goals' to trigger the custom Setter logic above.
        // Note: We CANNOT use 'field' here.
        this.goals += newGoals

        println("${this.name} now has ${this.goals} total goals!")
    }
}

// --- 7. Execution ---
fun main() {
    // No 'new' keyword (unlike Java/C++)
    val player = FootballPlayer("Minh Nguyen", 499)

    // Accessing a property (Calls the custom Getter)
    println("Is legendary? ${player.isLegend}")

    // Calling a function (Triggers the Proxy Setter)
    player.scoreGoal(2)

    // Trying to cheat (Triggering the "Blocked" logic in the Proxy)
    player.goals = 10
}
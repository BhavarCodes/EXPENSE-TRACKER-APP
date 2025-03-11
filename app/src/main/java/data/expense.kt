package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")  // Defines a table in the Room database
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-increment primary key
    val category: String,  // Example: Food, Transport, Bills
    val amount: Double,    // The expense amount
    val date: Long         // Timestamp when the expense was added
)

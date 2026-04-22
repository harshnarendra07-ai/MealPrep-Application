// https://drive.google.com/file/d/1uAg6gAAeVObvT4TFacAc7m8zHJ67selc/view?usp=sharing

package com.example.mymealprep

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

lateinit var db: AppDatabase
lateinit var mealDao: MealDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "meal_database"
        ).build()

        mealDao = db.mealDao()

        setContent {
            var currentScreen by remember { mutableStateOf("MainMenu") }

            if (currentScreen == "MainMenu") {
                GUI(
                    onNavigateToDbSearch = { currentScreen = "DbSearch" },
                    // ADD THIS NEW LINE:
                    onNavigateToIngredientSearch = { currentScreen = "IngredientSearch" }
                )
            } else if (currentScreen == "DbSearch") {
                DatabaseSearchScreen(onBack = { currentScreen = "MainMenu" })
            } else if (currentScreen == "IngredientSearch") {
                // ADD THIS NEW ROUTE:
                IngredientSearchScreen(onBack = { currentScreen = "MainMenu" })
            }
        }
    }
}




@Composable

fun GUI(onNavigateToDbSearch: () -> Unit, onNavigateToIngredientSearch: () -> Unit) {
    val scope = rememberCoroutineScope()
    var webSearchText by remember { mutableStateOf("") }


    var webSearchResults by remember { mutableStateOf<List<Meal>>(emptyList()) }

    val scrollState = rememberScrollState()

    Column(

        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(30.dp))


        Button(onClick = {
            scope.launch {
                val meal1 = Meal(
                    mealName = "Spicy Arrabiata Penne", category = "Vegetarian", area = "Italian",
                    instructions = "Bring a large pot of water to a boil...", youtubeLink = "https://www.youtube.com/watch?v=1IszT_guI08",
                    ingredient1 = "penne rigate", ingredient2 = "olive oil", measure1 = "1 pound", measure2 = "1/4 cup"
                )
                val meal2 = Meal(
                    mealName = "Brown Stew Chicken", category = "Chicken", area = "Jamaican",
                    instructions = "Squeeze lime over chicken...", youtubeLink = "https://www.youtube.com/watch?v=_gFB1fkNhXs",
                    ingredient1 = "Chicken", ingredient2 = "Tomato", measure1 = "1 whole", measure2 = "1 chopped"
                )
                mealDao.insertMeal(meal1)
                mealDao.insertMeal(meal2)
                println("SUCCESS!  Meals added to the database.")
            }
        }) {
            Text("Add Meals to DB")
        }

        Spacer(modifier = Modifier.height(10.dp))


        Button(onClick = {
            onNavigateToIngredientSearch()
        }) {
            Text("Search for Meals By Ingredient")
        }

        Spacer(modifier = Modifier.height(10.dp))


        Button(onClick = {
            onNavigateToDbSearch()
        }) {
            Text("Search for Meals")
        }

        Spacer(modifier = Modifier.height(30.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(30.dp))


        Text("Web Service Search")

        TextField(
            value = webSearchText,
            onValueChange = { newText -> webSearchText = newText },
            label = { Text("Enter meal name (e.g., 'Chi')") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            scope.launch {
                val rawJson = getMealsFromInternet(webSearchText)
                val translatedMeals = parseMealsJSON(rawJson)


                webSearchResults = translatedMeals
            }
        }) {
            Text("Search Web Service")
        }

        Spacer(modifier = Modifier.height(20.dp))


        for (meal in webSearchResults) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: ${meal.mealName}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Category: ${meal.category}")
                    Text(text = "Area: ${meal.area}")
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DatabaseSearchScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Meal>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = onBack) {
            Text("<- Back to Menu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Database (e.g., 'chi')") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                scope.launch {
                    searchResults = mealDao.searchMeals(searchText)
                }
            }
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Display Results
        if (searchResults.isEmpty()) {
            Text("No meals found.")
        } else {
            for (meal in searchResults) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    NetworkImage(url = "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg")


                    Text(
                        text = "Name: ${meal.mealName} \nCategory: ${meal.category}"
                    )
                }
            }
        }
    }
}


@Composable
fun NetworkImage(url: String) {

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }


    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {

                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()


                val input = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Meal Thumbnail",
            modifier = Modifier.size(100.dp).padding(end = 16.dp)
        )
    } else {
        Box(modifier = Modifier.size(100.dp).padding(end = 16.dp).background(Color.LightGray))
    }
}

@Composable
fun IngredientSearchScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var ingredientText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var saveMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
        Button(onClick = onBack) { Text("<- Back to Menu") }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = ingredientText,
            onValueChange = { ingredientText = it },
            label = { Text("Search by Ingredient (e.g., 'chicken_breast')") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- TASK 3: SEARCH WEB SERVICE BY INGREDIENT ---
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                scope.launch {
                    saveMessage = "Searching..."
                    val rawJson = getIngredientFromInternet(ingredientText)
                    searchResults = parseMealsJSON(rawJson)
                    saveMessage = "Found ${searchResults.size} meals."
                }
            }
        ) { Text("Search Web by Ingredient") }

        Spacer(modifier = Modifier.height(8.dp))

        // --- TASK 4: SAVE TO DB BUTTON ---
        // This button only appears IF we have results!
        if (searchResults.isNotEmpty()) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Makes the button Green!
                onClick = {
                    scope.launch {
                        for (meal in searchResults) {
                            mealDao.insertMeal(meal) // Save each one to the local DB
                        }
                        saveMessage = "Successfully saved ${searchResults.size} meals to the Database!"
                    }
                }
            ) { Text("Save Meals to Database") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = saveMessage, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Display the results
        for (meal in searchResults) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: ${meal.mealName}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}


suspend fun getIngredientFromInternet(ingredient: String): String {
    return withContext(Dispatchers.IO) {

        val url = URL("https://www.themealdb.com/api/json/v1/1/filter.php?i=$ingredient")
        val con = url.openConnection() as HttpURLConnection
        val stb = StringBuilder()
        try {
            val bf = BufferedReader(InputStreamReader(con.inputStream))
            var line: String? = bf.readLine()
            while (line != null) {
                stb.append(line + "\n")
                line = bf.readLine()
            }
        } catch (e: Exception) { e.printStackTrace() }
        stb.toString()
    }
}
package com.example.mymealprep
import org.json.JSONObject
import org.json.JSONArray



import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

suspend fun getMealsFromInternet(keyword: String): String {

    val url_string = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + keyword
    val url = URL(url_string)

    val con: HttpURLConnection = url.openConnection() as HttpURLConnection

    var stb = StringBuilder()

    withContext (Dispatchers.IO) {
        var bf = BufferedReader(InputStreamReader(con.inputStream))
        var line: String? = bf.readLine()

        while (line != null) {
            stb.append(line + "\n")
            line = bf.readLine()
        }
    }


    return stb.toString()
}

fun parseMealsJSON(jsonString: String): List<Meal> {

    val mealList = mutableListOf<Meal>()


    val json = JSONObject(jsonString)

    if (json.isNull("meals")) {
        return mealList
    }


    val jsonArray: JSONArray = json.getJSONArray("meals")


    for (i in 0..jsonArray.length() - 1) {
        val mealJson: JSONObject = jsonArray[i] as JSONObject


        val title = mealJson.optString("strMeal", "")
        val category = mealJson.optString("strCategory", "")
        val area = mealJson.optString("strArea", "")
        val instructions = mealJson.optString("strInstructions", "")
        val youtube = mealJson.optString("strYoutube", "")
        val ing1 = mealJson.optString("strIngredient1", "")
        val ing2 = mealJson.optString("strIngredient2", "")
        val meas1 = mealJson.optString("strMeasure1", "")
        val meas2 = mealJson.optString("strMeasure2", "")


        val meal = Meal(
            mealName = title,
            category = category,
            area = area,
            instructions = instructions,
            youtubeLink = youtube,
            ingredient1 = ing1,
            ingredient2 = ing2,
            measure1 = meas1,
            measure2 = meas2
        )


        mealList.add(meal)
    }


    return mealList
}
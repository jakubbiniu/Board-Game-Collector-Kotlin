package edu.put.inf151921

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*

class SynchronizationActivity : AppCompatActivity() {
    private val dbHelper: DatabaseHelper by lazy { DatabaseHelper(this) }
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization)
        progressBar = findViewById(R.id.progressBar)
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var synchroDate = sharedPreferences.getString("synchroDate",null)
        val date: TextView = findViewById(R.id.synDate)
        date.text = "Last synchronization date: " + synchroDate.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun showInfoMessage() {
        val message = "Database has been successfully updated"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun parseXmlResponse(xmlResponse: String?): List<Game> {
        val games = mutableListOf<Game>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser: XmlPullParser = factory.newPullParser()
            parser.setInput(StringReader(xmlResponse))

            var eventType = parser.eventType
            var currentGame: Game? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name
                        if (tagName == "item") {
                            currentGame = Game()
                            val objectId = parser.getAttributeValue(null, "objectid")
                            if (!objectId.isNullOrBlank()) {
                                currentGame.gameId = (objectId.toLongOrNull() ?: 0)
                            }
                            val subtype = parser.getAttributeValue(null, "subtype")
                            if (!subtype.isNullOrBlank()) {
                                currentGame.type = subtype
                            }
                        } else if (currentGame != null) {
                            when (tagName) {
                                "name" -> {
                                    val name = parser.nextText()
                                    if (!name.isNullOrBlank()) {
                                        currentGame.name = name
                                    }
                                }
                                "description" -> {
                                    val description = parser.nextText()
                                    if (!description.isNullOrBlank()) {
                                        currentGame.description = description
                                    }
                                }
                                "yearpublished" -> {
                                    val yearPublished = parser.nextText()
                                    if (!yearPublished.isNullOrBlank()) {
                                        currentGame.yearPublished = yearPublished.toIntOrNull() ?: 0
                                    }
                                }
                                "image" -> {
                                    val image = parser.nextText()
                                    if (!image.isNullOrBlank()) {
                                        currentGame.image = image
                                    }
                                }
                                "thumbnail" -> {
                                    val thumbnail = parser.nextText()
                                    if (!thumbnail.isNullOrBlank()) {
                                        currentGame.thumbnail = thumbnail
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name

                        if (tagName == "item" && currentGame != null) {
                            games.add(currentGame)
                            currentGame = null
                        }
                    }
                }

                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return games
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    private fun insertGamesIntoDatabase(games: List<Game>, expansions: List<Game>) {
        val db = dbHelper.writableDatabase
        dbHelper.onCreate(db)


        var progress=0
        var total = games.size + expansions.size
        var index = 0

        // Insert games into the database
        games.forEach { game ->
            progress = ((index + 1) * 100) / total
            index +=1
            //runOnUiThread {
                progressBar.setProgress(progress,true)
                progressBar.contentDescription = "Synchronization progress: $progress%"
            //}


            val values = ContentValues()
            values.put(DatabaseHelper.COLUMN_NAME, game.name)
            values.put(DatabaseHelper.COLUMN_GAME_ID, game.gameId)
            values.put(DatabaseHelper.COLUMN_TYPE, game.type)
            values.put(DatabaseHelper.COLUMN_YEAR_PUBLISHED, game.yearPublished)
            values.put(DatabaseHelper.COLUMN_IMAGE, game.image)
            values.put(DatabaseHelper.COLUMN_THUMBNAIL, game.thumbnail)


            val gameDetailsUrl = "https://www.boardgamegeek.com/xmlapi2/thing?id=${game.gameId}&stats=1"
            val client = OkHttpClient()
            val request = Request.Builder().url(gameDetailsUrl).build()

            var response: Response? = null

            try {
                response = client.newCall(request).execute()
                val xmlResponse = response.body?.string()

                if (xmlResponse != null) {
                    val (description, minPlayers, maxPlayers, rank) = parseGameAttributesFromXml(xmlResponse)


                    values.put(DatabaseHelper.COLUMN_DESCRIPTION, description)
                    values.put(DatabaseHelper.COLUMN_MIN, minPlayers)
                    values.put(DatabaseHelper.COLUMN_MAX, maxPlayers)
                    values.put(DatabaseHelper.COLUMN_RANK, rank)
                }


                db.insert(DatabaseHelper.TABLE_NAME, null, values)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                response?.body?.close()
            }
        }


        expansions.forEach { expansion ->
            progress = ((index + 1) * 100) / total
            index +=1
            //runOnUiThread {
                progressBar.setProgress(progress,true)
                progressBar.contentDescription = "Synchronization progress: $progress%"
            //}



            val values = ContentValues()
            values.put(DatabaseHelper.COLUMN_NAME, expansion.name)
            values.put(DatabaseHelper.COLUMN_GAME_ID, expansion.gameId)
            values.put(DatabaseHelper.COLUMN_YEAR_PUBLISHED, expansion.yearPublished)
            values.put(DatabaseHelper.COLUMN_TYPE, expansion.type)
            values.put(DatabaseHelper.COLUMN_IMAGE, expansion.image)
            values.put(DatabaseHelper.COLUMN_THUMBNAIL, expansion.thumbnail)

            val gameDetailsUrl = "https://www.boardgamegeek.com/xmlapi2/thing?id=${expansion.gameId}&stats=1"
            val client = OkHttpClient()
            val request = Request.Builder().url(gameDetailsUrl).build()

            var response2: Response? = null

            try {
                response2 = client.newCall(request).execute()
                val xmlResponse = response2.body?.string()

                if (xmlResponse != null) {
                    val (description, minPlayers, maxPlayers,rank) = parseGameAttributesFromXml(xmlResponse)


                    values.put(DatabaseHelper.COLUMN_DESCRIPTION, description)
                    values.put(DatabaseHelper.COLUMN_MIN, minPlayers)
                    values.put(DatabaseHelper.COLUMN_MAX, maxPlayers)
                    values.put(DatabaseHelper.COLUMN_RANK, rank)
                }

                db.insert(DatabaseHelper.TABLE_NAME, null, values)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                response2?.body?.close()
            }
        }

        db.close()
    }
    fun calculateTimeDifference(date1: String, date2: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTime1 = dateFormat.parse(date1)
        val dateTime2 = dateFormat.parse(date2)

        val diffInMillis = dateTime2.time - dateTime1.time

        val hours: Long = diffInMillis / (1000 * 60 * 60)

        return hours
    }
    private fun parseGameAttributesFromXml(xmlResponse: String?): Quadruple<String, Int, Int, Int> {
        var description = ""
        var minPlayers = 0
        var maxPlayers = 0
        var rankValue = 0

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser: XmlPullParser = factory.newPullParser()
            parser.setInput(StringReader(xmlResponse))

            var eventType = parser.eventType
            var insideDescriptionTag = false
            var insideRankTag = false
            var insideRatingsTag = false
            var insideStatisticsTag = false
            var insideRanksTag =false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name
                        if (tagName == "description") {
                            insideDescriptionTag = true
                        } else if (tagName == "minplayers") {
                            val value = parser.getAttributeValue(null, "value")
                            minPlayers = value?.toIntOrNull() ?: 0
                        } else if (tagName == "maxplayers") {
                            val value = parser.getAttributeValue(null, "value")
                            maxPlayers = value?.toIntOrNull() ?: 0
                        } else if (tagName == "rank" && insideRanksTag && parser.getAttributeValue(null, "type") == "subtype" && parser.getAttributeValue(null, "name") == "boardgame") {
                            val value =  parser.getAttributeValue(null, "value")
                            rankValue = value?.toIntOrNull() ?: 0
                        } else if (tagName == "ratings" && insideStatisticsTag) {
                            insideRatingsTag = true
                        } else if (tagName == "statistics") {
                            insideStatisticsTag = true
                        }
                        else if (tagName=="ranks" && insideRatingsTag){
                            insideRanksTag=true
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text.trim()
                        if (insideDescriptionTag) {
                            description = text
                            insideDescriptionTag = false
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name
                        if (tagName == "ratings") {
                            insideRatingsTag = false
                        } else if (tagName == "statistics") {
                            insideStatisticsTag = false
                        }
                        else if (tagName == "ranks"){
                            insideRanksTag=false
                        }
                    }
                }

                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Quadruple(description, minPlayers, maxPlayers, rankValue)
    }
    fun sycnhronize(v: View){
        var currentDate = getCurrentDate()
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var synchroDate = sharedPreferences.getString("synchroDate",null)
        var difference = synchroDate?.let { calculateTimeDifference(it,currentDate) }
        if (difference != null) {
            if (1==1){
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Confirm")
                dialogBuilder.setMessage("Are you sure you want to do the synchronization?")
                dialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                    val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    val cache = sharedPreferences.edit()
                    cache.putString("synchroDate", currentDate)
                    cache.apply()
                    progressBar.visibility = View.VISIBLE



                    var username = sharedPreferences.getString("username", null)
                    Toast.makeText(
                        this@SynchronizationActivity,
                        "Loading in progress. This may take a while...",
                        Toast.LENGTH_LONG
                    ).show()
                    GlobalScope.launch(Dispatchers.IO) {
                        val gameUrl = "https://boardgamegeek.com/xmlapi2/collection?username=$username&subtype=boardgame&excludesubtype=boardgameexpansion"
                        val expansionUrl = "https://boardgamegeek.com/xmlapi2/collection?username=$username&subtype=boardgameexpansion"
                        val client = OkHttpClient()
                        val gameRequest = Request.Builder().url(gameUrl).build()
                        val expansionRequest = Request.Builder().url(expansionUrl).build()

                        var gameResponse: Response? = null
                        var expansionResponse: Response? = null

                        try {
                            gameResponse = client.newCall(gameRequest).execute()
                            val gameXmlResponse = gameResponse.body?.string()

                            expansionResponse = client.newCall(expansionRequest).execute()
                            val expansionXmlResponse = expansionResponse.body?.string()

                            if (gameXmlResponse != null && expansionXmlResponse != null) {
                                val games = parseXmlResponse(gameXmlResponse)
                                val expansions = parseXmlResponse(expansionXmlResponse)

                                if (games.isNotEmpty() || expansions.isNotEmpty()) {
                                    insertGamesIntoDatabase(games, expansions)
                                    runOnUiThread {
                                        val progress = 100
                                        progressBar.setProgress(progress,true)
                                        progressBar.contentDescription = "Synchronization progress: 100%"
                                        progressBar.visibility = View.GONE
                                        showInfoMessage()
                                        val intent = Intent(applicationContext, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }

                                } else {
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@SynchronizationActivity,
                                            "No games found for the given username.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@SynchronizationActivity,
                                        "Failed to retrieve data from the server.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            gameResponse?.body?.close()
                            expansionResponse?.body?.close()
                        }
                    }
                }
                dialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialogBuilder.show()
            }

        }



    }
}

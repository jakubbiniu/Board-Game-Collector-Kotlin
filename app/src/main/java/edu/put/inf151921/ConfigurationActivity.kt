package edu.put.inf151921

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.*

class ConfigurationActivity : AppCompatActivity() {
    private val dbHelper: DatabaseHelper by lazy { DatabaseHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
    }

    private fun showInfoMessage() {
        val message = "Database has been successfully created"
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

        // Create the tables if they don't exist
        dbHelper.onCreate(db)

        // Insert games into the database
        games.forEach { game ->
            val values = ContentValues()
            values.put(DatabaseHelper.COLUMN_NAME, game.name)
            values.put(DatabaseHelper.COLUMN_GAME_ID, game.gameId)
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, game.description)
            values.put(DatabaseHelper.COLUMN_YEAR_PUBLISHED, game.yearPublished)
            values.put(DatabaseHelper.COLUMN_TYPE, game.type)
            db.insert(DatabaseHelper.TABLE_NAME, null, values)
        }

        // Insert expansions into the database
        expansions.forEach { expansion ->
            val values = ContentValues()
            values.put(DatabaseHelper.COLUMN_NAME, expansion.name)
            values.put(DatabaseHelper.COLUMN_GAME_ID, expansion.gameId)
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, expansion.description)
            values.put(DatabaseHelper.COLUMN_YEAR_PUBLISHED, expansion.yearPublished)
            values.put(DatabaseHelper.COLUMN_TYPE, expansion.type)
            db.insert(DatabaseHelper.TABLE_NAME, null, values)
        }

        db.close()
    }

    fun confirm(v: View) {
        val input: EditText = findViewById(R.id.username)
        val username: String = input.text.toString()
        val currentDate = getCurrentDate()

        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val cache = sharedPreferences.edit()
        cache.putString("username", username)
        cache.putString("synchroDate", currentDate)
        cache.apply()

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
                            showInfoMessage()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@ConfigurationActivity,
                                "No games found for the given username.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@ConfigurationActivity,
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

}

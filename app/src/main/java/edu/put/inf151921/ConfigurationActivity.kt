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

    private fun parseXmlResponse(xmlResponse: String?): GameCollection {
        val gameCollection = GameCollection()

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
                        } else if (currentGame != null) {
                            when (tagName) {
                                "name" -> {
                                    val attributeValue = parser.getAttributeValue(null, "value")
                                    currentGame.name = attributeValue ?: ""
                                }
                                // Add other game attributes as needed
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name

                        if (tagName == "item" && currentGame != null) {
                            gameCollection.games.add(currentGame)
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

        return gameCollection
    }


    private fun insertGamesIntoDatabase(games: List<Game>) {
        val db = dbHelper.writableDatabase

        games.forEach { game ->
            val values = ContentValues()
            values.put(DatabaseHelper.COLUMN_NAME, game.name)
            // Insert other game attributes as needed
            db.insert(DatabaseHelper.TABLE_NAME, null, values)
        }

        db.close()
    }

    fun confirm(v: View) {
        val input: EditText = findViewById(R.id.username)
        val username: String = input.text.toString()

        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()

        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://boardgamegeek.com/xmlapi2/collection?username=$username"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response: Response = client.newCall(request).execute()
                val xmlResponse = response.body?.string()

                val gameCollection = parseXmlResponse(xmlResponse)
                insertGamesIntoDatabase(gameCollection.games)

                runOnUiThread {
                    showInfoMessage()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

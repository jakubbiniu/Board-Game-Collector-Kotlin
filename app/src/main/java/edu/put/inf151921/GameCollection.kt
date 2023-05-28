package edu.put.inf151921

class GameCollection {
    val games: MutableList<Game> = mutableListOf()

    fun addGame(game: Game) {
        games.add(game)
    }

    fun getGamesList(): List<Game> {
        return games
    }

    fun getSize():Int{
        return games.size
    }
}


package edu.put.inf151921

class Game {
    var id: Int = 0
    var gameId: Long = 0
    var name: String = ""
    var description: String = ""
    var yearPublished: Int = 0
    var type:String =""

    constructor() {
        // Empty constructor
    }

    constructor(name: String) {
        this.name = name
    }



    fun add(collection: GameCollection) {
        collection.addGame(this)
    }
}

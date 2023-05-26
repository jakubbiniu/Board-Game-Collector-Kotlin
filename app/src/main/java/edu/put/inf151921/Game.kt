package edu.put.inf151921

class Game {
    var id: Int = 0
    var name: String = ""
    var imageUrl: String = ""

    constructor() {
        // Empty constructor
    }

    constructor(name: String) {
        this.name = name
    }

    constructor(id: Int, name: String, imageUrl: String) {
        this.id = id
        this.name = name
        this.imageUrl = imageUrl
    }

    fun add(collection: GameCollection) {
        collection.addGame(this)
    }
}

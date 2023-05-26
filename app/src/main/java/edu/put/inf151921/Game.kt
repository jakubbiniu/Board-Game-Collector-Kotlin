package edu.put.inf151921

class Game {
    var id: Int = 0
    var name: String = ""
    var thumbnail: String = ""
    var image: String = ""
    var description: String = ""
    var yearPublished: Int = 0
    var imageResId: Int = 0

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

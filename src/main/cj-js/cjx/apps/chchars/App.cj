package cjx.apps.chchars

import cjx.html.HTMLAny
import cjx.html.Document
import cjx.cordova.CordovaApp

class App: CordovaApp {
    def onDeviceReady() {
        val doc = Document.get()
        val question: HTMLAny = doc.getElementById("question").cast()
        val answers: List[HTMLAny] = [
            doc.getElementById("choice0").cast(),
            doc.getElementById("choice1").cast(),
            doc.getElementById("choice2").cast(),
            doc.getElementById("choice3").cast(),
        ]
        question.setInnerText("\n\n\nQUESTION")
        answers.get(0).setInnerText("\n\nCHOICE0")
        answers.get(1).setInnerText("\n\nCHOICE1")
        answers.get(2).setInnerText("\n\nCHOICE2")
        answers.get(3).setInnerText("\n\nCHOICE3")
    }
}

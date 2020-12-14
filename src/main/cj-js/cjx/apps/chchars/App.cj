package cjx.apps.chchars

import cjx.html.MouseEvent
import cjx.html.HTMLAny
import cjx.html.Document
import cjx.cordova.CordovaApp

class App: CordovaApp {
    val question: HTMLAny
    val answers: List[HTMLAny]

    def onDeviceReady(): Self {
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

        val self = App.new(question, answers)

        for answer in answers {
            answer.onClick(event -> self.onClickChoice(event))
        }

        return self
    }

    def onClickChoice(self: Self, event: MouseEvent) {
        val element: HTMLAny = event.target().cast()
        val innerText = element.innerText()
        val choice = innerText.cutFrom(innerText.size() - 1).parseInt().get()
        IO.println(choice.toString() + " clicked")
    }
}

package cjx.apps.chchars

import cjx.html.HTMLInputElement
import cjx.html.Window
import cjx.JSObject
import cj.Range
import cjx.apps.chchars.CharEntry
import cj.Http
import cjx.html.MouseEvent
import cjx.html.HTMLAny
import cjx.html.Document
import cjx.cordova.CordovaApp

class App: CordovaApp {
    val _htmlQuestion: HTMLAny
    val _htmlAnswers: List[HTMLAny]
    val _htmlTotalCorrect: HTMLAny
    val _htmlTotal: HTMLAny
    val _htmlMode: HTMLAny
    val _data: List[CharEntry]

    ## 5 integers --
    ## the first 4 are the 4 choices
    ## and the 5th is one of the 4 integers representing the correct answer.
    var _config: List[Int]

    var _selection: Nullable[Int]

    async def onDeviceReady(): Promise[Unit] {
        val doc = Document.get()
        val question: HTMLAny = doc.getElementById("question").cast()
        val answers: List[HTMLAny] = [
            doc.getElementById("choice0").cast(),
            doc.getElementById("choice1").cast(),
            doc.getElementById("choice2").cast(),
            doc.getElementById("choice3").cast(),
        ]
        question.setInnerText("QUESTION")
        answers.get(0).setInnerText("CHOICE0")
        answers.get(1).setInnerText("CHOICE1")
        answers.get(2).setInnerText("CHOICE2")
        answers.get(3).setInnerText("CHOICE3")

        Self.loadCharList().onResolve(data -> {
            val htmlLimit: HTMLInputElement = doc.getElementById("limit").cast()
            val modeButton: HTMLAny = doc.getElementById("mode").cast()

            val self = App.new(
                question,
                answers,
                doc.getElementById("correct").cast(),
                doc.getElementById("total").cast(),
                modeButton,
                data,
                [],
                Nullable.None,
            )

            val localStorage = Window.get().localStorage()
            if (localStorage.getItem("LIMIT").isEmpty()) {
                localStorage.setItem("LIMIT", "30")
            }
            htmlLimit.setValue(localStorage.getItem("LIMIT").get())
            htmlLimit.onInput(event -> {
                union htmlLimit.value().parseInt() {
                    case Some(i) {
                        localStorage.setItem("LIMIT", i.toString())
                    }
                    case None {
                        IO.eprintln("Could not parse limit value: " + htmlLimit.value())
                    }
                }
            })

            question.onClick(event -> {
                val ch = self._data.get(self._config.get(4)).char()
                Window.get().open("https://www.google.com/search?q=translate+" + ch)
            })

            modeButton.onClick(event -> self.toggleMode())

            for answer in answers {
                answer.onClick(event -> self.onClickChoice(event))
            }

            val resetButton: HTMLAny = doc.getElementById("reset").cast()
            resetButton.onClick(event -> self.onClickReset())

            self.pickNewChoices()
            self.blit()
        })
    }

    ## Whatever is chosen in '_config', make it be what's shown in the HTML
    def blit(self: Self) {
        val answer = self._data.get(self._config.get(4))
        val choices = [
            self._data.get(self._config.get(0)),
            self._data.get(self._config.get(1)),
            self._data.get(self._config.get(2)),
            self._data.get(self._config.get(3)),
        ]
        self._htmlQuestion.setInnerText(answer.char())
        val inPinyinMode = self.inPinyinMode()
        for i = 0; i < 4; i += 1 {
            val entry = choices.get(i)
            self._htmlAnswers.get(i).setInnerText(
                if (inPinyinMode) entry.pinyin() else entry.definition())
        }
    }

    def clearSelectionMarkers(self: Self) {
        self._selection = Nullable.None
        for element in self._htmlAnswers {
            element.classList().remove("correct")
            element.classList().remove("incorrect")
        }
    }

    def mode(self: Self): String {
        return if (self._htmlMode.innerText() == "pinyin") "pinyin" else "definition"
    }

    def inPinyinMode(self: Self): Bool {
        return self.mode() == "pinyin"
    }

    def setMode(self: Self, mode: String) {
        Assert.that(mode in ["pinyin", "definition"])
        self._htmlMode.setInnerText(mode)
    }

    def toggleMode(self: Self) {
        if (self.inPinyinMode()) {
            self.setMode("definition")
        } else {
            self.setMode("pinyin")
        }
    }

    def limit(self: Self): Int {
        return Window.get().localStorage().getItem("LIMIT").get().parseInt().get()
    }

    def pickNewChoices(self: Self) {
        val len = self.limit()
        IO.println("Picking from " + len + " elements")
        val choices = [
            Math.randint(len),
            Math.randint(len),
            Math.randint(len),
            Math.randint(len),
        ]
        val answer = choices.get(Math.randint(choices.size()))
        self._config = choices + [answer]
    }

    async def loadCharList(): Promise[List[CharEntry]] {
        return Http.read("data/chars.txt").await.split("\n").map(
            line -> Self._convert(line.split("|")))
    }

    def _convert(parts: List[String]): CharEntry {
        return CharEntry.new(parts.get(0).parseInt().get(), parts.get(1), parts.get(2), parts.get(3))
    }

    def total(self: Self): Int {
        return self._htmlTotal.innerText().parseInt().get()
    }

    def totalCorrect(self: Self): Int {
        return self._htmlTotalCorrect.innerText().parseInt().get()
    }

    def setTotal(self: Self, x: Int) {
        self._htmlTotal.setInnerText(x.toString())
    }

    def setTotalCorrect(self: Self, x: Int) {
        self._htmlTotalCorrect.setInnerText(x.toString())
    }

    def onClickReset(self: Self) {
        self.setTotalCorrect(0)
        self.setTotal(0)
    }

    def onClickChoice(self: Self, event: MouseEvent) {
        union self._selection {
            case Some(i) {
                # If we already have a selection, clear the selection
                # and move to the next problem
                self.clearSelectionMarkers()
                self.pickNewChoices()
                self.blit()
                self._selection = Nullable.None
            }
            case None {
                # Otherwise, process the user input
                val element: HTMLAny = event.target().cast()
                val id = element.id()
                val choice = id.cutFrom(id.size() - 1).parseInt().get()
                val correct = (
                    Range.upto(4)
                        .iter()
                        .filter(i -> self._config.get(4) == self._config.get(i))
                        .next()
                        .get()
                )
                val entry = self._data.get(self._config.get(correct))
                if choice == correct {
                    IO.println("Correct! (" + entry.char() + ")")
                    self.setTotalCorrect(self.totalCorrect() + 1)
                    self._htmlAnswers.get(choice).classList().add("correct")
                } else {
                    IO.println("Incorrect! (" + entry.char() + ")")
                    self._htmlAnswers.get(choice).classList().add("incorrect")
                    self._htmlAnswers.get(correct).classList().add("correct")
                }

                if (self.inPinyinMode()) {
                    val defn = entry.definition()
                    self._htmlQuestion.setInnerText(self._htmlQuestion.innerText() + "\n" + defn)
                } else {
                    val pinyin = entry.pinyin()
                    self._htmlQuestion.setInnerText(self._htmlQuestion.innerText() + " " + pinyin)
                }

                self._selection = Nullable.Some(choice)
                self.setTotal(self.total() + 1)
            }
        }
    }
}

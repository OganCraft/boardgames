package board.games.wizard

import spock.lang.Specification

class ScoreTest extends Specification {
    def 'the prophecy corresponds the number of wins'() {
        setup:
        def score0 = new Score(0)
        def score1 = new Score(1)
        def score2 = new Score(2)
        def score3 = new Score(5)

        when:
        score1.addOneWin()
        (1..2).each { score2.addOneWin() }
        (1..5).each { score3.addOneWin() }

        score0.computeScore()
        score1.computeScore()
        score2.computeScore()
        score3.computeScore()

        then:
        score0.score == 20
        score1.score == 30
        score2.score == 40
        score3.score == 70
    }

    def 'the prophecy does not correspond the number of wins'() {
        setup:
        def score0 = new Score(0)
        def score1 = new Score(1)
        def score2 = new Score(2)
        def score3 = new Score(5)

        when:
        score0.addOneWin()
        (1..3).each { score1.addOneWin() }
        score2.addOneWin()
        score3.addOneWin()

        score0.computeScore()
        score1.computeScore()
        score2.computeScore()
        score3.computeScore()

        then:
        score0.score == -10
        score1.score == -20
        score2.score == -10
        score3.score == -40
    }
}

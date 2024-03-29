package board.games.wizard

import board.games.Games
import board.room.RoomImpl
import board.user.User
import spock.lang.Specification

class WizardRulesTest extends Specification {
    WizardState state
    User user1, user2, user3, user4

    def setup() {
        user1 = new User('id1', 'User 1')
        def room = new RoomImpl(Games.WIZARD, user1)

        user2 = new User('id2', 'User 2')
        user3 = new User('id3', 'User 3')
        user4 = new User('id4', 'User 4')

        def users = [user1, user2, user3, user4]

        (users - user1).each { room.join(it)}

        state = new WizardState(room)
        users.each {
            state.getScore().get(it).add(new Score(0))
        }
    }

    def 'the player owns the played card'() {
        setup:
        // replace the random card by known card
        state.getCardsInHand()[user1].clear()
        state.getCardsInHand()[user1].add(new Card(1, Card.Color.BLUE))

        def card = new Card(2, Card.Color.RED)

        when:
        def message = WizardRules.playCard(user1, state, card)

        then:
        message == 'Tuto kartu hráč nemá v ruce: ' + card
    }

    def 'follow the color rule 1'() {
        setup:
        state.getPlayedCards().put(user1, new Card(1, Card.Color.RED))
        when:
        1 == 1
        // todo
        then:
        1 == 1
    }

    def 'close the round - zauberer always wins 1'() {
        setup:
        def cards = state.getPlayedCards()
        cards.put(user1, new Card(14, Card.Color.RED))
        cards.put(user2, new Card(14, Card.Color.BLUE))
        cards.put(user3, new Card(14, Card.Color.GREEN))

        when:
        WizardRules.closeTheRound(state)

        then:
        state.getRoundWinner() == user1
        state.getLastScore(user1).victories == 1
        state.getLastScore(user2).victories == 0
        state.getLastScore(user3).victories == 0
    }

    def 'close the round - zauberer always wins 2'() {
        setup:
        def cards = state.getPlayedCards()
        cards.put(user1, new Card(10, Card.Color.RED))
        cards.put(user2, new Card(14, Card.Color.BLUE))
        cards.put(user3, new Card(14, Card.Color.GREEN))

        when:
        WizardRules.closeTheRound(state)

        then:
        state.getRoundWinner() == user2
        state.getLastScore(user1).victories == 0
        state.getLastScore(user2).victories == 1
        state.getLastScore(user3).victories == 0
    }

    def 'close the round - trump wins 1'() {
        setup:
        def cards = state.getPlayedCards()
        cards.put(user1, new Card(5, Card.Color.BLUE))
        cards.put(user2, new Card(13, Card.Color.YELLOW))
        cards.put(user3, new Card(9, Card.Color.GREEN))

        state.trump = new Card(5, Card.Color.GREEN);

        when:
        WizardRules.closeTheRound(state)

        then:
        state.getRoundWinner() == user3
    }

    def 'close the round - narr provides no trump color'() {
        setup:
        def cards = state.getPlayedCards()
        cards.put(user1, new Card(5, Card.Color.BLUE))
        cards.put(user2, new Card(13, Card.Color.YELLOW))
        cards.put(user3, new Card(9, Card.Color.GREEN))

        state.trump = new Card(0, Card.Color.GREEN);

        when:
        WizardRules.closeTheRound(state)

        then:
        state.getRoundWinner() == user1
        state.getLastScore(user1).victories == 1
        state.getLastScore(user2).victories == 0
        state.getLastScore(user3).victories == 0
    }
}

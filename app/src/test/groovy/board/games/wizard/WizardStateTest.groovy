package board.games.wizard

import board.games.Games
import board.room.Room
import board.room.RoomImpl
import board.user.User
import spock.lang.Specification

class WizardStateTest extends Specification {
    def 'select next user on turn'() {
        setup:
        def user1 = new User('id1', 'User 1')
        def room = new RoomImpl(Games.WIZARD, user1)

        def user2 = new User('id2', 'User 2')
        def user3 = new User('id3', 'User 3')
        def user4 = new User('id4', 'User 4')
        room.join(user2)
        room.join(user3)
        room.join(user4)

        def state = new WizardState(room)
        def expectedUserOrder = [user1, user2, user3, user4, user1, user2]

        when:
        def actualUserOrder = expectedUserOrder.collect {
            def user = state.onTurn
            state.nextOnTurn()
            user
        }

        then:
        expectedUserOrder == actualUserOrder
    }
}

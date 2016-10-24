package spribe.task

import spock.lang.Specification

import java.util.concurrent.Executors

import static spribe.task.OpponentSeeker.Player

/**
 * @author Taras Zubrei
 */
class OpponentSeekerTest extends Specification {
    private static def opponentSeeker = new OpponentSeeker()
    private static List<Player> players
    private static List<List> tests

    def setupSpec() {
        players = [new Player("Rick", 100.0),
                   new Player("Morty", 10.0),
                   new Player("Ben", 70.5),
                   new Player("Oscar", 18.0),
                   new Player("Vanessa", 55.9),
                   new Player("Christie", 42.0),
                   new Player("Russel", 64.7)]
        tests = [[84.6, "Ben"],
                 [45.1, "Christie"],
                 [84.6, "Russel"]]
        opponentSeeker.addAll(players)
    }

    def "general use cases"() {
        when:
        Player nathan = new Player("Nathan", rating)
        then:
        opponentSeeker.findOpponent(nathan).name == opponent
        where:
        [rating, opponent] << tests
    }

    def "concurrent add"() {
        given:
        def opponentSeeker = new OpponentSeeker()
        when:
        def threadPool = Executors.newFixedThreadPool(20)
        players.stream()
                .map { player -> threadPool.submit { opponentSeeker.add(player) } }
                .forEach { it.get() }
        threadPool.shutdown()
        then:
        opponentSeeker.findOpponent(new Player("Nathan", 84.6)).name == "Ben"
    }

    def "concurrent get"() {
        given:
        def tests = [[84.6, "Ben"],
                 [45.1, "Christie"],
                 [0.0, "Morty"]]
        opponentSeeker.addAll(players)
        expect:
        tests.stream().parallel().allMatch {
            opponentSeeker.findOpponent(new Player("Nathan", it[0])).name == it[1]
        }
    }
}

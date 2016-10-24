package spribe.task

import spock.lang.Specification

import java.util.concurrent.Executors

/**
 * @author Taras Zubrei
 */
class WordCounterTest extends Specification {
    private static WordCounter wordCounter = new WordCounter()
    private static words;
    private static tests;

    def setupSpec() {
        def data = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam et placerat odio. Suspendisse eu varius augue. Suspendisse quis malesuada nibh, a mattis est. Nam vel laoreet sem. Nam quis magna tincidunt, consequat ex nec, hendrerit ex. Vestibulum elementum ipsum a mauris auctor condimentum. Nam quis posuere sem. Pellentesque bibendum, ligula at vehicula tincidunt, enim ante interdum libero, vitae viverra nisi nulla sit amet dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Maecenas fermentum pulvinar efficitur. Pellentesque nulla mi, pulvinar placerat scelerisque eget, sagittis eu sapien. Nulla gravida, dui ac elementum fringilla, lacus nunc tincidunt mi, lobortis cursus sapien turpis in urna. Nullam accumsan sollicitudin nibh, et dignissim tellus scelerisque in. Aenean pellentesque semper orci, ut cursus arcu gravida sed. Nulla mi sapien, aliquam non diam vel, auctor laoreet ex. Curabitur nec sagittis turpis, eget faucibus est. Integer a velit pharetra tellus efficitur congue. Cras sodales ut diam ac aliquet. Maecenas tempus, metus nec consectetur sodales, ipsum neque convallis nisi, sed blandit odio velit at tortor. Sed varius enim diam, ac dignissim ante finibus non. Morbi sit amet justo vitae mi eleifend auctor non non ipsum. Pellentesque auctor laoreet ligula at tincidunt. Maecenas vel mi efficitur, vehicula est eget, tincidunt velit. Sed et leo ut nulla iaculis convallis. Nullam non tincidunt urna. Vivamus condimentum semper bibendum. Integer lectus libero, mattis vel feugiat eu, cursus id sem. Maecenas at turpis nec felis viverra pretium ac sed arcu. Vestibulum pretium egestas velit. Vestibulum sed lorem et est varius ultricies volutpat ac neque. Praesent sagittis accumsan auctor. Donec iaculis tempus elementum. Curabitur ultrices interdum nulla id laoreet. Aenean ac egestas sem, vel malesuada ex. Nam lacinia ut orci ac dictum. Nulla maximus molestie eleifend. In quis orci eu enim auctor venenatis id a sapien. Fusce id dictum ipsum. Phasellus ut nulla purus. Nullam a felis gravida, iaculis nisl in, ultrices purus. Quisque ut nulla sed quam bibendum porta in vitae dui."
        words = data.replaceAll("[,.]", "").split(" ")
        tests = [["dolor", 1L],
                 ["Lorem", 2L],
                 ["dui", 3L],
                 ["Nam", 4L],
                 ["mi", 5L],
                 ["D'oh", 0L]]
        wordCounter.add(words)
    }

    def "concurrent add"() {
        given:
        def wordCounter = new WordCounter()
        when:
        def threadPool = Executors.newFixedThreadPool(20)
        Arrays.stream(words)
                .map { word -> threadPool.submit { wordCounter.add(word) } }
                .forEach { it.get() }
        threadPool.shutdown()
        then:
        wordCounter.get("dui") == 3L
    }

    def "sequential add"() {
        given:
        def wordCounter = new WordCounter()
        when:
        wordCounter.add(words)
        then:
        wordCounter.get("dui") == 3L
    }

    def "sequential get"() {
        expect:
        wordCounter.get(word) == count
        where:
        [word, count] << tests
    }

    def "concurrent get"() {
        expect:
        tests.stream().parallel().allMatch { wordCounter.get(it[0]) == it[1] }
    }

}

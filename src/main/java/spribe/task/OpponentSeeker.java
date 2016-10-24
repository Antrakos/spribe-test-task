package spribe.task;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Taras Zubrei
 */
public class OpponentSeeker {
    private NavigableSet<Player> players;

    public OpponentSeeker() {
        this.players = new ConcurrentSkipListSet<>();
    }

    public void add(Player player) {
        players.add(player);
    }

    public void addAll(Collection<? extends Player> playersCollection) {
        players.addAll(playersCollection);
    }

    public Player findOpponent(Player player) {
        Player result = chooseClosest(player, players.lower(player), players.higher(player));
        players.remove(result);
        return result;
    }

    private Player chooseClosest(Player player, Player lower, Player higher) {
        if (lower == null && higher == null)
            throw new IllegalStateException("No opponent found");
        else if (lower != null && higher == null)
            return lower;
        else if (lower == null && higher != null)
            return higher;
        else {
            if (Math.abs(player.compareTo(lower)) > Math.abs(player.compareTo(higher)))
                return higher;
            else
                return lower;
        }
    }

    static class Player implements Comparable<Player> {
        private String name;
        private Double rating;

        public Player(String name, Double rating) {
            this.name = name;
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player person = (Player) o;
            return Objects.equals(name, person.name) &&
                    Objects.equals(rating, person.rating);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, rating);
        }

        @Override
        public int compareTo(Player o) {
            return this.rating.compareTo(o.rating);
        }
    }
}

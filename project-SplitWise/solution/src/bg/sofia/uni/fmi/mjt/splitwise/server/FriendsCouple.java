package bg.sofia.uni.fmi.mjt.splitwise.server;

import java.util.Objects;

//lexicographically ordered pair of 2 usernames and one real number representing the dept
//if the dept is greater than 0 then username2 owes debt amount of money to username1
//if the dept is less then 0 then username1 owes |debt| amount of money to username2
//if the dept is 0 then username1 and username2 has no debt to each other
public class FriendsCouple {
    private String username1;
    private String username2;

    public FriendsCouple(String username1, String username2) {
        setOrderedCouple(username1, username2);
    }

    private void setOrderedCouple(String username1, String username2) {
        if (username1.compareTo(username2) < 0) {
            this.username1 = username1;
            this.username2 = username2;
        } else {
            this.username1 = username2;
            this.username2 = username1;
        }
    }

    public String getUsername1() {
        return username1;
    }

    public String getUsername2() {
        return username2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendsCouple that = (FriendsCouple) o;
        return Objects.equals(username1, that.username1) &&
                Objects.equals(username2, that.username2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username1, username2);
    }

    @Override
    public String toString() {
        return "FriendCouple{" +
                "username1='" + username1 + '\'' +
                ", username2='" + username2 + '\'' +
                '}';
    }
}
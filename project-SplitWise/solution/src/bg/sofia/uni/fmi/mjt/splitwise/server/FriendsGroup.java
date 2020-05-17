package bg.sofia.uni.fmi.mjt.splitwise.server;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FriendsGroup {
    private String groupName;
    private List<String> friendsUsernames;

    public FriendsGroup(String groupName, List<String> friendsUsernames) {
        this.groupName = groupName;
        friendsUsernames.sort(String::compareTo);
        this.friendsUsernames = friendsUsernames;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getFriendsUsernames() {
        return Collections.unmodifiableList(friendsUsernames);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendsGroup that = (FriendsGroup) o;
        return Objects.equals(groupName, that.groupName) &&
                Objects.equals(friendsUsernames, that.friendsUsernames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, friendsUsernames);
    }

    @Override
    public String toString() {
        return "FriendsGroup{" +
                "groupName='" + groupName + '\'' +
                ", friendsUsernames=" + friendsUsernames +
                '}';
    }
}

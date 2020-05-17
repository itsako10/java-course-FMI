package bg.sofia.uni.fmi.mjt.splitwise.server;

import java.util.ArrayList;
import java.util.List;

public class UserHistory {
    private int indexOfLastUnsent;
    private List<String> history;

    UserHistory() {
        history = new ArrayList<>();
    }

    void addToHistory(String historyMessage, boolean isLoggedIn) {
        if (isLoggedIn) {
            addToHistoryIfLoggedIn(historyMessage);
        } else {
            addToHistoryIfOffline(historyMessage);
        }
    }

    private void addToHistoryIfOffline(String historyMessage) {
        history.add(historyMessage);
    }

    private void addToHistoryIfLoggedIn(String hisoryMessage) {
        history.add(hisoryMessage);
        ++indexOfLastUnsent;
    }

    String getAllHistory() {
        StringBuilder allHistory = new StringBuilder();
        for (String elementOfHistory : history) {
            allHistory.append(elementOfHistory).append("\n");
        }

        return allHistory.toString().equals("") ?
                "There is no history to show." : allHistory.toString();
    }

    String getUnsentHistory() {
        StringBuilder unsentHistory = new StringBuilder();
        for (int i = indexOfLastUnsent; i < history.size(); ++i) {
            unsentHistory.append(history.get(i)).append("\n");
        }

        return unsentHistory.toString().equals("") ?
                "There are no notifications to show." : unsentHistory.toString();
    }

    void markTheWholeHistoryAsSent() {
        indexOfLastUnsent = history.size();
    }

    @Override
    public String toString() {
        return "UserHistory{" +
                "indexOfLastUnsent=" + indexOfLastUnsent +
                ", history=" + history +
                '}';
    }
}
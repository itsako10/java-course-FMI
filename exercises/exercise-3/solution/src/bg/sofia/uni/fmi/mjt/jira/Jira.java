package bg.sofia.uni.fmi.mjt.jira;

import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;
import bg.sofia.uni.fmi.mjt.jira.interfaces.Filter;
import bg.sofia.uni.fmi.mjt.jira.interfaces.Repository;
import bg.sofia.uni.fmi.mjt.jira.issues.Issue;

public class Jira implements Filter, Repository {
    private Issue[] issues;
    private int issueCounter;
    private static final int MAX_ISSUES_NUMBER = 100;

    public Jira() {
        issues = new Issue[MAX_ISSUES_NUMBER];
    }

    @Override
    public Issue find(String issueID) {
        if(issueID == null) {
            throw new RuntimeException("Null value!");
        }

        for(int i = 0; i < issueCounter; ++i) {
            if(issues[i].getIssueID().equals(issueID)) {
                return issues[i];
            }
        }
        return null;
    }

    @Override
    public void addIssue(Issue issue) {
        if (issueCounter >= MAX_ISSUES_NUMBER) {
            throw new RuntimeException("There is no more space for issues!");
        }

        if(find(issue.getIssueID()) != null) {
            throw new RuntimeException("Issue exists!");
        }

        issues[issueCounter++] = issue;
    }

    public void addActionToIssue(Issue issue, WorkAction action, String actionDescription) {
        if(issue == null) {
            throw new RuntimeException("Issue is null!");
        }
        else if (actionDescription == null) {
            throw new RuntimeException("actionDescription is null!");
        }

        Issue helper = find(issue.getIssueID());
        helper.addAction(action, actionDescription);
    }
    public void resolveIssue(Issue issue, IssueResolution resolution) {
        find(issue.getIssueID()).resolve(resolution);
    }
}

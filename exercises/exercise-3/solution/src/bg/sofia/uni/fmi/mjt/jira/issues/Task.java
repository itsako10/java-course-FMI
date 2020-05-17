package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;

public class Task extends Issue {
    public Task(IssuePriority priority, Component component, String description) {
        super(priority, component, description);
    }

    @Override
    public void addAction(WorkAction action, String description) {
        if (action == WorkAction.FIX || action == WorkAction.IMPLEMENTATION || action == WorkAction.TESTS) {
            throw new RuntimeException("Wrong actions!");
        }
        super.addAction(action, description);
    }

    @Override
    public void resolve(IssueResolution resolution) {
        this.setResolution(resolution);
        setLastModifiedOn();
    }
}
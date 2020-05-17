package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueStatus;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;
import bg.sofia.uni.fmi.mjt.jira.interfaces.Repository;

import java.time.LocalDateTime;

public abstract class Issue {
    private static int IDNumber;
    private String ID;
    private String description;
    private IssuePriority priority;
    private IssueResolution resolution;
    private IssueStatus status;
    private Component component;
    private String[] actionLog;
    private static final int MAX_ACTIONS_NUMBER = 20;
    private int actionCounter;
    private LocalDateTime createdOn;
    private LocalDateTime lastModifiedOn;

    public Issue(IssuePriority priority, Component component, String description) {
        this.priority = priority;
        this.component = component;
        this.description = description;
        setResolution(IssueResolution.UNRESOLVED);
        setStatus(IssueStatus.OPEN);
        actionLog = new String[MAX_ACTIONS_NUMBER];
        this.ID = component.getShortName() + "-" + IDNumber;
        ++IDNumber;
        createdOn = LocalDateTime.now();
        lastModifiedOn = LocalDateTime.now();
    }

    public String getIssueID() {
        return ID;
    }

    public String getDescription() {
        return description;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public IssueResolution getResolution() {
        return resolution;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public Component getComponent() {
        return component;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }

    public String[] getActionLog(){
        return actionLog;
    }

    public static String getWorkAction(String actionLog) {
        int lastIndex = actionLog.indexOf(':');
        return actionLog.substring(0, lastIndex).toUpperCase();
    }

    public boolean isActionInActionLog(String action) {
        for(int i = 0; i < actionCounter; ++i) {
            if(getWorkAction(getActionLog()[i]).equals(action)) {
                return true;
            }
        }
        return false;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
        setLastModifiedOn();
    }

    public void setResolution(IssueResolution resolution) {
        this.resolution = resolution;
        setLastModifiedOn();
    }

    public void addAction(WorkAction action, String description) {
        if(actionCounter >= MAX_ACTIONS_NUMBER) {
            throw new RuntimeException("There is no more space for actions!");
        }
        actionLog[actionCounter++] = action.toString().toLowerCase() + ": " + description;
        setLastModifiedOn();
    }

    public void setLastModifiedOn() {
        lastModifiedOn = LocalDateTime.now();
    }

    public abstract void resolve(IssueResolution resolution);
}

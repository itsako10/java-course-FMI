package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;

public class Bug extends Issue {
    public Bug(IssuePriority priority, Component component, String description) {
        super(priority, component, description);
    }

    @Override
    public void resolve(IssueResolution resolution) {
        if(!isActionInActionLog("FIX") || !isActionInActionLog("TESTS")) {
            throw new RuntimeException("Wrong actions!");
        }
        this.setResolution(resolution);
        setLastModifiedOn();
    }
}

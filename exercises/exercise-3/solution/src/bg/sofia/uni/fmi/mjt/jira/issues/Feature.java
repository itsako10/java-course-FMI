package bg.sofia.uni.fmi.mjt.jira.issues;

import bg.sofia.uni.fmi.mjt.jira.enums.IssuePriority;
import bg.sofia.uni.fmi.mjt.jira.enums.IssueResolution;
import bg.sofia.uni.fmi.mjt.jira.enums.WorkAction;

public class Feature extends Issue {
    public Feature(IssuePriority priority, Component component, String description) {
        super(priority, component, description);
    }

    @Override
    public void resolve(IssueResolution resolution) {
        if (!isActionInActionLog("DESIGN") || !isActionInActionLog("IMPLEMENTATION") || !isActionInActionLog("TESTS")) {
            throw new RuntimeException("Wrong actions!");
        }
        this.setResolution(resolution);
        setLastModifiedOn();
    }
}

package main;

public class GroupProps {
    private Integer groupId;
    private Integer startLine;
    private Integer endLine;
    private Integer editsNo;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }

    public Integer getEditsNo() {
        return editsNo;
    }

    public void setEditsNo(Integer editsNo) {
        this.editsNo = editsNo;
    }

    @Override
    public String toString() {
        return "GroupProps{" +
                "groupId=" + groupId +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
                ", editsNo=" + editsNo +
                '}';
    }
}

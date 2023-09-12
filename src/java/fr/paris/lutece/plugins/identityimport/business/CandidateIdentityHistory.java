package fr.paris.lutece.plugins.identityimport.business;

public class CandidateIdentityHistory {
    private int _nId;
    private int _nWfResourceHistoryId;
    private String _strStatus;
    private String _strComment;

    public int getId() {
        return _nId;
    }

    public void setId(int _nId) {
        this._nId = _nId;
    }

    public int getWfResourceHistoryId() {
        return _nWfResourceHistoryId;
    }

    public void setWfResourceHistoryId(int _nWfResourceHistoryId) {
        this._nWfResourceHistoryId = _nWfResourceHistoryId;
    }

    public String getStatus() {
        return _strStatus;
    }

    public void setStatus(String _strStatus) {
        this._strStatus = _strStatus;
    }

    public String getComment() {
        return _strComment;
    }

    public void setComment(String _strComment) {
        this._strComment = _strComment;
    }
}

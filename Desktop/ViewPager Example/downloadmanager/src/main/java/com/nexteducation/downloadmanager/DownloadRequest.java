package com.nexteducation.downloadmanager;

import java.util.ArrayList;

/**
 * Created by suresh on 2/11/2016.
 */
public class DownloadRequest
{
    private String nodeId;
    private String nodeName;
    private String label;
    private int itemsLength;
    private int completedLength;
    private String nodeStatus;
    private int versionType;
    private ArrayList<DownloadItemVO> downloadItem=new ArrayList<DownloadItemVO>();
    private DownloadSettings downloadSettings=new DownloadSettings();


    public void setDownloadItemVOs(ArrayList<DownloadItemVO> downloadItemVOs)
    {
        downloadItem = downloadItemVOs;
    }

    public ArrayList<DownloadItemVO> getDownloadItemVOs()
    {
        return downloadItem;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeId(String nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getNodeStatus()
    {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus)
    {
        this.nodeStatus = nodeStatus;
    }

    public String getLabel()
    {
        return label;
    }

    public int getVersionType()
    {
        return versionType;
    }

    public void setVersionType(int versionType)
    {
        this.versionType = versionType;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean retValue=false;
        if(object instanceof DownloadRequest)
        {
           DownloadRequest dr= (DownloadRequest) object;
           retValue=dr.getNodeId().equalsIgnoreCase(this.getNodeId());
        }
        return retValue;
    }

    public int getCompletedLength()
    {
        return completedLength;
    }

    public void setCompletedLength(int completedLength)
    {
        this.completedLength = completedLength;
    }

    public DownloadSettings getDownloadSettings()
    {
        return downloadSettings;
    }
    
}

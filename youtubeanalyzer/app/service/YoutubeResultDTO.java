package service;

/**
 * 
 * @author kanishak kapur
 * Class encapsulates all video details to be displayed
 */
public class YoutubeResultDTO {
	
	
	/**
	 * Sentiment  +1 positive -1 negative 0 neutral
	 */
	private int sentiment;
	private String videoTitle;
	private String owner;
	private Long videoCount;
	private Long timeLapsed;
	
	public int getSentiment() {
		return sentiment;
	}
	public void setSentiment(int sentiment) {
		this.sentiment = sentiment;
	}
	public String getVideoTitle() {
		return videoTitle;
	}
	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Long getVideoCount() {
		return videoCount;
	}
	public void setVideoCount(Long videoCount) {
		this.videoCount = videoCount;
	}
	public Long getTimeLapsed() {
		return timeLapsed;
	}
	public void setTimeLapsed(Long timeLapsed) {
		this.timeLapsed = timeLapsed;
	}
	
	
}

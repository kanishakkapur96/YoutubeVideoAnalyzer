package service;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.vdurmont.emoji.EmojiParser;

import play.libs.Json;

/**
 * @author kanishak kapur 
 * Class encapsulates service methods that call youtube data api
 */
public class YoutubeServiceAsync {

	private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	public static final JacksonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Set of happy and sad emoticons
	 */
	public static final String[] HAPPY_SET_VALUES = { "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "😉",
			"😊", "😇", "😍", "🤩", "😘", "😗", "☺", "😚", "😙", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "😎",
			"😸", "😹", "❤" };
	public static String[] SAD_SET_VALUES = { "😒", "😬", "🤥", "😔", "🤒", "🤕", "🤢", "🤮", "🤧", "🤯", "😕", "😟",
			"🙁", "☹", "😯", "😳", "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱", "😖", "😣", "😞", "😓", "😩", "😫",
			"😤", "😡", "🤬", "👿", "💀", "☠", "💩", "🙀", "😻", "😼", "😽", "😿", "🥺", "🥺", "🥺", "🥺", "🥺" };
	public static final Set<String> HAPPY_SET = new HashSet<String>(Arrays.asList(HAPPY_SET_VALUES));
	public static final Set<String> SAD_SET = new HashSet<String>(Arrays.asList(SAD_SET_VALUES));

	private YouTube youtube;

	/**
	 * @author kanishak kapur
	 * @return com.google.api.services.youtube.YouTube
	 */
	public static YouTube getService() {
		NetHttpTransport httpTransport;
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("General security exception");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IO exception");
		}
		return new YouTube.Builder(httpTransport, JSON_FACTORY, null).setApplicationName("ss")
				.setYouTubeRequestInitializer(new YouTubeRequestInitializer("<API_KEY>"))
				.build();
	}
	

	/**
	 * @author kanishak kapur
	 * @return CompletionStage<JsonNode>
	 */
	public CompletionStage<JsonNode> createPipeline(String keyword) {
		
		return supplyAsync(()->{
				youtube= YoutubeServiceAsync.getService();
				return youtube;
			}).
				thenApply((YouTube youtube) ->  {
					try {
						return youtube.search().list("id,snippet");
					} catch (IOException e1) {
						e1.printStackTrace();
						throw new UncheckedIOException(e1);
					}
				}).
				thenApply((YouTube.Search.List search) -> {
					search.setQ(keyword);
					search.setOrder("date");
					search.setType("video");
					search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
					return search;
				}).
				thenApply(search -> {
					try {
						return search.execute();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					return new SearchListResponse();
				})
				.exceptionally(exception -> handleException(exception)).
				thenApply((SearchListResponse searchResponse) -> searchResponse.getItems()).
				thenCompose(currentResultList -> createResponseAsync(currentResultList)).thenApply(object ->{
					JsonNode json = Json.toJson(object);
					return json;
				})
				.exceptionally(throwable -> handleException(throwable));
		
				
	}
	
	/**
	 * @author kanishak kapur
	 */
	public static <T> T handleException(Throwable exception) {
		exception.printStackTrace();
		throw new RuntimeException(exception);
	}
	
	/**
	 * @author kanishak kapur
	 * @return Integer
	 */
	public Integer getSentiment(Video item){
		
		YouTube.CommentThreads.List request;
		try {
			request = youtube.commentThreads().list("snippet");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create comment thread");
		}
		
		long happyCount = 0, sadCount = 0;
		int totalCount = 0;
		int sentiment = 0;
		
		int compare = item.getStatistics().getCommentCount()!=null ? item.getStatistics().getCommentCount().compareTo(BigInteger.ZERO): 0;
		if (compare > 0) {
			CommentThreadListResponse commentResponse;
			try {
				commentResponse = request.setVideoId(item.getId()).setMaxResults(100L).execute();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot find comments");
			}

			StringBuilder sb = new StringBuilder("");
			commentResponse.getItems().parallelStream().map((CommentThread th) -> {
				return th.getSnippet().getTopLevelComment().getSnippet().getTextDisplay();
			}).collect(Collectors.toList()).forEach(comment -> {
				sb.append(comment);
				sb.append(" ");
			});

			String comment = sb.toString();

			List<String> allEmoji = EmojiParser.extractEmojis(comment);
			totalCount = allEmoji.size();
			if (totalCount > 0) {
				happyCount = allEmoji.parallelStream().filter(HAPPY_SET::contains).count();
				sadCount = allEmoji.parallelStream().filter(SAD_SET::contains).count();

				if (((double) happyCount / totalCount) >= 0.7) {
					sentiment = 1;
				} else if (((double) sadCount / totalCount) >= 0.7) {
					sentiment = -1;
				}
			}
		}
		return sentiment;
		
	}
	
	
	
	/**
	 * @author kanishak kapur
	 * @return CompletionStage<List<YoutubeResultDTO>>
	 */
	public CompletionStage<List<YoutubeResultDTO>> createResponseAsync(List<SearchResult> searchResultList) {
				
		return supplyAsync(()->{
			return searchResultList.parallelStream().map(res -> res.getId().getVideoId())
					.collect(Collectors.joining(","));
		}).thenApply(videoIdsString ->{
			YouTube.Videos.List videoRequest;
			VideoListResponse response;
			try {
				videoRequest = youtube.videos().list("snippet,contentDetails,statistics");
				response = videoRequest.setId(videoIdsString).execute();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not find videos ");
			}
			return response;
		}).thenApply(response ->{
			
			return response.getItems().parallelStream().map(item -> {
				
				YoutubeResultDTO resultDTO = new YoutubeResultDTO();
				Long viewCount = item.getStatistics().getViewCount().longValue();
				resultDTO.setVideoTitle(item.getSnippet().getTitle());
				resultDTO.setOwner(item.getSnippet().getChannelTitle());
				resultDTO.setTimeLapsed(item.getSnippet().getPublishedAt().getValue());
				resultDTO.setVideoCount(viewCount);
				resultDTO.setSentiment(getSentiment(item));
				return resultDTO;

			}).collect(Collectors.toList());
		});
	}
}

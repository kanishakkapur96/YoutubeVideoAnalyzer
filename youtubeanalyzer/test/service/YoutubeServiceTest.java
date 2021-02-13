package service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;


/**
 * @author kanishak.kapur
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({YoutubeServiceAsync.class,SearchResult.class,YouTube.class,ResourceId.class,SearchListResponse.class,YouTube.CommentThreads.List.class,CommentThreadListResponse.class,YouTube.Search.List.class,YouTube.Videos.List.class})
public class YoutubeServiceTest {	
	
	@InjectMocks
	YoutubeServiceAsync youtubeService;
	
	@Mock
	SearchListResponse searchResponse;
		
	@Mock
	CommentThreadListResponse commentResponse;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	YouTube.Videos.List videoRequest;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	YouTube.CommentThreads.List request;
	
	@Mock
	YouTube.Search.List search;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	YouTube youtube;
	
	
	@Test
	public void testCreatePipeline() throws IOException {
		
		PowerMockito.mockStatic(YoutubeServiceAsync.class);
		List<SearchResult> searchResultList=new ArrayList<>();
		SearchResult searchResult1= new SearchResult();
		SearchResult searchResult2= new SearchResult();
		SearchResult searchResult3= new SearchResult();
		SearchResult searchResult4= new SearchResult();
		
		searchResult1.setId(new ResourceId().setVideoId("100"));
		searchResult2.setId(new ResourceId().setVideoId("101"));
		searchResult3.setId(new ResourceId().setVideoId("102"));
		searchResult4.setId(new ResourceId().setVideoId("103"));
		
		
		searchResultList.add(searchResult1);
		searchResultList.add(searchResult2);
		searchResultList.add(searchResult3);
		searchResultList.add(searchResult4);
		
		
		VideoListResponse response=new VideoListResponse();
		Video item1=new Video().setStatistics(new VideoStatistics().setCommentCount(new BigInteger("90")));
		item1.getStatistics().setViewCount(new BigInteger("8800"));
		item1.setSnippet(new VideoSnippet().setTitle("Video1"));
		item1.getSnippet().setChannelTitle("CHANNEL 1");
		item1.getSnippet().setPublishedAt(new DateTime(System.currentTimeMillis()));
		
		Video item2=new Video().setStatistics(new VideoStatistics().setCommentCount(new BigInteger("190")));
		item2.getStatistics().setViewCount(new BigInteger("1110"));
		item2.setSnippet(new VideoSnippet().setTitle("Video2"));
		item2.getSnippet().setChannelTitle("CHANNEL 2");
		item2.getSnippet().setPublishedAt(new DateTime(System.currentTimeMillis()));
		
		Video item3=new Video().setStatistics(new VideoStatistics().setCommentCount(new BigInteger("900")));
		item3.getStatistics().setViewCount(new BigInteger("1880"));
		item3.setSnippet(new VideoSnippet().setTitle("Video3"));
		item3.getSnippet().setChannelTitle("CHANNEL 3");
		item3.getSnippet().setPublishedAt(new DateTime(System.currentTimeMillis()));
		
		List<Video> videoList=new ArrayList<>();
		videoList.add(item1);
		videoList.add(item2);
		videoList.add(item3);
		
		response.setItems(videoList);
		
		YouTube.Videos.List videoRequest=Mockito.mock(YouTube.Videos.List.class);
		when(youtube.videos().list("snippet,contentDetails,statistics")).thenReturn(videoRequest);
		when(youtube.search().list("id,snippet")).thenReturn(search);
		when(search.execute()).thenReturn(searchResponse);
		when(searchResponse.getItems()).thenReturn(searchResultList);
		when(videoRequest.setId(anyString())).thenReturn(videoRequest);
		when(videoRequest.execute()).thenReturn(response);
		
		 when(YoutubeServiceAsync.getService()).thenReturn(youtube);
		 
		 when(youtube.commentThreads().list("snippet")).thenReturn(request);
		 when(request.setVideoId(anyString()).setMaxResults(100L).execute()).thenReturn(commentResponse);
		 
		 List<CommentThread> commentThreadList=new ArrayList<>();
		 CommentThread th1= new CommentThread().setSnippet(new CommentThreadSnippet().
				 setTopLevelComment(new Comment().setSnippet(new CommentSnippet().setTextDisplay("😀, 😃,😄, 😁"))));
		 
		 CommentThread th2= new CommentThread().setSnippet(new CommentThreadSnippet().
				 setTopLevelComment(new Comment().setSnippet(new CommentSnippet().setTextDisplay("😀, 😃,😄, 😁"))));
		 
		 CommentThread th3= new CommentThread().setSnippet(new CommentThreadSnippet().
				 setTopLevelComment(new Comment().setSnippet(new CommentSnippet().setTextDisplay("😀, 😃,😄, 😁"))));
		 
		 commentThreadList.add(th1);
		 commentThreadList.add(th2);
		 commentThreadList.add(th3);
		 
		 when(commentResponse.getItems()).thenReturn(commentThreadList);
		 
		 CompletableFuture<JsonNode> result=(CompletableFuture<JsonNode>) youtubeService.createPipeline("Dogs");
		 try {
			JsonNode res= result.get();
			System.out.println("RESULT");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		 
	}
}

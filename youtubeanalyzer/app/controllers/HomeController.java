package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.mvc.*;
import service.YoutubeResultDTO;
import service.YoutubeServiceAsync;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 * @author kanishak kapur
 */
public class HomeController extends Controller {
	
	List<String> testList = Arrays.asList("Hello","I","am","Java");
	List<YoutubeResultDTO> response=new ArrayList<>();

    /**
     * Displays results for a hardcoded search term.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     * @author kanishak kapur
     */
	public CompletionStage<Result> index(String keyWord) {
		YoutubeServiceAsync youtube = new YoutubeServiceAsync();
		return youtube.createPipeline(keyWord).thenApply(object ->{
				return ok(object);
		});
		
	}	
    public Result homePage() {
        return ok(views.html.index.render(response));
    }
}

package test1pkg;

import java.io.FileWriter;
import java.io.PrintWriter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GetPlace {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("Qx9tgGMFYtq0inqKTD53ccS4X")
		  .setOAuthConsumerSecret("oZ8nBZaoz5YEciXgXj38x6XTC8YU33QDmvkeLougvm8e2fHbJt")
		  .setOAuthAccessToken("2303507334-MLg5zeIrbDeKfaNUHMzckwUBHFl6xD7tRAfgz0h")
		  .setOAuthAccessTokenSecret("xsLFtTfXdJVsPQWxVHorQs3PRWkttgwopLqES0HQtpJVc")
		  .setHttpProxyHost("192.168.36.22")
		  .setHttpProxyPort(3128)
		  .setHttpProxyUser("cs12b1006")
		  .setHttpProxyPassword("123");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		try {
			
			Query query = new Query("place:4516bcae4ad207f1");
			
			query.setCount(5);
			//query.sinceId(638590092513144832L);
			int searchResultCount=1;
			
			int count=0;
			long lowestTweetId = Long.MAX_VALUE;
			long highestTweetId = 0;
			
			//while(searchResultCount !=0){
				
				QueryResult result = twitter.search(query);
				
				searchResultCount = result.getTweets().size();
				
				System.out.println(searchResultCount);
				
				for (Status status : result.getTweets()) {
		           
//					System.out.println("\n@" + status.getUser().getName() + ": "
//		               + status.getCreatedAt() + status.getUser().get);
					
					if(status.getPlace() != null){
						System.out.println("Place found!!");
						PrintWriter writer = new PrintWriter(new FileWriter("place.txt", true));
						
						writer.print(status.getPlace().getId());
						writer.print(","+status.getPlace().getName());
						writer.print(","+status.getPlace().getCountry());
						writer.print(","+status.getPlace().getCountryCode());
						writer.print(","+status.getPlace().getPlaceType());
						writer.print(","+'"'+status.getPlace().getFullName()+'"');
						writer.print(","+status.getPlace().getStreetAddress());
						writer.println("");
						writer.close();
						
						break;
					}
		          // break;
		           
		           
		            
		          // count++;
		          }
				
				//System.out.println(count);
			//}
		         
			
				System.out.println("--------------------------------------------------------");
			//	System.out.println(highestTweetId);
			//System.out.println(count);
	          
	          
			

        }catch(Exception e ){
        	
        }
	
	
	}

}




package ca.bsolomon.gw2events.level.dynamodb;

import java.util.List;

import ca.bsolomon.gw2events.level.dynamodb.model.MapInfo;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDbService {

	private AmazonDynamoDBClient dynamoDB = null;
	private DynamoDBMapper mapper = null;
		
	public void initDynamoDbClient() throws Exception {
		dynamoDB = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());
        Region region = Region.getRegion(Regions.US_EAST_1);
        dynamoDB.setRegion(region);
        
        mapper = new DynamoDBMapper(dynamoDB);
    }
	
	public List<MapInfo> getAllMaps() {
		return MapLevelDao.getAllMaps(mapper);
	}
	
	public List<MapInfo> getMapRange(int lowRange, int highRange) {
		return MapLevelDao.getMapRange(mapper, lowRange, highRange);
	}
	
	public MapInfo getMapById(int mapId) {
		return MapLevelDao.getMapById(mapper, mapId);
	}
}

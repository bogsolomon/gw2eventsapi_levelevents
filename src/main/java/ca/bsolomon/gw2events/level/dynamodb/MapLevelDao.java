package ca.bsolomon.gw2events.level.dynamodb;

import java.util.ArrayList;
import java.util.List;

import ca.bsolomon.gw2events.level.dynamodb.model.MapInfo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

public class MapLevelDao {

	public static List<MapInfo> getAllMaps(DynamoDBMapper mapper) {
		List<MapInfo> mapInfo = new ArrayList<>();

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		PaginatedScanList<MapInfo> result = mapper.scan(MapInfo.class,
				scanExpression);

		for (MapInfo info : result) {
			mapInfo.add(info);
		}

		return mapInfo;
	}

	public static List<MapInfo> getMapRange(DynamoDBMapper mapper,
			Integer lowRange, Integer highRange) {
		List<MapInfo> mapInfo = new ArrayList<>();

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		scanExpression
				.addFilterCondition(
						"LowLevelRange",
						new Condition().withComparisonOperator(
								ComparisonOperator.GE)
								.withAttributeValueList(
										new AttributeValue().withN(lowRange
												.toString())));
		scanExpression
				.addFilterCondition(
						"HighLevelRange",
						new Condition().withComparisonOperator(
								ComparisonOperator.LE)
								.withAttributeValueList(
										new AttributeValue().withN(highRange
												.toString())));

		PaginatedScanList<MapInfo> result = mapper.scan(MapInfo.class,
				scanExpression);

		for (MapInfo info : result) {
			mapInfo.add(info);
		}

		return mapInfo;
	}

	public static MapInfo getMapById(DynamoDBMapper mapper, int mapId) {
		return mapper.load(MapInfo.class, mapId);
	}
	
	public static void saveMap(DynamoDBMapper mapper, MapInfo map) {
		mapper.save(map);
	}

	public static void batchSaveMap(DynamoDBMapper mapper, List<MapInfo> maps) {
		List<FailedBatch> failed = mapper.batchSave(maps);
		
		if (failed.size() > 0) {
			System.out.println(failed.size()+" items failed to save.");
			System.out.println(failed.get(0).getException());
		}
	}
}

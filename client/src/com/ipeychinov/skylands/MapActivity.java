package com.ipeychinov.skylands;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ipeychinov.skylands.model.Coordinates;
import com.ipeychinov.skylands.model.WorldMap;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class MapActivity extends ListActivity {

	private AsyncService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);

		init();
		loadMapData();
	}

	private void init() {
		this.service = new AsyncService(this);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View v, int position,
					long id) {
				Coordinates coordinates = (Coordinates) list
						.getItemAtPosition(position);
				startOtherlandActivity(coordinates.getId());
			}

		});
	}

	protected void startOtherlandActivity(long coordinatesId) {
		finish();
		Intent intent = new Intent(this, OtherlandActivity.class);
		intent.putExtra(OtherlandActivity.LAND_COORDINATES_ID,
				String.valueOf(coordinatesId));
		startActivity(intent);
	}

	private void loadMapData() {

		Map<String, String> paramMap = new HashMap<String, String>(1);
		paramMap.put("id", "6");

		Gson gson = new GsonBuilder().create();
		final String request = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() { // TODO Auto-generated method stub
				service.doRequest(request, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						update(content);
					}

					@Override
					public void onError(String message) {
						notifyError("Failed to load coordinates");
					}

				});
			}

		});
		thread.run();

	}

	protected void notifyError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}

	protected void update(String content) {
		WorldMap map = parseResponse(content);

		List<Coordinates> coordinates = map.getCoordinates();
		ArrayAdapter<Coordinates> coordinatesAdapter = new ArrayAdapter<Coordinates>(
				this, R.layout.activity_map, R.id.map_country_name, coordinates) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.list_item_map, null);
				}

				Coordinates coordinate = getItem(position);

				String name = coordinate.getCountryName();
				TextView countryNameText = (TextView) convertView
						.findViewById(R.id.map_country_name);
				countryNameText.setText(name);

				int x = coordinate.getX();
				int y = coordinate.getY();
				String xy = "[" + String.valueOf(x) + ";" + String.valueOf(y)
						+ "]";
				TextView xyText = (TextView) convertView
						.findViewById(R.id.map_coordinates);
				xyText.setText(xy);

				return convertView;
			}
		};

		setListAdapter(coordinatesAdapter);
	}

	private WorldMap parseResponse(String content) {
		Gson gson = new GsonBuilder().create();

		Type listType = new TypeToken<ArrayList<Coordinates>>() {
		}.getType();
		List<Coordinates> coordinates = gson.fromJson(content, listType);

		WorldMap response = new WorldMap();
		response.setCoordinates(coordinates);

		return response;
	}

	@Override
	public void onBackPressed() {
		startSkylandsActivity();
	}

	private void startSkylandsActivity() {
		finish();
		Intent intent = new Intent(this, SkylandsActivity.class);
		startActivity(intent);
	}
}

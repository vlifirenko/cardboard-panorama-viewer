package ru.cplx.glasses.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.cplx.glasses.vo.MarkerVo;
import ru.cplx.glasses.vo.SceneVo;
import android.util.JsonReader;
import android.util.JsonToken;

public class JsonParser {

	public List<SceneVo> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readMessagesArray(reader);
		} finally {
			reader.close();
		}
	}

	private List<SceneVo> readMessagesArray(JsonReader reader)
			throws IOException {
		List<SceneVo> messages = new ArrayList<SceneVo>();

		reader.beginObject();
		while (reader.hasNext()) {
			if (reader.nextName().equals("scenes")) {
				reader.beginArray();
				while (reader.hasNext()) {
					messages.add(readScene(reader));
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return messages;
	}

	private SceneVo readScene(JsonReader reader) throws IOException {
		String id = null;
		String name = null;
		String img = null;
		int rotation = 0;
		List<MarkerVo> markers = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String jsonName = reader.nextName();
			if (jsonName.equals("id")) {
				id = reader.nextString();
			} else if (jsonName.equals("name")) {
				name = reader.nextString();
			} else if (jsonName.equals("img")) {
				img = reader.nextString();
			} else if (jsonName.equals("markers")
					&& reader.peek() != JsonToken.NULL) {
				reader.beginArray();
				markers = readMarkers(reader);
				reader.endArray();
			} else if (jsonName.equals("rotation")) {
				rotation = reader.nextInt();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new SceneVo(id, name, img, markers, rotation);
	}

	private List<MarkerVo> readMarkers(JsonReader reader) throws IOException {
		List<MarkerVo> markers = new ArrayList<MarkerVo>();

		while (reader.hasNext()) {
			String scene = null;
			int[] angles = new int[2];
			reader.beginObject();
			while (reader.hasNext()) {
				String jsonName = reader.nextName();
				if (jsonName.equals("angles")) {
					reader.beginArray();
					int i = 0;
					while (reader.hasNext()) {
						angles[i] = reader.nextInt();
						i++;
					}
					reader.endArray();
				} else if (jsonName.equals("scene")) {
					scene = reader.nextString();
				}
			}
			markers.add(new MarkerVo(scene, angles));
			reader.endObject();
		}
		return markers;
	}
}

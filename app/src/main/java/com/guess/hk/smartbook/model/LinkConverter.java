package com.guess.hk.smartbook.model;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class LinkConverter {

	private Gson gson = new Gson();

	@TypeConverter
	public String fromHobbies(List<Link> hobbies) {
		return gson.toJson(hobbies);
	}

	@TypeConverter
	public List<Link> toHobbies(String data) {
		Type listType = new TypeToken<List<Link>>() {}.getType();
		return gson.fromJson(data, listType);
	}
}

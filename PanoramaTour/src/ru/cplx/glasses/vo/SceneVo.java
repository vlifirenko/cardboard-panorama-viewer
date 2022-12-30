package ru.cplx.glasses.vo;

import java.util.List;

public class SceneVo {

	public String id;
	public String name;
	public String img;
	public List<MarkerVo> markers;
	public int rotation;

	public SceneVo(String id, String name, String img, List<MarkerVo> markers, int rotation) {
		this.id = id;
		this.name = name;
		this.img = img;
		this.markers = markers;
		this.rotation = rotation;
	}
}

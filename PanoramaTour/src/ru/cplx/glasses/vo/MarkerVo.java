package ru.cplx.glasses.vo;

public class MarkerVo {
	
	public int[] angles = new int[2];
	public String scene;
	
	public MarkerVo(String scene, int[] angles) {
		this.angles = angles;
		this.scene = scene;
	}	
}

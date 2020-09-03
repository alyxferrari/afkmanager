package com.alyxferrari.afkmanager;
public class CustomVector3 {
	public double x;
	public double y;
	public double z;
	public CustomVector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof CustomVector3) {
			CustomVector3 temp = (CustomVector3) object;
			if (x == temp.x && y == temp.y && z == temp.z) {
				return true;
			}
		}
		return false;
	}
}
package analogwatch;

class SphericalCoordinate {
	private double myX;
	private double myY;

	SphericalCoordinate(double x, double y) {
		myX = x;
		myY = y;
	}

	double getXEntry() {
		return myX;
	}

	double getYEntry() {
		return myY;
	}
}
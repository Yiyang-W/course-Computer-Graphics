package windowing.drawable;

public class ZBufferDrawable extends DrawableDecorator{
	private double[][] zBuffer;
	private int width, height;

	public ZBufferDrawable(Drawable delegate) {
		super(delegate);
		width = delegate.getWidth();
		height = delegate.getHeight();

		this.zBuffer = new double[width][];
		for (int i = 0; i < width; ++i) {
			zBuffer[i] = new double[height];
			for (int j = 0; j < height; ++j) {
				zBuffer[i][j] = -200;
			}
		}
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
//		System.out.println(z);
		if (x >= width  ||  y >= height || x < 0  || y < 0) {
			System.out.println("("+x+" ,"+y+")");
		}
		if (z > zBuffer[x][y]) {
			zBuffer[x][y] = z;
			super.setPixel(x, y, z, argbColor);
		}
	}

	public void clear(){
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				zBuffer[i][j] = -200;
			}
		}
	}
}

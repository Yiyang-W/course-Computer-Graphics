package client;

import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;


public class ColoredDrawable extends DrawableDecorator {
	private int argb;

	public ColoredDrawable(Drawable delegate, int argb) {
		super(delegate);
		this.argb = argb;
	}

	@Override
	public void fill(int argbColor, double z){ super.fill(argb, z);}

}

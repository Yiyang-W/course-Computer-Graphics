

In assignment 4, first I repair the depth bug of the last one. I rewrote the depth part and using 1/z for interpolate, and it works now. 

And for this assignment, mainly I finished three shading methods. I added normal and camera_space_position to Vector3D as Point3DH, and it was a lot of work to do, for I had to do everything needed for these two points everywhere, for example, interpolate in clipping and rendering and so on.

It basically works fine, except that there are some flaws. When using Gouraud and Phong shading the color of some pixels may be weird. But overall I think it's all fine. 